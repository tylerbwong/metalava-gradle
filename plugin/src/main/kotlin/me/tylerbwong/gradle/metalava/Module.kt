package me.tylerbwong.gradle.metalava

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.io.File
import java.util.Locale

internal sealed class Module {
    /**
     * The bootclasspath to be passed to metalava when it parses the source files.
     *
     * @return The bootclasspath for to be passed to metalava. May be empty.
     *
     * @see compileClasspath
     */
    open val bootClasspath: Collection<File> = emptyList()

    /**
     * Returns directories and/or files containing classes that are to be on the classpath when
     * metalava parses the source files.
     *
     * Depending on the Platform, different classpaths may be required, and these can be identified
     * with the [variant] parameter.
     *
     * @param variant Identifies a classpath retained by the Module. If `null` then the default
     * classpath for that Platform is used.
     * @return The classpath for to be passed to metalava. May be empty.
     */
    abstract fun compileClasspath(project: Project, variant: String? = null): FileCollection

    /**
     * The source sets to be passed to metalava to execute on. Will exclude test sources.
     */
    abstract fun sourceSets(project: Project, variant: String? = null): FileCollection

    class Android(private val extension: LibraryExtension) : Module() {
        /**
         * The list of available library variants to be passed into [compileClasspath] so as to
         * filter its output.
         *
         * @see compileClasspath
         */
        val libraryVariants: Collection<String>
            get() = extension.libraryVariants.map { it.name }

        override val bootClasspath: Collection<File>
            get() = extension.bootClasspath

        override fun compileClasspath(project: Project, variant: String?): FileCollection {
            require(variant != null) { "The compileClasspath variant cannot be null." }
            require(libraryVariants.contains(variant)) { "Unexpected compileClasspath variant. Got $variant." }
            return extension.libraryVariants.find { it.name.equals(variant) }
                ?.getCompileClasspath(null)
                ?.filter { it.exists() } ?: project.files()
        }

        override fun sourceSets(project: Project, variant: String?): FileCollection {
            require(variant != null) { "The sourceSet variant cannot be null." }
            require(libraryVariants.contains(variant)) { "Unexpected sourceSet variant. Got $variant." }
            val libraryVariant = extension.libraryVariants.find { it.name.equals(variant) }
            require(libraryVariant != null) { "Could not find library variant for $variant." }
            return project.files(
                libraryVariant.sourceSets
                    .filterNot {
                        it.name
                            .lowercase(Locale.getDefault())
                            .contains(SourceSet.TEST_SOURCE_SET_NAME)
                    }
                    .flatMap { it.javaDirectories + it.kotlinDirectories },
            )
        }
    }

    class Kotlin(
        javaExtension: JavaPluginExtension,
        private val kotlinExtension: KotlinProjectExtension,
    ) : Module() {

        private val javaModule = Java(javaExtension)

        private val KotlinProjectExtension.targets: Iterable<KotlinTarget>
            get() = when (this) {
                is KotlinSingleTargetExtension<*> -> listOf(target)
                is KotlinMultiplatformExtension -> targets
                else -> error("Unexpected 'kotlin' extension $this")
            }

        override val bootClasspath: Collection<File>
            get() = javaModule.bootClasspath

        override fun compileClasspath(project: Project, variant: String?): FileCollection {
            return javaModule.compileClasspath(project, variant) + (
                kotlinExtension.targets
                    .flatMap { it.compilations }
                    .filter {
                        it.defaultSourceSet.name.contains(
                            SourceSet.MAIN_SOURCE_SET_NAME,
                            ignoreCase = true,
                        )
                    }
                    .map { it.compileDependencyFiles }
                    .reduceOrNull(FileCollection::plus)
                    ?.filter { it.exists() && it.checkDirectory(listOf(".jar", ".class")) }
                    ?: project.files()
                )
        }

        override fun sourceSets(project: Project, variant: String?): FileCollection {
            return project.files(
                javaModule.sourceSets(project, variant) + kotlinExtension.sourceSets
                    .filterNot {
                        it.name
                            .lowercase(Locale.getDefault())
                            .contains(SourceSet.TEST_SOURCE_SET_NAME)
                    }
                    .flatMap { it.kotlin.sourceDirectories },
            )
        }
    }

    class Java(private val extension: JavaPluginExtension) : Module() {
        override val bootClasspath: Collection<File> by lazy {
            listOfNotNull(
                System.getProperty("sun.boot.class.path")
                    ?.let { File(it) }
                    ?.takeIf { it.exists() },
                File(System.getProperty("java.home"))
                    .resolve("jre${File.separator}lib${File.separator}rt.jar")
                    .takeIf { it.exists() },
            )
        }

        override fun compileClasspath(project: Project, variant: String?): FileCollection {
            return extension.sourceSets
                .filter { it.name.contains(SourceSet.MAIN_SOURCE_SET_NAME, ignoreCase = true) }
                .map { it.compileClasspath }
                .reduceOrNull(FileCollection::plus)
                ?.filter { it.exists() && it.checkDirectory(listOf(".jar", ".class")) }
                ?: project.files()
        }

        override fun sourceSets(project: Project, variant: String?): FileCollection {
            return project.files(
                extension.sourceSets
                    .filterNot {
                        it.name
                            .lowercase(Locale.getDefault())
                            .contains(SourceSet.TEST_SOURCE_SET_NAME)
                    }
                    .flatMap { it.java.srcDirs },
            )
        }
    }

    class Composite(private val modules: List<Module>) : Module() {
        override val bootClasspath: Collection<File>
            get() = modules.flatMap { it.bootClasspath }

        override fun compileClasspath(project: Project, variant: String?): FileCollection {
            return modules
                .map { it.compileClasspath(project, variant) }
                .reduceOrNull(FileCollection::plus)
                ?.filter { it.exists() } ?: project.files()
        }

        override fun sourceSets(project: Project, variant: String?): FileCollection {
            return modules
                .map { it.sourceSets(project, variant) }
                .reduceOrNull(FileCollection::plus) ?: project.files()
        }

        internal inline fun <reified T : Module> extract(): T? = modules.firstOrNull {
            it is T
        } as? T
    }

    companion object {
        internal val Project.module: Module?
            get() {
                // Use findByName to avoid requiring consumers to have the Android Gradle plugin
                // in their classpath when applying this plugin to a non-Android project
                val androidModule = extensions.findByName("android")
                    ?.takeIf { it is LibraryExtension }
                    ?.let { Android(it as LibraryExtension) }

                val javaPluginExtension = extensions.findByType<JavaPluginExtension>()

                val kotlinModule = extensions.findByName("kotlin")
                    ?.takeIf { it is KotlinProjectExtension && javaPluginExtension != null }
                    ?.let { Kotlin(javaPluginExtension!!, it as KotlinProjectExtension) }

                val javaModule = javaPluginExtension?.let { Java(it) }

                val modules = listOfNotNull(androidModule, kotlinModule, javaModule)
                return modules.takeIf { it.isNotEmpty() }?.let { Composite(it) }
            }

        internal fun File.checkDirectory(validExtensions: Collection<String>): Boolean {
            return if (isFile) {
                validExtensions.any { name.endsWith(it, ignoreCase = true) }
            } else {
                listFiles()?.all { it.checkDirectory(validExtensions) } ?: false
            }
        }
    }
}

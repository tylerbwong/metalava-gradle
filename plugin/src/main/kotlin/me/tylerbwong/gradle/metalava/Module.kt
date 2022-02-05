package me.tylerbwong.gradle.metalava

import com.android.build.gradle.LibraryExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

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
     *
     * @see variants
     */
    abstract fun compileClasspath(variant: String? = null): Collection<File>

    /**
     * The list of available variants to be passed into [compileClasspath] so as to filter
     * its output. Can be null.
     *
     * @see compileClasspath
     */
    open val variants: Collection<String>? = null

    class Android(private val extension: LibraryExtension) : Module() {
        override val bootClasspath: Collection<File>
            get() = extension.bootClasspath
        override fun compileClasspath(variant: String?): Collection<File> {
            require(variant != null) { "The compileClasspath variant cannot be null." }
            require(variants.contains(variant)) { "Unexpected compileClasspath variant. Got $variant." }
            return extension.libraryVariants.find { it.name.equals(variant) }!!.getCompileClasspath(null).files
        }
        override val variants: Collection<String>
            get() = extension.libraryVariants.map { it.name }
    }

    class Multiplatform(private val extension: KotlinMultiplatformExtension) : Module() {
        override fun compileClasspath(variant: String?): Collection<File> {
            require(variant == null) { "Unexpected compileClasspath variant. Got $variant." }
            return extension.targets
                .flatMap { it.compilations }
                .filter { it.defaultSourceSetName.contains("main", ignoreCase = true) }
                .flatMap { it.compileDependencyFiles }
                .filter { it.exists() && it.checkDirectory(listOf(".jar", ".class")) }
        }
    }

    class Java(private val extension: JavaPluginExtension) : Module() {
        override val bootClasspath: Collection<File>
            get() = File(System.getProperty("java.home")).walkTopDown()
                .toList()
                .filter { it.exists() && it.name == "rt.jar" }
        override fun compileClasspath(variant: String?): Collection<File> {
            require(variant == null) { "Unexpected compileClasspath variant. Got $variant." }
            return extension.sourceSets
                .filter { it.name.contains("main", ignoreCase = true) }
                .flatMap { it.compileClasspath }
                .filter { it.exists() && it.checkDirectory(listOf(".jar", ".class")) }
        }
    }

    companion object {
        internal val Project.module: Module
            get() {
                // Use findByName to avoid requiring consumers to have the Android Gradle plugin
                // in their classpath when applying this plugin to a non-Android project
                val libraryExtension = extensions.findByName("android")
                val multiplatformExtension = extensions.findByName("kotlin")
                val javaPluginExtension = extensions.findByType<JavaPluginExtension>()
                return when {
                    libraryExtension != null && libraryExtension is LibraryExtension -> Android(libraryExtension)
                    multiplatformExtension != null && multiplatformExtension is KotlinMultiplatformExtension -> Multiplatform(multiplatformExtension)
                    javaPluginExtension != null -> Java(javaPluginExtension)
                    else -> throw GradleException("This module is currently not supported by the Metalava plugin.")
                }
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

package me.tylerbwong.gradle.metalava

import com.android.build.gradle.LibraryExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.findPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

internal sealed class Module {

    open val bootClasspath: Collection<File> = emptyList()
    abstract val compileClasspath: Collection<File>

    class Android(private val extension: LibraryExtension) : Module() {
        override val bootClasspath: Collection<File>
            get() = extension.bootClasspath
        override val compileClasspath: Collection<File>
            get() = extension.libraryVariants.find {
                it.name.contains("debug", ignoreCase = true)
            }?.getCompileClasspath(null)?.filter { it.exists() }?.files ?: emptyList()
    }

    class Multiplatform(private val extension: KotlinMultiplatformExtension) : Module() {
        override val compileClasspath: Collection<File>
            get() = extension.targets
                .flatMap { it.compilations }
                .filter { it.defaultSourceSetName.contains("main", ignoreCase = true) }
                .flatMap { it.compileDependencyFiles }
                .filter { it.exists() && it.checkDirectory(listOf(".jar", ".class")) }
    }

    class Java(private val convention: JavaPluginConvention) : Module() {
        override val compileClasspath: Collection<File>
            get() = convention.sourceSets
                .filter { it.name.contains("main", ignoreCase = true) }
                .flatMap { it.compileClasspath }
                .filter { it.exists() && it.checkDirectory(listOf(".jar", ".class")) }
    }

    companion object {
        internal val Project.module: Module
            get() {
                // Use findByName to avoid requiring consumers to have the Android Gradle plugin
                // in their classpath when applying this plugin to a non-Android project
                val libraryExtension = extensions.findByName("android")
                val multiplatformExtension = extensions.findByType<KotlinMultiplatformExtension>()
                val javaPluginConvention = convention.findPlugin<JavaPluginConvention>()
                return when {
                    libraryExtension != null && libraryExtension is LibraryExtension -> Android(libraryExtension)
                    multiplatformExtension != null -> Multiplatform(multiplatformExtension)
                    javaPluginConvention != null -> Java(javaPluginConvention)
                    else -> throw GradleException("This module is currently not supported by the Metalava plugin")
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

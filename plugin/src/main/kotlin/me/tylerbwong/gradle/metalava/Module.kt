package me.tylerbwong.gradle.metalava

import com.android.build.gradle.LibraryExtension
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

internal sealed class Module {

    open val bootClasspath: Collection<File> = emptyList()
    abstract val compileClasspath: Collection<File>

    class Android(private val extension: LibraryExtension, private val variantName: String) : Module() {
        override val bootClasspath: Collection<File>
            get() = extension.bootClasspath
        override val compileClasspath: Collection<File>
            get() = extension.libraryVariants.find {
                it.name.contains(variantName, ignoreCase = true)
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

    class Java(private val extension: JavaPluginExtension) : Module() {
        override val bootClasspath: Collection<File>
            get() = File(System.getProperty("java.home")).walkTopDown()
                .toList()
                .filter { it.exists() && it.name == "rt.jar" }
        override val compileClasspath: Collection<File>
            get() = extension.sourceSets
                .filter { it.name.contains("main", ignoreCase = true) }
                .flatMap { it.compileClasspath }
                .filter { it.exists() && it.checkDirectory(listOf(".jar", ".class")) }
    }

    companion object {
        internal fun Project.module(extension: MetalavaExtension): Module? {
            // Use findByName to avoid requiring consumers to have the Android Gradle plugin
            // in their classpath when applying this plugin to a non-Android project
            val libraryExtension = extensions.findByName("android")
            val multiplatformExtension = extensions.findByName("kotlin")
            val javaPluginExtension = extensions.findByType<JavaPluginExtension>()
            return when {
                libraryExtension != null && libraryExtension is LibraryExtension -> Android(libraryExtension, extension.androidVariantName)
                multiplatformExtension != null && multiplatformExtension is KotlinMultiplatformExtension -> Multiplatform(multiplatformExtension)
                javaPluginExtension != null -> Java(javaPluginExtension)
                else -> if (extension.ignoreUnsupportedModules.not()) {
                    throw GradleException("This module is currently not supported by the Metalava plugin")
                } else null
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

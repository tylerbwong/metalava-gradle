package me.tylerbwong.gradle.metalava.plugin

import com.android.build.gradle.LibraryExtension
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import me.tylerbwong.gradle.task.DownloadFileTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.findByType
import java.io.File
import java.util.Locale

class MetalavaPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create("metalava", MetalavaExtension::class.java)
            afterEvaluate {
                val downloadMetalavaTaskProvider = tasks.register(
                    "downloadMetalavaJar",
                    DownloadFileTask::class.java
                ) {
                    url.set("https://storage.googleapis.com/android-ci/metalava-full-1.3.0-SNAPSHOT.jar")
                    output.set(layout.buildDirectory.file("${rootProject.buildDir}/metalava/metalava.jar"))
                }

                // TODO Support non-Android projects
                val libraryExtension = extensions.findByType<LibraryExtension>()
                    ?: throw GradleException("Metalava not available for non-android modules yet")
                val debugVariant = libraryExtension.libraryVariants.find {
                    it.name.contains("debug", ignoreCase = true)
                } ?: throw GradleException("Metalava not available for non-debug variants")

                tasks.register("metalavaSignature", JavaExec::class.java) {
                    @Suppress("UnstableApiUsage")
                    classpath(
                        extension.metalavaJarPath?.let { files(it) }
                            ?: downloadMetalavaTaskProvider.flatMap { it.output.asFile }
                    )

                    val androidClasspath = libraryExtension.bootClasspath
                    val externalClasspath = debugVariant.getCompileClasspath(null).filter { it.exists() }
                    val fullClasspath = (androidClasspath + externalClasspath).joinToString(":")
                    val sources = file("src").walk()
                        .maxDepth(2)
                        .onEnter { !it.name.toLowerCase(Locale.getDefault()).contains("test") }
                        .filter { it.isDirectory && (it.name == "java" || it.name == "kotlin") }
                        .toList()

                    val hides = sources.flatMap { file ->
                        file.walk().filter { it.isDirectory && it.name == "internal" }.toList()
                    }.map {
                        it.relativeTo(projectDir).path
                            .split(File.separator)
                            .drop(3)
                            .joinToString(".")
                    }.distinct()

                    val sourcePaths = listOf("--source-path") + sources.joinToString(":")
                    val hidePackages = hides.flatMap { listOf("--hide-package", it) } +
                        extension.hidePackages.map { "--hide-package $it" }
                    val hideAnnotations = extension.hideAnnotations.map { "--hide-annotation $it " }

                    val args = listOf(
                        extension.documentation,
                        "--no-banner",
                        "--format=${extension.format}",
                        "${extension.signature}", extension.outputFileName,
                        "--classpath", fullClasspath,
                        "--output-kotlin-nulls=${if (extension.shouldOutputKotlinNulls) "yes" else "no"}",
                        "--output-default-values=${if (extension.shouldOutputDefaultValues) "yes" else "no"}",
                        "--omit-common-packages=${if (extension.shouldOmitCommonPackages) "yes" else "no"}"
                    ) + sourcePaths + hidePackages + hideAnnotations

                    isIgnoreExitValue = true
                    setArgs(args)
                }
            }
        }
    }
}

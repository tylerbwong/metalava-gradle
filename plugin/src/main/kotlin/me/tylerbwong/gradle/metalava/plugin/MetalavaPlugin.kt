package me.tylerbwong.gradle.metalava.plugin

import com.android.build.gradle.LibraryExtension
import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import me.tylerbwong.gradle.task.DownloadTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.findPlugin
import java.io.File
import java.util.Locale

/**
 * This plugin will register two tasks on modules that it is applied to.
 *
 * 1. downloadMetalavaJar - Downloads and caches the Metalava JAR in the root project's build folder if one does not
 * exist at the same location already.
 * 2. metalavaSignature - Executes the Metalava JAR with the following arguments configured in [MetalavaExtension]. If
 * no custom JAR location is provided, it will use the result of the download task.
 */
class MetalavaPlugin : Plugin<Project> {

    private val Boolean.flagValue: String get() = if (this) "yes" else "no"

    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create("metalava", MetalavaExtension::class.java)
            afterEvaluate {
                val downloadMetalavaJarTaskProvider = registerDownloadMetalavaJarTask()
                val libraryExtension = extensions.findByType<LibraryExtension>()
                val javaPluginConvention = convention.findPlugin<JavaPluginConvention>()
                val module = when {
                    libraryExtension != null -> Module.Android(libraryExtension)
                    javaPluginConvention != null -> Module.Java(javaPluginConvention)
                    else -> throw GradleException("This module is currently not supported by the Metalava plugin")
                }
                registerMetalavaSignatureTask(extension, module, downloadMetalavaJarTaskProvider)
            }
        }
    }

    private fun Project.registerDownloadMetalavaJarTask(): TaskProvider<DownloadTask> {
        return tasks.register(
            "downloadMetalavaJar",
            DownloadTask::class.java
        ) {
            description = "Downloads a Metalava JAR to the root project build folder."
            url.set(METALAVA_URL)
            output.set(layout.buildDirectory.file("${rootProject.buildDir}$JAR_LOCATION"))
        }
    }

    private fun Project.registerMetalavaSignatureTask(
        extension: MetalavaExtension,
        module: Module,
        downloadMetalavaJarTaskProvider: TaskProvider<DownloadTask>
    ) {
        tasks.register("metalavaSignature", JavaExec::class.java) {
            group = "documentation"
            description = "Generates a Metalava signature descriptor file."
            @Suppress("UnstableApiUsage")
            classpath(
                extension.metalavaJarPath?.let { files(it) }
                    ?: downloadMetalavaJarTaskProvider.flatMap { it.output.asFile }
            )
            val fullClasspath = (module.bootClasspath + module.compileClasspath).joinToString(":")
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

            val args: List<String> = listOf(
                "${extension.documentation}",
                "--no-banner",
                "--no-color",
                "--format=${extension.format}",
                "${extension.signature}", extension.filename,
                "--java-source", "${extension.javaSourceLevel}",
                "--classpath", fullClasspath,
                "--output-kotlin-nulls=${extension.shouldOutputKotlinNulls.flagValue}",
                "--output-default-values=${extension.shouldOutputDefaultValues.flagValue}",
                "--omit-common-packages=${extension.shouldOmitCommonPackages.flagValue}",
                "--include-signature-version=${extension.shouldIncludeSignatureVersion.flagValue}"
            ) + sourcePaths + hidePackages + hideAnnotations

            isIgnoreExitValue = true
            setArgs(args)
        }
    }

    companion object {
        private const val METALAVA_URL = "https://storage.googleapis.com/android-ci/metalava-full-1.3.0-SNAPSHOT.jar"
        private const val JAR_LOCATION = "/metalava/metalava.jar"
    }
}

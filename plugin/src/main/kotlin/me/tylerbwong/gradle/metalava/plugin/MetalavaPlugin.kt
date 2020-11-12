package me.tylerbwong.gradle.metalava.plugin

import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.Module.Companion.module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.repositories
import java.io.File
import java.util.Locale

/**
 * This plugin will register a helper task `metalavaSignature` on modules that it is applied to. The task runs Metalava
 * with the following arguments configured in [MetalavaExtension].
 */
class MetalavaPlugin : Plugin<Project> {

    private val Boolean.flagValue: String get() = if (this) "yes" else "no"

    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create("metalava", MetalavaExtension::class.java)
            afterEvaluate {
                registerMetalavaSignatureTask(extension, module)
            }
        }
    }

    private fun Project.registerMetalavaSignatureTask(extension: MetalavaExtension, module: Module) {
        tasks.register("metalavaSignature", JavaExec::class.java) {
            group = "documentation"
            description = "Generates a Metalava signature descriptor file."
            main = "com.android.tools.metalava.Driver"
            classpath(extension.metalavaJarPath?.let { files(it) } ?: getMetalavaClasspath(extension.version))

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

    private fun Project.getMetalavaClasspath(version: String): FileCollection {
        repositories {
            google()
        }
        val configuration = configurations.maybeCreate("metalava").apply {
            val dependency = this@getMetalavaClasspath.dependencies.create(
                "com.android.tools.metalava:metalava:$version"
            )
            dependencies.add(dependency)
        }
        return files(configuration)
    }
}

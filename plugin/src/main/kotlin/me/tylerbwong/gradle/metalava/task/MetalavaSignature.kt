package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskProvider
import java.io.File
import java.util.Locale

internal object MetalavaSignature : MetalavaTaskContainer() {
    fun registerMetalavaSignatureTask(
        project: Project,
        name: String,
        description: String,
        extension: MetalavaExtension,
        module: Module,
        filename: String = extension.filename
    ): TaskProvider<JavaExec> {
        return with(project) {
            tasks.register(name, JavaExec::class.java) {
                group = "documentation"
                this.description = description
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
                    extension.hiddenPackages.map { "--hide-package $it" }
                val hideAnnotations = extension.hiddenAnnotations.map { "--hide-annotation $it " }

                val args: List<String> = listOf(
                    "${extension.documentation}",
                    "--no-banner",
                    "--no-color",
                    "--format=${extension.format}",
                    "${extension.signature}", filename,
                    "--java-source", "${extension.javaSourceLevel}",
                    "--classpath", fullClasspath,
                    "--output-kotlin-nulls=${extension.outputKotlinNulls.flagValue}",
                    "--output-default-values=${extension.outputDefaultValues.flagValue}",
                    "--omit-common-packages=${extension.omitCommonPackages.flagValue}",
                    "--include-signature-version=${extension.includeSignatureVersion.flagValue}"
                ) + sourcePaths + hidePackages + hideAnnotations

                isIgnoreExitValue = true
                setArgs(args)
            }
        }
    }
}

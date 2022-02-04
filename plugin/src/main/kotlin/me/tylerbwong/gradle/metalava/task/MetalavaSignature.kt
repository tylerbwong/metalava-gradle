package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskProvider
import java.io.File
import java.util.Locale

internal object MetalavaSignature : MetalavaTaskContainer() {
    private val sourceLanguageDirectoryNames = listOf("java", "kotlin")

    fun registerMetalavaSignatureTask(
        project: Project,
        name: String,
        description: String,
        extension: MetalavaExtension,
        module: Module,
        taskGroup: String? = "documentation",
        filename: String = extension.filename
    ): TaskProvider<JavaExec> {
        return with(project) {
            tasks.register(name, JavaExec::class.java) {
                require(extension.sourcePaths.isNotEmpty()) { "sourcePaths cannot be empty." }
                if (taskGroup != null) {
                    group = taskGroup
                }
                this.description = description
                mainClass.set("com.android.tools.metalava.Driver")
                classpath(extension.metalavaJarPath?.let { files(it) } ?: getMetalavaClasspath(extension.version))

                val sourceFiles = extension.sourcePaths.flatMap { sourcePath ->
                    file(sourcePath)
                        .walk()
                        .onEnter {
                            extension.ignoreSourcePaths.none { ignoredDirectoryName ->
                                ignoredDirectoryName.equals(it.name, ignoreCase = true)
                            }
                        }
                        .filter { it.isDirectory }
                        .filter { it.name.toLowerCase(Locale.getDefault()) in sourceLanguageDirectoryNames }
                        .toList()
                }

                inputs.files(sourceFiles)
                inputs.property("documentation", extension.documentation)
                inputs.property("format", extension.format)
                inputs.property("signature", extension.signature)
                inputs.property("javaSourceLevel", extension.javaSourceLevel)
                inputs.property("outputKotlinNulls", extension.outputKotlinNulls.flagValue)
                inputs.property("outputDefaultValues", extension.outputDefaultValues.flagValue)
                inputs.property("includeSignatureVersion", extension.includeSignatureVersion.flagValue)
                inputs.property("hiddenPackages", extension.hiddenPackages)
                inputs.property("hiddenAnnotations", extension.hiddenAnnotations)
                outputs.file(filename)

                doFirst {
                    val fullClasspath = (module.bootClasspath + module.compileClasspath).joinToString(File.pathSeparator)

                    val sourcePaths = listOf("--source-path") + sourceFiles.joinToString(File.pathSeparator)

                    val hidePackages =
                        extension.hiddenPackages.flatMap { listOf("--hide-package", it) }
                    val hideAnnotations =
                        extension.hiddenAnnotations.flatMap { listOf("--hide-annotation", it) }

                    val args: List<String> = listOf(
                        "${extension.documentation}",
                        "--no-banner",
                        "--format=${extension.format}",
                        "${extension.signature}", filename,
                        "--java-source", "${extension.javaSourceLevel}",
                        "--classpath", fullClasspath,
                        "--output-kotlin-nulls=${extension.outputKotlinNulls.flagValue}",
                        "--output-default-values=${extension.outputDefaultValues.flagValue}",
                        "--include-signature-version=${extension.includeSignatureVersion.flagValue}"
                    ) + sourcePaths + hidePackages + hideAnnotations

                    isIgnoreExitValue = true
                    setArgs(args)
                }
            }
        }
    }
}

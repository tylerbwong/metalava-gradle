package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskProvider
import java.io.File

internal object MetalavaSignature : MetalavaTaskContainer() {
    fun registerMetalavaSignatureTask(
        project: Project,
        extension: MetalavaExtension,
        module: Module,
        taskName: String,
        taskDescription: String,
        taskGroup: String? = "documentation",
        variantName: String? = null,
        filename: String = extension.filename
    ): TaskProvider<JavaExec> {
        return with(project) {
            tasks.register(getFullTaskname(taskName, variantName), JavaExec::class.java) {
                require(extension.sourcePaths.isNotEmpty()) { "sourcePaths cannot be empty." }

                if (taskGroup != null) {
                    group = taskGroup
                }
                description = taskDescription
                mainClass.set("com.android.tools.metalava.Driver")
                classpath(extension.metalavaJarPath?.let { files(it) } ?: getMetalavaClasspath(extension.version))

                val compileClasspath = module.compileClasspath(variantName)
                val sourceFiles = extension.sourcePaths.map { file(it) }

                inputs.files(compileClasspath)
                inputs.files(sourceFiles)
                inputs.files(extension.sourcePathsFileCollection)
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
                    val fullClasspath = (module.bootClasspath + compileClasspath).joinToString(File.pathSeparator)

                    val sourcePaths = (
                        sourceFiles +
                            extension.sourcePathsFileCollection.elements.get().map { it.asFile }
                                .also { files ->
                                    val nonExistentDirs = files.filter { !it.exists() }
                                    require(nonExistentDirs.isEmpty()) {
                                        "Specified source path doesn't exist: $nonExistentDirs"
                                    }
                                    val nonDirectories = files.filter { !it.isDirectory }
                                    require(nonDirectories.isEmpty()) {
                                        "Specified source path isn't a directory: $nonDirectories"
                                    }
                                }
                        )
                        .joinToString(File.pathSeparator)

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
                        "--include-signature-version=${extension.includeSignatureVersion.flagValue}",
                        "--source-path", sourcePaths,
                    ) + hidePackages + hideAnnotations

                    isIgnoreExitValue = true
                    setArgs(args)
                }
            }
        }
    }
}

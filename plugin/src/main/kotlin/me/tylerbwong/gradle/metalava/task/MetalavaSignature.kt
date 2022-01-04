package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskProvider
import java.io.File
import java.util.Locale

private val sourceLanguageDirectoryNames = listOf("java", "kotlin")

// Predicate to determine whether a given directory is to be excluded from metalava API processing.
// Uses String.contains with case-insensitive matching for comparison.
private val isDirectoryToBeIgnored = { directoryName: String, ignoredDirectoryNames: Set<String> ->
    var isDirectoryToBeIgnored = false
    run loop@{
        ignoredDirectoryNames.forEach {
            if (directoryName.contains(it, ignoreCase = true)) {
                    isDirectoryToBeIgnored = true
                    return@loop
                }
        }
    }
    isDirectoryToBeIgnored
}

// Predicate to determine whether a given directory is to be excluded from metalava API processing.
// Uses String.equals for comparison.
private val isDirectoryToBeIgnoredStrict = { directoryName: String, ignoredDirectoryNames: Set<String> ->
    var isDirectoryToBeIgnored = false
    run loop@{
        ignoredDirectoryNames.forEach {
            if (directoryName == it) {
                isDirectoryToBeIgnored = true
                return@loop
            }
        }
    }
    isDirectoryToBeIgnored
}

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
                require(extension.sourcePaths.isNotEmpty()) {"sourcePaths cannot be empty."}

                group = "documentation"
                this.description = description
                mainClass.set("com.android.tools.metalava.Driver")
                classpath(extension.metalavaJarPath?.let { files(it) } ?: getMetalavaClasspath(extension.version))

                val directoryCompareFunction = if (extension.ignoreSourcePathsExactMatch) isDirectoryToBeIgnoredStrict else isDirectoryToBeIgnored

                val sourceFiles = mutableSetOf<File>()
                for (directory in extension.sourcePaths) {
                    sourceFiles.addAll(
                        file(directory)
                            .walk()
                            .onEnter { it.name.toLowerCase(Locale.getDefault()) in sourceLanguageDirectoryNames }
                            .onEnter { !directoryCompareFunction(it.name, extension.ignoreSourcePaths) }
                            .filter { it.isDirectory }
                            .toList()
                    )
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

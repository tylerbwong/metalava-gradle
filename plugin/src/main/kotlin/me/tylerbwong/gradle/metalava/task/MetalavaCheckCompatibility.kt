package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

internal object MetalavaCheckCompatibility : MetalavaTaskContainer() {
    fun registerMetalavaCheckCompatibilityTask(
        project: Project,
        extension: MetalavaExtension,
        module: Module,
        taskName: String,
        taskDescription: String,
        variantName: String?
    ) {
        with(project) {
            val tempFilename = layout.buildDirectory.file("metalava/current.txt").get().asFile.absolutePath
            val generateTempMetalavaSignatureTask = MetalavaSignature.registerMetalavaSignatureTask(
                project = this,
                extension = extension,
                module = module,
                taskName = "metalavaGenerateTempSignature",
                taskDescription = """
                    Generates a Metalava signature descriptor file in the project build directory for API compatibility 
                    checking.
                """.trimIndent(),
                taskGroup = null,
                variantName = variantName,
                filename = tempFilename
            )
            val checkCompatibilityTask = tasks.register(getFullTaskName(taskName, variantName), JavaExec::class.java) {
                group = "verification"
                description = taskDescription
                mainClass.set("com.android.tools.metalava.Driver")
                classpath(extension.metalavaJarPath?.let { files(it) } ?: getMetalavaClasspath(extension.version))
                dependsOn(generateTempMetalavaSignatureTask)

                // Use temp signature file for incremental Gradle task output
                // If both the current API and temp API have not changed since last run, then
                // consider this task UP-TO-DATE
                inputs.file(extension.filename)
                inputs.property("format", extension.format)
                inputs.property("inputKotlinNulls", extension.inputKotlinNulls.flagValue)
                inputs.property("releaseType", extension.releaseType)
                inputs.property("hiddenPackages", extension.hiddenPackages)
                inputs.property("hiddenAnnotations", extension.hiddenAnnotations)
                outputs.file(tempFilename)

                doFirst {
                    // TODO Consolidate flags between tasks
                    val hidePackages =
                        extension.hiddenPackages.flatMap { listOf("--hide-package", it) }
                    val hideAnnotations =
                        extension.hiddenAnnotations.flatMap { listOf("--hide-annotation", it) }

                    val args: List<String> = listOf(
                        "--no-banner",
                        "--format=${extension.format}",
                        "--source-files", tempFilename,
                        "--check-compatibility:api:${extension.releaseType}", extension.filename,
                        "--input-kotlin-nulls=${extension.inputKotlinNulls.flagValue}"
                    ) + extension.reportWarningsAsErrors.flag("--warnings-as-errors") +
                        extension.reportLintsAsErrors.flag("--lints-as-errors") + hidePackages + hideAnnotations

                    isIgnoreExitValue = false
                    setArgs(args)
                }
            }
            // Projects that apply this plugin should include API compatibility checking as part of their regular checks.
            // However, it may be that source dirs are generated only after some other build phase, and so the
            // association with 'check' should be configurable.
            if (extension.enforceCheck) afterEvaluate { tasks.findByName("check")?.dependsOn(checkCompatibilityTask) }
        }
    }
}

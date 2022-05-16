package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.Module.Companion.getTemporarySignatureFilePath
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.Project
import java.io.File
import java.io.FileReader

internal object MetalavaCompareApiWithCurrent : MetalavaTaskContainer() {
    fun registerMetalavaCompareApiWithCurrent(
        project: Project,
        extension: MetalavaExtension,
        @Suppress("UNUSED_PARAMETER") module: Module,
        taskName: String,
        taskDescription: String,
        variantName: String?
    ) {
        with(project) {
            tasks.register(getFullTaskName(taskName, variantName)) {
                group = "verification"
                description = taskDescription
                dependsOn(project.tasks.findByName(getFullTaskName("metalavaGenerateTempSignature", variantName)))

                val tempSignatureFilename = project.getTemporarySignatureFilePath()

                // Use temp signature file for incremental Gradle task output
                // If both the current API and temp API have not changed since last run, then
                // consider this task UP-TO-DATE
                inputs.file(extension.filename)
                inputs.property("format", extension.format)
                inputs.property("inputKotlinNulls", extension.inputKotlinNulls.flagValue)
                inputs.property("apiType", extension.apiType)
                inputs.property("hiddenPackages", extension.hiddenPackages)
                inputs.property("hiddenAnnotations", extension.hiddenAnnotations)
                outputs.file(tempSignatureFilename)

                doFirst {
                    val signatureFile = layout.projectDirectory.file(extension.filename).asFile
                    require(signatureFile.exists()) { "MetalavaCompareApiWithCurrent 1 Couldn't find \"${signatureFile}\"." }
                    require(File(tempSignatureFilename).exists()) { "MetalavaCompareApiWithCurrent 2 Couldn't find \"${tempSignatureFilename}\"." }

                    var existingText: String
                    FileReader(signatureFile).use { existingText = it.readText() }

                    var tempFileText: String
                    FileReader(tempSignatureFilename).use { tempFileText = it.readText() }

                    check(existingText == tempFileText) {
                        "Signature files don't match. Compare \"${signatureFile.absolutePath}\" and \"$tempSignatureFilename\"."
                    }
                }
            }
        }
    }
}

package me.tylerbwong.gradle.metalava.plugin

import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.Module.Companion.module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import me.tylerbwong.gradle.metalava.task.MetalavaCheckCompatibility
import me.tylerbwong.gradle.metalava.task.MetalavaGenerateSignatureTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class MetalavaPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create("metalava", MetalavaExtension::class.java)
            afterEvaluate {
                val currentModule = module
                if (currentModule != null) {
                    if (currentModule is Module.Android) {
                        currentModule.libraryVariants.forEach {
                            createMetalavaTasks(this, extension, currentModule, it)
                        }
                    } else {
                        createMetalavaTasks(this, extension, currentModule)
                    }
                } else {
                    logger.warn("Module $name is not supported by the Metalava Gradle plugin")
                }
            }
        }
    }

    private fun createMetalavaTasks(
        project: Project,
        metalavaExtension: MetalavaExtension,
        module: Module,
        variantName: String? = null,
    ) {
        MetalavaGenerateSignatureTask.registerMetalavaSignatureTask(
            project = project,
            extension = metalavaExtension,
            module = module,
            variantName = variantName,
        )

        MetalavaCheckCompatibility.registerMetalavaCheckCompatibilityTask(
            project = project,
            extension = metalavaExtension,
            module = module,
            taskName = "metalavaCheckCompatibility",
            taskDescription = "Checks API compatibility between the code base and the current or release API.",
            variantName = variantName
        )
    }
}

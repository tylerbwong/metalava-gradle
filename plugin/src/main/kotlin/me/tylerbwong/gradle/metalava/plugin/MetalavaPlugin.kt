package me.tylerbwong.gradle.metalava.plugin

import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.Module.Companion.module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import me.tylerbwong.gradle.metalava.task.MetalavaCheckCompatibilityTask
import me.tylerbwong.gradle.metalava.task.MetalavaGenerateSignatureTask
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class MetalavaPlugin : Plugin<Project> {
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
        val generateSignatureTask = MetalavaGenerateSignatureTask.register(
            project = project,
            extension = metalavaExtension,
            module = module,
            variantName = variantName,
        )

        val outputSignatureFileProvider = generateSignatureTask
            .flatMap { project.layout.projectDirectory.file(it.filename) }
        metalavaExtension.outputSignatureFileProperty.set(outputSignatureFileProvider)

        val checkCompatibilityTask = MetalavaCheckCompatibilityTask.register(
            project = project,
            extension = metalavaExtension,
            module = module,
            variantName = variantName,
        )

        // Projects that apply this plugin should include API compatibility checking as part of their regular checks.
        // However, it may be that source dirs are generated only after some other build phase, and so the
        // association with 'check' should be configurable.
        project.tasks.named("check").configure {
            if (metalavaExtension.enforceCheck.get()) {
                dependsOn(checkCompatibilityTask)
            }
        }
    }
}

package me.tylerbwong.gradle.metalava.plugin

import me.tylerbwong.gradle.metalava.Module.Companion.module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import me.tylerbwong.gradle.metalava.task.MetalavaCheckCompatibility
import me.tylerbwong.gradle.metalava.task.MetalavaSignature
import org.gradle.api.Plugin
import org.gradle.api.Project

class MetalavaPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create("metalava", MetalavaExtension::class.java)
            afterEvaluate {
                val currentModule = module
                val variants = currentModule.variants ?: listOf("")
                variants.forEach {
                    MetalavaSignature.registerMetalavaSignatureTask(
                        project = this,
                        extension = extension,
                        module = currentModule,
                        taskName = "metalavaGenerateSignature",
                        taskDescription = "Generates a Metalava signature descriptor file.",
                        variantName = it.ifEmpty { null }
                    )

                    MetalavaCheckCompatibility.registerMetalavaCheckCompatibilityTask(
                        project = this,
                        extension = extension,
                        module = currentModule,
                        taskName = "metalavaCheckCompatibility",
                        taskDescription = "Checks API compatibility between the code base and the current or release API.",
                        variantName = it.ifEmpty { null }
                    )
                }
            }
        }
    }
}

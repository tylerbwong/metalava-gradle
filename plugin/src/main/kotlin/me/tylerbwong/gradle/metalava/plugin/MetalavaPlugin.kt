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
                val currentModule = module(extension) ?: return@afterEvaluate
                MetalavaSignature.registerMetalavaSignatureTask(
                    project = this,
                    name = "metalavaGenerateSignature",
                    description = "Generates a Metalava signature descriptor file.",
                    extension = extension,
                    module = currentModule
                )
                MetalavaCheckCompatibility.registerMetalavaCheckCompatibilityTask(
                    project = this,
                    extension = extension,
                    module = currentModule
                )
            }
        }
    }
}

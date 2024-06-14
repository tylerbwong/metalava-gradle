package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

@CacheableTask
internal abstract class MetalavaCheckCompatibilityTask @Inject constructor(
    objectFactory: ObjectFactory,
    workerExecutor: WorkerExecutor,
) : MetalavaGenerateSignatureTask(objectFactory, workerExecutor) {

    init {
        group = "verification"
        description = TASK_DESCRIPTION
    }

    @get:OutputFile
    abstract val tempFilename: Property<String>

    @get:Input
    abstract val apiType: Property<String>

    @get:Input
    abstract val inputKotlinNulls: Property<Boolean>

    @get:Input
    abstract val reportWarningsAsErrors: Property<Boolean>

    @get:Input
    abstract val reportLintsAsErrors: Property<Boolean>

    @TaskAction
    fun metalavaCheckCompatibilityTask() {
        metalavaGenerateSignatureInternal(filenameOverride = tempFilename.get(), awaitWork = true)
        val hidePackages = hiddenPackages.get().flatMap { listOf("--hide-package", it) }
        val hideAnnotations = hiddenAnnotations.get().flatMap { listOf("--hide-annotation", it) }

        val args: List<String> = listOf(
            "--format=${format.get()}",
            "--source-files",
            tempFilename.get(),
            "--check-compatibility:${apiType.get()}:released",
            filename.get(),
        ) + reportWarningsAsErrors.get().flag("--warnings-as-errors") + reportLintsAsErrors.get()
            .flag("--lints-as-errors") + hidePackages + hideAnnotations + arguments.get()
        executeMetalavaWork(args)
    }

    internal companion object : MetalavaTaskContainer() {
        private const val TASK_NAME = "metalavaCheckCompatibility"
        private const val TASK_DESCRIPTION =
            "Checks API compatibility between the code base and the released API."
        private const val METALAVA_CURRENT_PATH = "metalava/current.txt"

        fun register(
            project: Project,
            objectFactory: ObjectFactory,
            extension: MetalavaExtension,
            module: Module,
            variantName: String?,
        ): TaskProvider<MetalavaCheckCompatibilityTask> {
            val tempFilenameProvider = project.layout.buildDirectory
                .file(METALAVA_CURRENT_PATH).map { it.asFile.absolutePath }
            val taskName = getFullTaskName(TASK_NAME, variantName)
            val metalavaClasspath = project.getMetalavaClasspath(
                objectFactory,
                jarPath = extension.metalavaJarPath.get().ifEmpty { null },
                version = extension.version.get(),
            )
            val bootClasspathProvider = project.provider { module.bootClasspath }
            return project.tasks.register<MetalavaCheckCompatibilityTask>(taskName) {
                this.metalavaClasspath.from(metalavaClasspath)
                tempFilename.set(tempFilenameProvider)
                sourceSets.setFrom(
                    module.sourceSets(
                        project,
                        variantName,
                    ) + extension.additionalSourceSets - extension.excludedSourceSets,
                )
                filename.set(extension.filename)
                shouldRunGenerateSignature.set(false)
                bootClasspath.from(bootClasspathProvider)
                compileClasspath.from(module.compileClasspath(project, variantName))
                format.set(extension.format)
                signature.set(extension.signature)
                javaSourceLevel.set(extension.javaSourceLevel)
                hiddenPackages.set(extension.hiddenPackages)
                hiddenAnnotations.set(extension.hiddenAnnotations)
                apiType.set(extension.apiType)
                inputKotlinNulls.set(extension.inputKotlinNulls)
                reportWarningsAsErrors.set(extension.reportWarningsAsErrors)
                reportLintsAsErrors.set(extension.reportLintsAsErrors)
                arguments.set(extension.arguments)
            }
        }
    }
}

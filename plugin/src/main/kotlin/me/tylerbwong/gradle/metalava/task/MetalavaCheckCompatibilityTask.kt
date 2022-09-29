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
import org.gradle.kotlin.dsl.create
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
            "--no-banner",
            "--format=${format.get()}",
            "--source-files", tempFilename.get(),
            "--check-compatibility:${apiType.get()}:released", filename.get(),
            "--input-kotlin-nulls=${inputKotlinNulls.get().flagValue}"
        ) + reportWarningsAsErrors.get().flag("--warnings-as-errors") + reportLintsAsErrors.get()
            .flag("--lints-as-errors") + hidePackages + hideAnnotations
        executeMetalavaWork(args)
    }

    internal companion object : MetalavaTaskContainer() {
        private const val TASK_NAME = "metalavaCheckCompatibility"
        private const val TASK_DESCRIPTION =
            "Checks API compatibility between the code base and the released API."
        private const val METALAVA_CURRENT_PATH = "metalava/current.txt"

        fun create(
            project: Project,
            objectFactory: ObjectFactory,
            extension: MetalavaExtension,
            module: Module,
            variantName: String?
        ): MetalavaCheckCompatibilityTask {
            val tempFilename = project.layout.buildDirectory
                .file(METALAVA_CURRENT_PATH).get().asFile.absolutePath
            val taskName = getFullTaskName(TASK_NAME, variantName)
            val metalavaClasspath = project.getMetalavaClasspath(
                objectFactory,
                jarPath = extension.metalavaJarPath.get().ifEmpty { null },
                version = extension.version.get(),
            )
            return project.tasks.create<MetalavaCheckCompatibilityTask>(taskName) {
                this.metalavaClasspath.from(metalavaClasspath)
                this.tempFilename.set(tempFilename)
                sourcePaths.setFrom(extension.sourcePaths)
                filename.set(extension.filename)
                shouldRunGenerateSignature.set(false)
                bootClasspath.from(module.bootClasspath)
                compileClasspath.from(module.compileClasspath(variantName))
                documentation.set(extension.documentation)
                format.set(extension.format)
                signature.set(extension.signature)
                javaSourceLevel.set(extension.javaSourceLevel)
                outputKotlinNulls.set(extension.outputKotlinNulls)
                outputDefaultValues.set(extension.outputDefaultValues)
                includeSignatureVersion.set(extension.includeSignatureVersion)
                hiddenPackages.set(extension.hiddenPackages)
                hiddenAnnotations.set(extension.hiddenAnnotations)
                apiType.set(extension.apiType)
                inputKotlinNulls.set(extension.inputKotlinNulls)
                reportWarningsAsErrors.set(extension.reportWarningsAsErrors)
                reportLintsAsErrors.set(extension.reportLintsAsErrors)
            }
        }
    }
}

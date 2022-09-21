package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.Documentation
import me.tylerbwong.gradle.metalava.Module
import me.tylerbwong.gradle.metalava.Signature
import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.create
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject

@CacheableTask
internal abstract class MetalavaGenerateSignatureTask @Inject constructor(
    objectFactory: ObjectFactory,
    workerExecutor: WorkerExecutor,
) : BaseMetalavaTask(objectFactory, workerExecutor) {

    init {
        group = "documentation"
        description = TASK_DESCRIPTION
    }

    @get:Classpath
    abstract val bootClasspath: ConfigurableFileCollection

    @get:Classpath
    abstract val compileClasspath: ConfigurableFileCollection

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFiles
    abstract val sourcePaths: ConfigurableFileCollection

    @get:Input
    abstract val documentation: Property<Documentation>

    @get:Input
    abstract val signature: Property<Signature>

    @get:Input
    abstract val javaSourceLevel: Property<JavaVersion>

    @get:Input
    abstract val outputKotlinNulls: Property<Boolean>

    @get:Input
    abstract val outputDefaultValues: Property<Boolean>

    @get:Input
    abstract val includeSignatureVersion: Property<Boolean>

    @get:Input
    abstract val shouldRunGenerateSignature: Property<Boolean>

    @get:OutputFile
    @get:Optional
    abstract val keepFilename: Property<String?>

    @TaskAction
    fun metalavaGenerateSignature() {
        if (shouldRunGenerateSignature.get()) {
            metalavaGenerateSignatureInternal()
        }
    }

    protected fun metalavaGenerateSignatureInternal(
        filenameOverride: String? = null,
        awaitWork: Boolean = false
    ) {
        val fullClasspath = (bootClasspath + compileClasspath).joinToString(File.pathSeparator)
        val sourcePaths = sourcePaths.filter { it.exists() }.joinToString(File.pathSeparator)
        val hidePackages = hiddenPackages.get().flatMap { listOf("--hide-package", it) }
        val hideAnnotations = hiddenAnnotations.get().flatMap { listOf("--hide-annotation", it) }
        val keepFilename = keepFilename.orNull
        val keepFileFlags = if (!keepFilename.isNullOrEmpty()) {
            listOf("--proguard", keepFilename)
        } else {
            emptyList()
        }

        val args: List<String> = listOf(
            "${documentation.get()}",
            "--no-banner",
            "--format=${format.get()}",
            "${signature.get()}", filenameOverride ?: filename.get(),
            "--java-source", "${javaSourceLevel.get()}",
            "--classpath", fullClasspath,
            "--output-kotlin-nulls=${outputKotlinNulls.get().flagValue}",
            "--output-default-values=${outputDefaultValues.get().flagValue}",
            "--include-signature-version=${includeSignatureVersion.get().flagValue}",
            "--source-path", sourcePaths,
        ) + hidePackages + hideAnnotations + keepFileFlags
        executeMetalavaWork(args, awaitWork)
    }

    internal companion object : MetalavaTaskContainer() {
        private const val TASK_NAME = "metalavaGenerateSignature"
        private const val TASK_DESCRIPTION = "Generates a Metalava signature descriptor file."

        fun create(
            project: Project,
            objectFactory: ObjectFactory,
            extension: MetalavaExtension,
            module: Module,
            variantName: String? = null,
        ) {
            val taskName = getFullTaskName(TASK_NAME, variantName)
            val metalavaClasspath = project.getMetalavaClasspath(
                objectFactory,
                jarPath = extension.metalavaJarPath.get().ifEmpty { null },
                version = extension.version.get(),
            )
            project.tasks.create<MetalavaGenerateSignatureTask>(taskName) {
                this.metalavaClasspath.from(metalavaClasspath)
                sourcePaths.from(extension.sourcePaths)
                filename.set(extension.filename)
                shouldRunGenerateSignature.set(true)
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
                keepFilename.set(extension.keepFilename)
            }
        }
    }
}

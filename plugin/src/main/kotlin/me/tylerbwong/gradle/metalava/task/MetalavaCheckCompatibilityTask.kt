package me.tylerbwong.gradle.metalava.task

import javax.inject.Inject
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
import org.gradle.workers.WorkerExecutor

@CacheableTask
internal abstract class MetalavaCheckCompatibilityTask
@Inject
constructor(objectFactory: ObjectFactory, workerExecutor: WorkerExecutor) :
  MetalavaGenerateSignatureTask(objectFactory, workerExecutor) {

  init {
    group = "verification"
    description = TASK_DESCRIPTION
  }

  @get:OutputFile abstract val tempFilename: Property<String>

  @get:Input abstract val apiType: Property<String>

  @get:Input abstract val inputKotlinNulls: Property<Boolean>

  @get:Input abstract val reportWarningsAsErrors: Property<Boolean>

  @get:Input abstract val reportLintsAsErrors: Property<Boolean>

  @TaskAction
  fun metalavaCheckCompatibilityTask() {
    metalavaGenerateSignatureInternal(filenameOverride = tempFilename.get(), awaitWork = true)
    val args: List<String> =
      listOf(
        "--source-files",
        tempFilename.get(),
        "--check-compatibility:${apiType.get()}:released",
        filename.get(),
      ) +
        reportWarningsAsErrors.get().flag("--warnings-as-errors") +
        reportLintsAsErrors.get().flag("--lints-as-errors") +
        createCommonArgs()
    executeMetalavaWork(args)
  }

  internal companion object : MetalavaTaskContainer() {
    private const val TASK_NAME = "metalavaCheckCompatibility"
    private const val TASK_DESCRIPTION =
      "Checks API compatibility between the code base and the released API."
    private const val METALAVA_CURRENT_PATH = "metalava/current.txt"

    fun register(
      project: Project,
      extension: MetalavaExtension,
      module: Module,
      variantName: String?,
    ): TaskProvider<MetalavaCheckCompatibilityTask> {
      val tempFilenameProvider =
        project.layout.buildDirectory.file(METALAVA_CURRENT_PATH).map { it.asFile.absolutePath }
      val taskName = getFullTaskName(TASK_NAME, variantName)
      val metalavaClasspath =
        project.getMetalavaClasspath(
          metalavaJar = extension.metalavaJar,
          version = extension.version.get(),
        )
      val bootClasspathProvider = project.provider { module.bootClasspath }
      return project.tasks.register(taskName, MetalavaCheckCompatibilityTask::class.java) {
        it.metalavaClasspath.from(metalavaClasspath)
        it.tempFilename.set(tempFilenameProvider)
        it.sourceSets.from(module.sourceSets(project, variantName))
        it.additionalSourceSets.setFrom(extension.additionalSourceSets)
        it.excludedSourceSets.setFrom(extension.excludedSourceSets)
        it.filename.set(extension.filename)
        it.shouldRunGenerateSignature.set(false)
        it.bootClasspath.from(bootClasspathProvider)
        it.compileClasspath.from(module.compileClasspath(project, variantName))
        it.format.set(extension.format)
        it.signature.set(extension.signature)
        it.javaSourceLevel.set(extension.javaSourceLevel)
        it.hiddenPackages.set(extension.hiddenPackages)
        it.hiddenAnnotations.set(extension.hiddenAnnotations)
        it.apiCompatAnnotations.set(extension.apiCompatAnnotations)
        it.apiType.set(extension.apiType)
        it.inputKotlinNulls.set(extension.inputKotlinNulls)
        it.reportWarningsAsErrors.set(extension.reportWarningsAsErrors)
        it.reportLintsAsErrors.set(extension.reportLintsAsErrors)
        it.arguments.set(extension.arguments)
      }
    }
  }
}

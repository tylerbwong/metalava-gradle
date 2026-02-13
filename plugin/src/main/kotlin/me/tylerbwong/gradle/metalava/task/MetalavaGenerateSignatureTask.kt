package me.tylerbwong.gradle.metalava.task

import java.io.File
import javax.inject.Inject
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
import org.gradle.api.tasks.TaskProvider
import org.gradle.workers.WorkerExecutor

@CacheableTask
internal abstract class MetalavaGenerateSignatureTask
@Inject
constructor(objectFactory: ObjectFactory, workerExecutor: WorkerExecutor) :
  BaseMetalavaTask(objectFactory, workerExecutor) {

  init {
    group = "documentation"
    description = TASK_DESCRIPTION
  }

  @get:Classpath abstract val bootClasspath: ConfigurableFileCollection

  @get:Classpath abstract val compileClasspath: ConfigurableFileCollection

  @get:PathSensitive(PathSensitivity.RELATIVE)
  @get:InputFiles
  abstract val sourceSets: ConfigurableFileCollection

  @get:PathSensitive(PathSensitivity.RELATIVE)
  @get:InputFiles
  abstract val additionalSourceSets: ConfigurableFileCollection

  @get:PathSensitive(PathSensitivity.RELATIVE)
  @get:InputFiles
  abstract val excludedSourceSets: ConfigurableFileCollection

  @get:Input abstract val signature: Property<Signature>

  @get:Input abstract val javaSourceLevel: Property<JavaVersion>

  @get:Input abstract val shouldRunGenerateSignature: Property<Boolean>

  @get:OutputFile @get:Optional abstract val keepFilename: Property<String>

  @TaskAction
  fun metalavaGenerateSignature() {
    if (shouldRunGenerateSignature.get()) {
      metalavaGenerateSignatureInternal()
    }
  }

  protected fun metalavaGenerateSignatureInternal(
    filenameOverride: String? = null,
    awaitWork: Boolean = false,
  ) {
    val fullClasspath = (bootClasspath + compileClasspath).joinToString(File.pathSeparator)
    val sourcePaths =
      (sourceSets + additionalSourceSets - excludedSourceSets)
        .filter { it.exists() }
        .joinToString(File.pathSeparator)
    val keepFilename = keepFilename.orNull
    val keepFileFlags =
      if (!keepFilename.isNullOrEmpty()) {
        listOf("--proguard", keepFilename)
      } else {
        emptyList()
      }

    val args: List<String> =
      listOf(
        "${signature.get()}",
        filenameOverride ?: filename.get(),
        "--java-source",
        "${javaSourceLevel.get()}",
        "--classpath",
        fullClasspath,
        "--source-path",
        sourcePaths,
      ) + keepFileFlags + createCommonArgs()
    executeMetalavaWork(args, awaitWork)
  }

  internal companion object : MetalavaTaskContainer() {
    private const val TASK_NAME = "metalavaGenerateSignature"
    private const val TASK_DESCRIPTION = "Generates a Metalava signature descriptor file."

    fun register(
      project: Project,
      extension: MetalavaExtension,
      module: Module,
      variantName: String? = null,
    ): TaskProvider<MetalavaGenerateSignatureTask> {
      val taskName = getFullTaskName(TASK_NAME, variantName)
      val metalavaClasspath =
        project.getMetalavaClasspath(
          metalavaJar = extension.metalavaJar,
          version = extension.version.get(),
        )
      val bootClasspathProvider = project.provider { module.bootClasspath }
      return project.tasks.register(taskName, MetalavaGenerateSignatureTask::class.java) {
        it.metalavaClasspath.from(metalavaClasspath)
        it.sourceSets.from(module.sourceSets(project, variantName))
        it.additionalSourceSets.setFrom(extension.additionalSourceSets)
        it.excludedSourceSets.setFrom(extension.excludedSourceSets)
        it.filename.set(extension.filename)
        it.shouldRunGenerateSignature.set(true)
        it.bootClasspath.from(bootClasspathProvider)
        it.compileClasspath.from(module.compileClasspath(project, variantName))
        it.format.set(extension.format)
        it.signature.set(extension.signature)
        it.javaSourceLevel.set(extension.javaSourceLevel)
        it.hiddenPackages.set(extension.hiddenPackages)
        it.hiddenAnnotations.set(extension.hiddenAnnotations)
        it.apiCompatAnnotations.set(extension.apiCompatAnnotations)
        it.keepFilename.set(extension.keepFilename)
        it.arguments.set(extension.arguments)
      }
    }
  }
}

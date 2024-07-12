package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.Format
import me.tylerbwong.gradle.metalava.worker.MetalavaWorkAction
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.kotlin.dsl.setProperty
import org.gradle.workers.WorkerExecutor

internal abstract class BaseMetalavaTask(
    objectFactory: ObjectFactory,
    private val workerExecutor: WorkerExecutor,
) : DefaultTask() {

    @get:Classpath
    abstract val metalavaClasspath: ConfigurableFileCollection

    @get:Optional
    @get:OutputFile
    abstract val filename: Property<String>

    @get:Optional
    @get:Input
    abstract val format: Property<Format>

    @get:Optional
    @get:Input
    val hiddenPackages: SetProperty<String> = objectFactory.setProperty()

    @get:Optional
    @get:Input
    val hiddenAnnotations: SetProperty<String> = objectFactory.setProperty()

    @get:Optional
    @get:Input
    val apiCompatAnnotations: SetProperty<String> = objectFactory.setProperty()

    @get:Optional
    @get:Input
    val arguments: SetProperty<String> = objectFactory.setProperty()

    protected fun executeMetalavaWork(args: List<String>, awaitWork: Boolean = false) {
        val queue = workerExecutor.noIsolation()
        logger.debug("Executing Metalava with arguments: {}", args)
        queue.submit(MetalavaWorkAction::class.java) {
            classpath.from(metalavaClasspath)
            arguments.set(args.joinToString())
        }
        if (awaitWork) {
            queue.await()
        }
    }

    protected fun createCommonArgs(): List<String> {
        val hidePackages = hiddenPackages.get().flatMap { listOf("--hide-package", it) }
        val hideAnnotations = hiddenAnnotations.get().flatMap { listOf("--hide-annotation", it) }
        val apiCompatAnnotations = apiCompatAnnotations.get().flatMap { listOf("--api-compat-annotation", it) }
        return listOf(
            "--format=${format.get()}",
        ) + hidePackages + hideAnnotations + apiCompatAnnotations + arguments.get()
    }
}

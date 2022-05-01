package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.worker.MetalavaWorkAction
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.workers.WorkerExecutor

internal abstract class BaseMetalavaTask(
    private val workerExecutor: WorkerExecutor,
) : DefaultTask() {

    @get:Classpath
    abstract val metalavaClasspath: ConfigurableFileCollection

    protected fun executeMetalavaWork(args: List<String>) {
        val queue = workerExecutor.processIsolation()
        queue.submit(MetalavaWorkAction::class.java) {
            classpath.from(metalavaClasspath)
            arguments.set(args.joinToString())
        }
    }
}

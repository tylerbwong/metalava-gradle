package me.tylerbwong.gradle.metalava.task

import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

@CacheableTask
internal abstract class MetalavaCheckCompatibilityTask @Inject constructor(
    workerExecutor: WorkerExecutor,
) : BaseMetalavaTask(workerExecutor) {

    @TaskAction
    fun metalavaCheckCompatibilityTask() {
        val args = listOf("")
        executeMetalavaWork(args)
    }

    companion object {
        const val TASK_NAME = "metalavaCheckCompatibility"
        const val TASK_DESCRIPTION = "Checks API compatibility between the code base and the current or release API."
    }
}

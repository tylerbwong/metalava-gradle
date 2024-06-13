package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

@CacheableTask
internal abstract class MetalavaHelpTask @Inject constructor(
    objectFactory: ObjectFactory,
    workerExecutor: WorkerExecutor,
) : BaseMetalavaTask(objectFactory, workerExecutor) {

    init {
        group = "other"
        description = TASK_DESCRIPTION
    }

    @TaskAction
    fun metalavaHelp() {
        executeMetalavaWork(listOf("--help"))
    }

    companion object : MetalavaTaskContainer() {
        private const val TASK_NAME = "metalavaHelp"
        private const val TASK_DESCRIPTION = "Displays the metalava help message."

        fun register(
            project: Project,
            objectFactory: ObjectFactory,
            extension: MetalavaExtension,
        ) {
            val metalavaClasspath = project.getMetalavaClasspath(
                objectFactory,
                jarPath = extension.metalavaJarPath.get().ifEmpty { null },
                version = extension.version.get(),
            )
            if (project.tasks.findByName(TASK_NAME) == null) {
                project.tasks.register<MetalavaHelpTask>(TASK_NAME) {
                    this.metalavaClasspath.from(metalavaClasspath)
                }
            }
        }
    }
}

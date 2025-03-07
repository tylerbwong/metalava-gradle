package me.tylerbwong.gradle.metalava.worker

import org.gradle.process.ExecOperations
import org.gradle.workers.WorkAction
import javax.inject.Inject

/**
 * A [WorkAction] that executes the Metalava main class and passes the necessary arguments to the
 * driver.
 */
internal abstract class MetalavaWorkAction @Inject constructor(
    private val execOperations: ExecOperations,
) : WorkAction<MetalavaWorkParameters> {

    override fun execute() {
        execOperations.javaexec {
            systemProperty("java.awt.headless", "true")
            mainClass.set(METALAVA_MAIN_CLASS)
            classpath(parameters.classpath)
            isIgnoreExitValue = false
            args = parameters.arguments.get().split(", ")
        }
    }

    companion object {
        private const val METALAVA_MAIN_CLASS = "com.android.tools.metalava.Driver"
    }
}

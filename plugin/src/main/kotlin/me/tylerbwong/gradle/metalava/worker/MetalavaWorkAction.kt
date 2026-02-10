package me.tylerbwong.gradle.metalava.worker

import javax.inject.Inject
import org.gradle.process.ExecOperations
import org.gradle.workers.WorkAction

/**
 * A [WorkAction] that executes the Metalava main class and passes the necessary arguments to the
 * driver.
 */
internal abstract class MetalavaWorkAction @Inject constructor(
    private val execOperations: ExecOperations,
) : WorkAction<MetalavaWorkParameters> {

    override fun execute() {
        execOperations.javaexec {
            it.systemProperty("java.awt.headless", "true")
            it.mainClass.set(METALAVA_MAIN_CLASS)
            it.classpath(parameters.classpath)
            it.isIgnoreExitValue = false
            it.args = parameters.arguments.get().split(", ")
        }
    }

    companion object {
        private const val METALAVA_MAIN_CLASS = "com.android.tools.metalava.Driver"
    }
}

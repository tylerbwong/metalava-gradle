package me.tylerbwong.gradle.metalava.task

import me.tylerbwong.gradle.metalava.extension.MetalavaExtension
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import java.util.Locale

internal abstract class MetalavaTaskContainer {
    protected val Boolean.flagValue: String get() = if (this) "yes" else "no"

    protected fun Boolean.flag(flagValue: String): List<String> = if (this) {
        listOf(flagValue)
    } else {
        emptyList()
    }

    /**
     * Obtains the Metalava classpath from either:
     * 1. A locally provided JAR path OR
     * 2. if no JAR path is provided, the artifact coordinates specified by
     * [METALAVA_GROUP_ID]:[METALAVA_MODULE_ID]:[MetalavaExtension.version]
     */
    protected fun Project.getMetalavaClasspath(
        objectFactory: ObjectFactory,
        extension: MetalavaExtension
    ): FileCollection {
        return extension.metalavaJarPath?.let { objectFactory.fileCollection().from(it) } ?: run {
            val configuration = configurations.findByName(METALAVA_MODULE_ID)
                ?: configurations.create(METALAVA_MODULE_ID).apply {
                    val dependency = this@getMetalavaClasspath.dependencies.create(
                        "$METALAVA_GROUP_ID:$METALAVA_MODULE_ID:${extension.version}"
                    )
                    dependencies.add(dependency)
                }
            files(configuration)
        }
    }

    protected fun getFullTaskName(taskName: String, variantName: String?): String {
        return if (variantName != null) {
            taskName + variantName.capitalize(Locale.getDefault())
        } else {
            taskName
        }
    }

    companion object {
        private const val METALAVA_GROUP_ID = "com.android.tools.metalava"
        private const val METALAVA_MODULE_ID = "metalava"
    }
}

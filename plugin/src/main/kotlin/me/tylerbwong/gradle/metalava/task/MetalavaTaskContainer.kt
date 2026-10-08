package me.tylerbwong.gradle.metalava.task

import java.util.Locale
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider

internal abstract class MetalavaTaskContainer {
    protected fun Boolean.flag(flagValue: String): List<String> =
        if (this) {
            listOf(flagValue)
        } else {
            emptyList()
        }

    /**
     * Obtains the Metalava classpath from either:
     * 1. A locally provided JAR `FileCollection` OR
     * 2. if no JAR path is provided, the artifact coordinates specified by
     *    [METALAVA_GROUP_ID]:[METALAVA_MODULE_ID]:[MetalavaExtension.version]
     */
    protected fun Project.getMetalavaClasspath(
        metalavaJar: FileCollection,
        version: Provider<String>,
    ): FileCollection {
        val dependencyHandler = dependencies
        val configuration =
            configurations.findByName(METALAVA_MODULE_ID)
                ?: configurations.create(METALAVA_MODULE_ID).apply {
                    dependencies.addLater(
                        version.map { version ->
                            dependencyHandler.create(
                                "$METALAVA_GROUP_ID:$METALAVA_MODULE_ID:$version"
                            )
                        }
                    )
                }
        return files(provider { if (metalavaJar.isEmpty) configuration else metalavaJar })
    }

    protected fun getFullTaskName(taskName: String, variantName: String?): String {
        return if (variantName != null) {
            taskName +
                variantName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
        } else {
            taskName
        }
    }

    companion object {
        private const val METALAVA_GROUP_ID = "com.android.tools.metalava"
        private const val METALAVA_MODULE_ID = "metalava"
    }
}

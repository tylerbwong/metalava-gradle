package me.tylerbwong.gradle.metalava

import com.android.build.gradle.LibraryExtension
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.kotlin.dsl.get
import java.io.File

sealed class Module(
    val bootClasspath: Collection<File>,
    val compileClasspath: Collection<File>
) {
    class Android(extension: LibraryExtension) : Module(
        bootClasspath = extension.bootClasspath,
        compileClasspath = extension.libraryVariants.find {
            it.name.contains("debug", ignoreCase = true)
        }?.getCompileClasspath(null)?.filter { it.exists() }?.files ?: emptyList()
    )

    class Java(convention: JavaPluginConvention) : Module(
        bootClasspath = emptyList(),
        compileClasspath = convention.sourceSets["main"].compileClasspath.files
    )
}
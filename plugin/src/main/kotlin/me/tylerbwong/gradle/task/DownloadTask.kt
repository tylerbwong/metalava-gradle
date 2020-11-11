package me.tylerbwong.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * A simple task to download a file from a given URL.
 *
 * @property url The location of the desired file to download from
 * @property output The location of the downloaded file
 */
internal abstract class DownloadTask : DefaultTask() {
    @get:Input
    abstract val url: Property<String>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {
        if (output.asFile.get().exists()) {
            return
        }
        val rawUrl = url.get()
        try {
            URL(rawUrl).openStream().use {
                Files.copy(it, output.asFile.get().toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        } catch (ex: Exception) {
            throw GradleException("Unable to download from $rawUrl", ex)
        }
    }
}
package me.tylerbwong.gradle.task

import okhttp3.OkHttpClient
import okhttp3.Request
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * A simple task to download a file from a given URL.
 *
 * @property url The location of the desired file to download from
 * @property output The location of the downloaded file
 */
abstract class DownloadTask : DefaultTask() {
    @get:Input
    abstract val url: Property<String>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {
        if (output.asFile.get().exists()) {
            return
        }
        val client = OkHttpClient()
        val rawUrl = url.get()
        val request = Request.Builder().get().url(rawUrl).build()
        val body = client.newCall(request).execute().body ?: throw GradleException("Unable to download from $rawUrl")

        body.byteStream().use {
            output.asFile.get().outputStream().buffered().use { file ->
                it.copyTo(file)
            }
        }
    }
}
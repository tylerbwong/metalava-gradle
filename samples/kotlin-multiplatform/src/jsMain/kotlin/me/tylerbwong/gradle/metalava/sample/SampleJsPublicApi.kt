package me.tylerbwong.gradle.metalava.sample

internal class JsPlatform : Platform {
    override val platform: String = "JS"
}

actual val platform: Platform = JsPlatform()

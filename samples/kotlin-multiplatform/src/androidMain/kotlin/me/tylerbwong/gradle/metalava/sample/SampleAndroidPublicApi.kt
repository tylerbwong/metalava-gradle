package me.tylerbwong.gradle.metalava.sample

internal class AndroidPlatform : Platform {
    override val platform: String = "Android"
}

actual val platform: Platform = AndroidPlatform()

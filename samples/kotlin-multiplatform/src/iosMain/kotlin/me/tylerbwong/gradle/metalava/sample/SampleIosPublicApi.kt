package me.tylerbwong.gradle.metalava.sample

internal class IosPlatform : Platform {
    override val platform: String = "iOS"
}

actual val platform: Platform = IosPlatform()

package me.tylerbwong.gradle.metalava.sample

internal class LinuxPlatform : Platform {
  override val platform: String = "Linux"
}

actual val platform: Platform = LinuxPlatform()

package me.tylerbwong.gradle.metalava.sample

internal class JvmPlatform : Platform {
  override val platform: String = "JVM"
}

actual val platform: Platform = JvmPlatform()

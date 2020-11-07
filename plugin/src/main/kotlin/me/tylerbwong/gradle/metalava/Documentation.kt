package me.tylerbwong.gradle.metalava

sealed class Documentation(private val flagValue: String) {
    object Public : Documentation("--public")
    object Protected : Documentation("--protected")
    object Package : Documentation("--package")
    object Private : Documentation("--private")
    object Hidden : Documentation("--hidden")

    override fun toString(): String = flagValue
}
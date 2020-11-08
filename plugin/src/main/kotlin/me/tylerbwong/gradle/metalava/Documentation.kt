package me.tylerbwong.gradle.metalava

enum class Documentation(private val flagValue: String) {
    PUBLIC("--public"),
    PROTECTED("--protected"),
    PACKAGE("--package"),
    PRIVATE("--private"),
    HIDDEN("--hidden");

    override fun toString(): String = flagValue
}
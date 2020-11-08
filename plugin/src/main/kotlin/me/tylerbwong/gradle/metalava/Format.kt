package me.tylerbwong.gradle.metalava

enum class Format(private val format: String) {
    V1("v1"),
    V2("v2"),
    V3("v3");

    override fun toString(): String = format
}
package me.tylerbwong.gradle.metalava

/**
 * Sets the output signature file format to be the given version.
 */
enum class Format(private val format: String) {
    V1("v1"),
    V2("v2"),
    V3("v3");

    override fun toString(): String = format
}
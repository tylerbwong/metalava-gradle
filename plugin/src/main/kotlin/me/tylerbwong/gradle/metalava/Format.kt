package me.tylerbwong.gradle.metalava

/** Sets the output signature file format to be the given version. */
public enum class Format(private val format: String) {
    V2("v2"),
    V3("v3"),
    V4("v4");

    override fun toString(): String = format
}

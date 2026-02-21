package me.tylerbwong.gradle.metalava

/** Sets the output signature file format to be the given version. */
public enum class Format {
    V2,
    V3,
    V4;

    override fun toString(): String = name.lowercase()
}

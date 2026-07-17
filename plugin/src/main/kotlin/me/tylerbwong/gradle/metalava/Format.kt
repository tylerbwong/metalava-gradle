package me.tylerbwong.gradle.metalava

/** Sets the output signature file format to be the given version. */
public enum class Format(
    private val version: String
) {
    V2("2.0"),
    V3("3.0"),
    V4("4.0");

    override fun toString(): String = version
}

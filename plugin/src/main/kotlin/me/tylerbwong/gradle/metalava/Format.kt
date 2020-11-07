package me.tylerbwong.gradle.metalava

sealed class Format(private val format: String) {
    object V1 : Format("v1")
    object V2 : Format("v2")
    object V3 : Format("v3")

    override fun toString(): String = format
}
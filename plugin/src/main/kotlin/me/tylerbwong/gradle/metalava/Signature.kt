package me.tylerbwong.gradle.metalava

enum class Signature(private val signature: String) {
    API("--api"),
    PRIVATE_API("--private-api"),
    DEX_API("--dex-api"),
    PRIVATE_DEX_API("--private-dex-api"),
    REMOVED_API("--removed-api");

    override fun toString(): String = signature
}
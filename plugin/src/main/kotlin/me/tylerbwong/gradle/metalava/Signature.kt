package me.tylerbwong.gradle.metalava

sealed class Signature(private val signature: String) {
    object Api : Signature("--api")
    object PrivateApi : Signature("--private-api")
    object DexApi : Signature("--dex-api")
    object PrivateDexApi : Signature("--private-dex-api")
    object RemovedApi : Signature("--removed-api")

    override fun toString(): String = signature
}
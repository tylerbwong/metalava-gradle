package me.tylerbwong.gradle.metalava

/**
 * Flags to determine which type of signature file to generate.
 */
enum class Signature(private val signature: String) {
    /**
     * Generate a signature descriptor file.
     */
    API("--api"),

    /**
     * Generate a signature descriptor file listing the exact private APIs.
     */
    PRIVATE_API("--private-api"),

    /*
     * Generate a DEX signature descriptor file listing the APIs.
     */
    DEX_API("--dex-api"),

    /**
     * Generate a DEX signature descriptor file listing the exact private APIs.
     */
    PRIVATE_DEX_API("--private-dex-api"),

    /**
     * Generate a DEX signature descriptor along with file and line numbers.
     */
    DEX_API_MAPPING("--dex-api-mapping"),

    /**
     * Generate a signature descriptor file for APIs that have been removed.
     */
    REMOVED_API("--removed-api");

    override fun toString(): String = signature
}
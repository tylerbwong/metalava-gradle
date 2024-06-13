package me.tylerbwong.gradle.metalava

/**
 * Flags to determine documented API by visibility modifier.
 */
@Deprecated("This has been removed and is not currently used.")
enum class Documentation(private val flagValue: String) {
    /**
     * Only include elements that are public.
     */
    PUBLIC("--public"),

    /**
     * Only include elements that are public or protected.
     */
    PROTECTED("--protected"),

    /**
     * Only include elements that are public, protected or package protected.
     */
    PACKAGE("--package"),

    /**
     * Include all elements except those that are marked hidden.
     */
    PRIVATE("--private"),

    /**
     * Include all elements, including hidden.
     */
    HIDDEN("--hidden");

    override fun toString(): String = flagValue
}

package me.tylerbwong.gradle.metalava.extension

import me.tylerbwong.gradle.metalava.Documentation
import me.tylerbwong.gradle.metalava.Format
import me.tylerbwong.gradle.metalava.Signature
import org.gradle.api.JavaVersion

open class MetalavaExtension {
    /**
     * The version of Metalava to use.
     */
    var version = "1.0.0-alpha01"

    /**
     * A custom Metalava JAR location path to use instead of the embedded dependency.
     */
    var metalavaJarPath: String? = null

    /**
     * Sets the source level for Java source files; default is 1.8.
     */
    var javaSourceLevel = JavaVersion.VERSION_1_8

    /**
     * @see Format
     */
    var format: Format = Format.V3

    /**
     * @see Signature
     */
    var signature: Signature = Signature.API

    /**
     * The final descriptor file output name.
     */
    var filename = "api.txt"

    /**
     * @see Documentation
     */
    var documentation: Documentation = Documentation.PROTECTED

    /**
     * Controls whether nullness annotations should be formatted as in Kotlin (with "?" for nullable types, "" for non
     * nullable types, and "!" for unknown. The default is yes.
     */
    var outputKotlinNulls = true

    /**
     * Controls whether default values should be included in signature files. The default is yes.
     */
    var outputDefaultValues = true

    /**
     * Skip common package prefixes like java.lang.* and kotlin.* in signature files, along with packages for well known
     * annotations like @Nullable and @NonNull.
     */
    var omitCommonPackages = true

    /**
     * Whether the signature files should include a comment listing the format version of the signature file.
     */
    var includeSignatureVersion = true

    /**
     * Remove the given packages from the API even if they have not been marked with @hide.
     */
    val hiddenPackages = mutableSetOf<String>()

    /**
     * Treat any elements annotated with the given annotation as hidden.
     */
    val hiddenAnnotations = mutableSetOf<String>()

    /**
     * Promote all warnings to errors.
     */
    var reportWarningsAsErrors = false

    /**
     * Promote all API lint warnings to errors.
     */
    var reportLintsAsErrors = false
}

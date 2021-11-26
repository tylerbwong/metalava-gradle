package me.tylerbwong.gradle.metalava.extension

import me.tylerbwong.gradle.metalava.Documentation
import me.tylerbwong.gradle.metalava.Format
import me.tylerbwong.gradle.metalava.Signature
import org.gradle.api.JavaVersion

open class MetalavaExtension {
    /**
     * The version of Metalava to use.
     */
    var version = "1.0.0-alpha04"

    /**
     * A custom Metalava JAR location path to use instead of the embedded dependency.
     */
    var metalavaJarPath: String? = null

    /**
     * Sets the source level for Java source files; default is 11.
     */
    var javaSourceLevel = JavaVersion.VERSION_11

    /**
     * @see Format
     */
    var format: Format = Format.V4

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
     * Controls whether nullness annotations should be formatted as in Kotlin (with "?" for nullable
     * types, "" for non-nullable types, and "!" for unknown. The default is yes.
     */
    var outputKotlinNulls = true

    /**
     * Controls whether default values should be included in signature files. The default is yes.
     */
    var outputDefaultValues = true

    /**
     * Whether the signature files should include a comment listing the format version of the
     * signature file.
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
     * Whether the signature file being read should be interpreted as having encoded its types using
     * Kotlin style types: a suffix of "?" for nullable types, no suffix for non nullable types, and
     * "!" for unknown. The default is no.
     */
    var inputKotlinNulls = false

    /**
     * Promote all warnings to errors.
     */
    var reportWarningsAsErrors = false

    /**
     * Promote all API lint warnings to errors.
     */
    var reportLintsAsErrors = false

    /**
     * For Android modules defines which variant should be used to resolve classpath when Metalava
     * generates or checks API.
     */
    var androidVariantName = "debug"

    /**
     * Customization of the severities to apply when doing compatibility checking
     * "current" will identify all changes (i.e. removals and additions)
     * "released" will identify only removals and additions of abstract methods
     * The default is "current"
     */

    var releaseType = "current"
}

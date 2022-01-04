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
     * signature file. The default is yes.
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
     * Customization of the severities to apply when doing compatibility checking.
     * "current" will identify all changes (i.e. removals and additions).
     * "released" will identify only removals and additions of abstract methods.
     * The default is "current".
     */
    var releaseType = "current"

    /**
     * The directories to search for source files. An exception will be thrown is the named
     * directories are not direct children of the project root. The default is "src".
     */
    var sourcePaths = mutableSetOf("src")

    /**
     * The sub-directories of those specified by [sourcePaths] that contain any of the given strings
     * that are not to be searched for source files. The default is "test".
     *
     * The behavior of this property can be configured using [ignoreSourcePathsExactMatch].
     *
     * @see ignoreSourcePathsExactMatch
     */
    var ignoreSourcePaths = mutableSetOf("test")

    /**
     * Flag to be used in conjunction with [ignoreSourcePaths] to toggle the matching behaviour. The
     * default behavior (i.e. `false`) is that directories specified by [ignoreSourcePaths] are
     * successfully matched if the elements of [ignoreSourcePaths] form (using a case-insensitive
     * match) part of the directory name (i.e. `String.contains(ignoreCase = true)`). Enabling this
     * flag will change that behavior to case-sensitive exact-matching (i.e. equality) only.
     *
     * @see ignoreSourcePaths
     */
    var ignoreSourcePathsExactMatch = false

    /**
     * If the tasks should run as part of Gradle's `check` task. The default is yes.
     */
    var enforceCheck = true;
}

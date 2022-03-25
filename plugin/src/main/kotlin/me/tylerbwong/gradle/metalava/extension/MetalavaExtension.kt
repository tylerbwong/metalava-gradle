package me.tylerbwong.gradle.metalava.extension

import me.tylerbwong.gradle.metalava.Documentation
import me.tylerbwong.gradle.metalava.Format
import me.tylerbwong.gradle.metalava.Signature
import org.gradle.api.JavaVersion
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class MetalavaExtension @Inject constructor(
    objects: ObjectFactory,
) {
    /**
     * The version of Metalava to use.
     */
    var version = "1.0.0-alpha05"

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
     *  Type is one of 'api' and 'removed', which checks either the public api or the removed api.
     */
    var apiType = "api"

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
     * The directories to search for source files. An exception will be thrown if the named
     * directories are not direct children of the project root. The default is "src".
     *
     * @see addSourcePaths
     */
    var sourcePaths = mutableSetOf("src")

    /** Internal. Do not use. */
    internal val sourcePathsFileCollection = objects.fileCollection()

    /**
     * Add a directory (or multiple) in which to search for source files.
     * The given paths are evaluated as per [org.gradle.api.Project.files].
     */
    fun addSourcePaths(sourcePaths: Any) {
        sourcePathsFileCollection.from(sourcePaths)
    }

    /**
     * If the tasks should run as part of Gradle's `check` task. The default is yes.
     */
    var enforceCheck = true
}

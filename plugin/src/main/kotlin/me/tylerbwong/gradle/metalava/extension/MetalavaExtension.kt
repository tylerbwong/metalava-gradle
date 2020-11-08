package me.tylerbwong.gradle.metalava.extension

import me.tylerbwong.gradle.metalava.Documentation
import me.tylerbwong.gradle.metalava.Format
import me.tylerbwong.gradle.metalava.Signature

open class MetalavaExtension {
    var metalavaJarPath: String? = null
    var format: Format = Format.V3
    var signature: Signature = Signature.API
    var outputFileName = DEFAULT_FILE_NAME
    var documentation: Documentation = Documentation.PROTECTED
    var shouldOutputKotlinNulls = true
    var shouldOutputDefaultValues = true
    var shouldOmitCommonPackages = true
    val hidePackages = mutableSetOf<String>()
    val hideAnnotations = mutableSetOf<String>()

    companion object {
        private const val DEFAULT_FILE_NAME = "api.txt"
    }
}
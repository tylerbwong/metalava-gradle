package me.tylerbwong.gradle.metalava

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

class MetalavaGradlePluginTest {

    @TempDir
    lateinit var testProjectDir: File

    private lateinit var buildscriptFile: File
    private lateinit var gradleRunner: GradleRunner

    @BeforeEach
    fun setUp() {
        gradleRunner = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(testProjectDir.absoluteFile)
            .withTestKitDir(testProjectDir.resolve("test"))
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `check metalavaGenerateSignature runs successfully with no source`(
        isConfigurationCacheEnabled: Boolean
    ) {
        buildscriptFile = testProjectDir.resolve("build.gradle.kts").apply {
            appendText(
                """
                    allprojects {
                        repositories {
                            google()
                            mavenCentral()
                        }
                    }
    
                    plugins {
                        `java-library`
                        id("me.tylerbwong.gradle.metalava")
                    }
                """
            )
        }
        val arguments = listOf("metalavaGenerateSignature") + if (isConfigurationCacheEnabled) {
            listOf("--configuration-cache")
        } else {
            emptyList()
        }
        val result = gradleRunner
            .withArguments(arguments)
            .build()
        assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `check plugin reports warning for unsupported module`(isConfigurationCacheEnabled: Boolean) {
        buildscriptFile = testProjectDir.resolve("build.gradle.kts").apply {
            appendText(
                """
                    allprojects {
                        repositories {
                            google()
                            mavenCentral()
                        }
                    }
    
                    plugins {
                        id("me.tylerbwong.gradle.metalava")
                    }
                """
            )
        }
        val arguments = if (isConfigurationCacheEnabled) {
            listOf("--configuration-cache")
        } else {
            emptyList()
        }
        val result = gradleRunner
            .withArguments(arguments)
            .build()
        assertTrue(result.output.contains("not supported by the Metalava Gradle plugin"))
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `check addSourcePaths propagates task dependency`(isConfigurationCacheEnabled: Boolean) {
        buildscriptFile = testProjectDir.resolve("build.gradle.kts").apply {
            appendText(
                """
                    allprojects {
                        repositories {
                            google()
                            mavenCentral()
                        }
                    }
    
                    plugins {
                        `java-library`
                        id("me.tylerbwong.gradle.metalava")
                    }
                    
                    val customSourceGeneratingTaskProvider = tasks.register("customSourceGeneratingTask") {
                        val outputDir = file("customSrc/")
                        outputs.dir(outputDir)
                        mkdir(outputDir)
                    }
                    
                    metalava {
                        addSourcePaths(customSourceGeneratingTaskProvider.map { it.outputs.files })
                    }
                """
            )
        }
        val arguments = listOf("metalavaGenerateSignature") + if (isConfigurationCacheEnabled) {
            listOf("--configuration-cache")
        } else {
            emptyList()
        }
        val result = gradleRunner
            .withArguments(arguments)
            .build()
        assertTrue(result.tasks.any { it.path == ":customSourceGeneratingTask" })
    }

    @AfterEach
    fun tearDown() {
        buildscriptFile.delete()
    }
}

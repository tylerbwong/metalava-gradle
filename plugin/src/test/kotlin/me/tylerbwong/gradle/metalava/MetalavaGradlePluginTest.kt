package me.tylerbwong.gradle.metalava

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `check metalavaGenerateSignature runs successfully with no source`(
        isConfigurationCacheEnabled: Boolean,
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
                """,
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
                """,
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
                        additionalSourceSets.setFrom(customSourceGeneratingTaskProvider.map { it.outputs.files })
                    }
                """,
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

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `check outputSignatureFileProvider creates dependency on generation task`() {
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

                    tasks.register("customTask") {
                        inputs.file(metalava.outputSignatureFileProvider)
                        doFirst { }
                    }
                """,
            )
        }
        val result = gradleRunner
            .withArguments("customTask")
            .build()
        assertTrue(result.tasks.any { it.path == ":metalavaGenerateSignature" })
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `should ignore debug source sets by default`(debug: Boolean) {
        buildscriptFile = testProjectDir.resolve("build.gradle.kts").apply {
            writeText(
                """
                   allprojects {
                        repositories {
                            google()
                            mavenCentral()
                        }
                    }

                    plugins {
                        id("com.android.library")
                        id("me.tylerbwong.gradle.metalava")
                    }

                    android {
                        namespace = "com.example"
                        compileSdk = 36
                    }
                """.trimIndent(),
            )
        }
        testProjectDir.resolve("src/main/java/com/example/Foo.java").apply {
            parentFile.mkdirs()
            writeText(
                """
                    package com.example;

                    public final class Foo {}
                """,
            )
        }
        testProjectDir.resolve("src/debug/java/com/example/Bar.java").apply {
            parentFile.mkdirs()
            writeText(
                """
                    package com.example

                    public final class Bar {}
                """,
            )
        }
        testProjectDir.resolve("src/release/java/com/example/FooBar.kt").apply {
            parentFile.mkdirs()
            writeText(
                """
                    package com.example

                    public final class FooBar {}
                """,
            )
        }

        val metalavaTask = if (debug) "metalavaGenerateSignatureDebug" else "metalavaGenerateSignatureRelease"
        gradleRunner
            .withArguments(metalavaTask)
            .build()

        val expected = if (debug) {
            """
            // Signature format: 4.0
            package com.example {

              public final class Foo {
                ctor public Foo();
              }

            }
            """.trimIndent()
        } else {
            """
            // Signature format: 4.0
            package com.example {

              public final class Foo {
                ctor public Foo();
              }

              public final class FooBar {
                ctor public FooBar();
              }

            }
            """.trimIndent()
        }

        assertEquals(
            expected,
            testProjectDir.resolve("api.txt").readText().trimEnd(),
        )
    }

    @AfterEach
    fun tearDown() {
        buildscriptFile.delete()
    }
}

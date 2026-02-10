package me.tylerbwong.gradle.metalava

import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class MetalavaGradlePluginTest {

    @TempDir
    lateinit var testProjectDir: File
        private set

    private val buildscriptFile: File get() = testProjectDir.resolve("build.gradle.kts")

    @Test
    fun `check metalavaGenerateSignature runs successfully with no source`() {
        buildscriptFile.apply {
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
        val result = runner("metalavaGenerateSignature")
            .build()
        assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }

    @Test
    fun `check plugin reports warning for unsupported module`() {
        buildscriptFile.apply {
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
        val result = runner()
            .build()
        assertTrue(result.output.contains("not supported by the Metalava Gradle plugin"))
    }

    @Test
    fun `check addSourcePaths propagates task dependency`() {
        buildscriptFile.apply {
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
        val result = runner("metalavaGenerateSignature")
            .build()
        assertTrue(result.tasks.any { it.path == ":customSourceGeneratingTask" })
    }

    @Test
    fun `check outputSignatureFileProvider creates dependency on generation task`() {
        buildscriptFile.apply {
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
        val result = runner("customTask")
            .build()
        assertTrue(result.tasks.any { it.path == ":metalavaGenerateSignature" })
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `should ignore debug source sets by default`(debug: Boolean) {
        buildscriptFile.apply {
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
        runner(metalavaTask)
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

    private fun runner(vararg arguments: String): GradleRunner {
        return GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(testProjectDir)
            .withTestKitDir(testKitDir.toFile())
            .withArguments(arguments.toList() + commonGradleArgs)
    }
}

private val testKitDir by lazy {
    val gradleUserHome = System.getenv("GRADLE_USER_HOME")
        ?: Path(System.getProperty("user.home"), ".gradle").absolutePathString()
    Path(gradleUserHome, "testkit")
}

private val commonGradleArgs = setOf(
    "--configuration-cache",
    "--stacktrace",
)

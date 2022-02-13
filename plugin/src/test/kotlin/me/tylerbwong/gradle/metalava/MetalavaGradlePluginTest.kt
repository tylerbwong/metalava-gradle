package me.tylerbwong.gradle.metalava

import org.gradle.testkit.runner.GradleRunner
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class MetalavaGradlePluginTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    private lateinit var buildscriptFile: File
    private lateinit var gradleRunner: GradleRunner

    @Before
    fun setUp() {
        gradleRunner = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(testProjectDir.root)
            .withTestKitDir(testProjectDir.newFolder())
    }

    @Test
    fun `check metalavaGenerateSignature runs successfully with no source`() {
        buildscriptFile = testProjectDir.newFile("build.gradle.kts").apply {
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
        val result = gradleRunner
            .withArguments("metalavaGenerateSignature")
            .build()
        assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }

    @Test
    fun `check plugin reports warning for unsupported module`() {
        buildscriptFile = testProjectDir.newFile("build.gradle.kts").apply {
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
        val result = gradleRunner.build()
        assertTrue(result.output.contains("not supported by the Metalava Gradle plugin"))
    }

    @After
    fun tearDown() {
        buildscriptFile.delete()
    }
}

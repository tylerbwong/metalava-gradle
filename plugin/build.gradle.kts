import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlintGradle)
    alias(libs.plugins.pluginPublish)
}

repositories {
    google()
    mavenCentral()
}

group = "me.tylerbwong.gradle.metalava"
version = "0.4.0-alpha03"

gradlePlugin {
    website.set("https://github.com/tylerbwong/metalava-gradle")
    vcsUrl.set("https://github.com/tylerbwong/metalava-gradle")
    plugins {
        create("metalavaPlugin") {
            id = "me.tylerbwong.gradle.metalava"
            displayName = "Metalava Gradle Plugin"
            description = "A Gradle plugin for Metalava, AOSP's tool for API metadata extraction and compatibility tracking."
            tags.set(listOf("metalava", "api-tracking"))
            implementationClass = "me.tylerbwong.gradle.metalava.plugin.MetalavaPlugin"
        }
    }
}

kotlin {
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation { enabled = true }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(kotlin("gradle-plugin"))
    compileOnly(libs.androidGradle)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiterParams)
    testRuntimeOnly(libs.junit.platformLauncher)
}

tasks.check {
    dependsOn(
        // TODO: https://youtrack.jetbrains.com/issue/KT-78525
        tasks.checkLegacyAbi,
    )
}

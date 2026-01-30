plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.ktlintGradle)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.pluginPublish)
    alias(libs.plugins.metalavaGradle)
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
    explicitApi()
}

lint {
    baseline = file("lint-baseline.xml")
    warningsAsErrors = true
    disable += "NewerVersionAvailable"
    disable += "GradleDependency"
    disable += "AndroidGradlePluginVersion"
}

metalava {
    filename.set("api/${project.version}.txt")
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

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)

    lintChecks(libs.androidx.gradlePluginLints)
}

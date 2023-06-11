plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.ktlintGradle)
    alias(libs.plugins.pluginPublish)
    alias(libs.plugins.metalavaGradle)
    MetalavaGradleProjectPlugin
}

group = "me.tylerbwong.gradle.metalava"
version = "0.3.4-SNAPSHOT"

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

metalava {
    filename.set("api/${project.version}.txt")
    sourcePaths.setFrom("src/main")
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
}

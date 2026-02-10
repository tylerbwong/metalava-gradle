plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.ktlintGradle)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.pluginPublish)
    alias(libs.plugins.metalavaGradle)
    alias(libs.plugins.buildConfig)
}

repositories {
    google()
    mavenCentral()
}

group = "me.tylerbwong.gradle.metalava"
version = "0.5.0"

buildConfig {
    generateAtSync = true
    packageName = "me.tylerbwong.gradle.metalava"
    sourceSets.named("main") {
        buildConfigField("METALAVA_VERSION", libs.versions.android.metalava)
    }
}

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
    version = libs.versions.android.metalava
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val testPluginClasspath by configurations.registering {
    isCanBeResolved = true
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(kotlin("gradle-plugin"))
    compileOnly(libs.androidGradle)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)

    testPluginClasspath(libs.androidGradle)

    lintChecks(libs.androidx.gradlePluginLints)
}

tasks.pluginUnderTestMetadata {
    // Plugins used in tests could be resolved in classpath.
    pluginClasspath.from(
        testPluginClasspath,
    )
}

tasks.whenTaskAdded {
    if (name == "metalavaCheckCompatibility") {
        // TODO: there might be a bug, metalava tasks should run after generation tasks.
        dependsOn(tasks.generateBuildConfigClasses)
    }
}

tasks.validatePlugins {
    // TODO: https://github.com/gradle/gradle/issues/22600
    enableStricterValidation = true
}

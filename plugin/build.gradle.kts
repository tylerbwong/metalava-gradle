import org.gradle.api.plugins.JavaPlugin.API_ELEMENTS_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlintGradle)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.pluginPublish)
    alias(libs.plugins.metalavaGradle)
    alias(libs.plugins.buildConfig)
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
    website = "https://github.com/tylerbwong/metalava-gradle"
    vcsUrl = "https://github.com/tylerbwong/metalava-gradle"
    plugins {
        create("metalavaPlugin") {
            id = "me.tylerbwong.gradle.metalava"
            displayName = "Metalava Gradle Plugin"
            description = "A Gradle plugin for Metalava, AOSP's tool for API metadata extraction and compatibility tracking."
            tags = listOf("metalava", "api-tracking")
            implementationClass = "me.tylerbwong.gradle.metalava.plugin.MetalavaPlugin"
        }
    }
}

kotlin {
    explicitApi()
    compilerOptions {
        allWarningsAsErrors = true
        // https://docs.gradle.org/current/userguide/compatibility.html#kotlin
        apiVersion = KotlinVersion.KOTLIN_2_2
        languageVersion = apiVersion
        jvmTarget = JvmTarget.fromTarget(libs.versions.jvmTarget.get())
        jvmDefault = JvmDefaultMode.NO_COMPATIBILITY
        freeCompilerArgs.add("-Xjdk-release=${libs.versions.jvmTarget.get()}")
    }
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

configurations.named(API_ELEMENTS_CONFIGURATION_NAME) {
    attributes.attribute(
        // TODO: https://github.com/gradle/gradle/issues/24608
        GradlePluginApiVersion.GRADLE_PLUGIN_API_VERSION_ATTRIBUTE,
        objects.named(libs.versions.minGradle.get()),
    )
}

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.android.gradlePluginApi)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiterParams)
    testRuntimeOnly(libs.junit.platformLauncher)

    lintChecks(libs.androidx.gradlePluginLints)
}

tasks.withType<JavaCompile>().configureEach {
    options.release = libs.versions.jvmTarget.get().toInt()
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    // Required to test configuration cache in tests when using withDebug().
    // See https://github.com/gradle/gradle/issues/22765#issuecomment-1339427241.
    jvmArgs(
        "--add-opens=java.base/java.util=ALL-UNNAMED",
        "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED",
        "--add-opens=java.base/java.net=ALL-UNNAMED",
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

import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlintGradle)
    alias(libs.plugins.pluginPublish)
}

group = "me.tylerbwong.gradle.metalava"
version = "0.4.0-alpha03"

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
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation { enabled = true }
    explicitApi()
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

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.android.gradlePlugin)

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

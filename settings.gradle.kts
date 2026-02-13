pluginManagement {
    repositories {
        mavenCentral()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        gradlePluginPortal()
    }

    includeBuild("./plugin")
}

plugins {
    id("com.gradle.develocity") version "4.3.2"
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        val isCI = providers.environmentVariable("CI").isPresent
        publishing.onlyIf { isCI }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        gradlePluginPortal()
    }
}

include(
    ":samples:groovy-android",
    ":samples:groovy-java",
    ":samples:kotlin-dsl-android",
    ":samples:kotlin-dsl-compose",
    ":samples:kotlin-dsl-empty",
    ":samples:kotlin-dsl-kotlin",
    ":samples:kotlin-multiplatform",
)

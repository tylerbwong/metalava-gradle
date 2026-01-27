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
    ":samples:kotlin-multiplatform"
)

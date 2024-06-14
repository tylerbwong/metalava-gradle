rootProject.name = "metalava-gradle"

pluginManagement {
    includeBuild("./plugin")

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include(
    ":samples:groovy-android",
    ":samples:groovy-java",
    ":samples:kotlin-dsl-android",
    ":samples:kotlin-dsl-empty",
    ":samples:kotlin-dsl-kotlin",
    ":samples:kotlin-multiplatform"
)

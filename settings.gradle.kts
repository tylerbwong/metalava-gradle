rootProject.name = "metalava-gradle"

include(
    ":plugin",
    ":samples:groovy-android",
    ":samples:groovy-java",
    ":samples:kotlin-dsl-android",
    ":samples:kotlin-dsl-java",
    ":samples:kotlin-multiplatform"
)

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

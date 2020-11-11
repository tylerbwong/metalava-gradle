plugins {
    `kotlin-dsl`
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "0.12.0"
    id("maven-publish")
    id("me.tylerbwong.gradle.metalava")
}

group = "me.tylerbwong.gradle"
version = "0.1.0-alpha04"

gradlePlugin {
    plugins {
        create("metalavaPlugin") {
            id = "me.tylerbwong.gradle.metalava"
            implementationClass = "me.tylerbwong.gradle.metalava.plugin.MetalavaPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/tylerbwong/metalava-gradle"
    vcsUrl = "https://github.com/tylerbwong/metalava-gradle"
    description = "A Gradle plugin for Metalava, AOSP's tool for API metadata extraction and compatibility tracking."
    (plugins) {
        "metalavaPlugin" {
            displayName = "Metalava Gradle Plugin"
            tags = listOf("metalava", "api-tracking")
            version = "${project.version}"
        }
    }
    mavenCoordinates {
        groupId = "${project.group}"
        artifactId = rootProject.name
        version = "${project.version}"
    }
}

publishing {
    publications {
        create<MavenPublication>("pluginPublication") {
            from(components["kotlin"])
            groupId = "${project.group}"
            artifactId = rootProject.name
            version = "${project.version}"
        }
    }
}

metalava {
    filename = "api/${project.version}.txt"
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:gradle:4.2.0-alpha16")
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.12.0"
    id("me.tylerbwong.gradle.metalava")
}

group = "me.tylerbwong.gradle"
version = "0.1.6-SNAPSHOT"

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
    compileOnly(gradleApi())
    compileOnly(kotlin("gradle-plugin"))
    compileOnly(kotlin("stdlib"))
    compileOnly("com.android.tools.build:gradle:4.1.1")
}

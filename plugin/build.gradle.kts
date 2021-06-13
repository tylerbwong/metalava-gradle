plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.12.0"
    id("me.tylerbwong.gradle.metalava")
}

group = "me.tylerbwong.gradle"
version = "0.1.8-SNAPSHOT"

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
    compileOnly(libs.android.gradlePlugin)
}

/**
 * Attempts to grab Gradle Publish credentials from environment variables if the properties are not
 * present. Will be done before plugin publish.
 */
val setupPublishCredentialsTask = tasks.register("setupPublishCredentials") {
    doFirst {
        val keyProperty = "gradle.publish.key"
        val secretProperty = "gradle.publish.secret"

        if (!project.hasProperty(keyProperty) || !project.hasProperty(secretProperty)) {
            logger.warn("`$keyProperty` or `$secretProperty` were not set. Attempting to configure from environment variables")

            val key = System.getenv("GRADLE_PUBLISH_KEY")
            val secret = System.getenv("GRADLE_PUBLISH_SECRET")
            if (key != null && secret != null) {
                System.setProperty(keyProperty, key)
                System.setProperty(secretProperty, secret)
            } else {
                logger.warn("Publishing key or secret was null")
            }
        }
    }
}

tasks.getByName("publishPlugins").dependsOn(setupPublishCredentialsTask)

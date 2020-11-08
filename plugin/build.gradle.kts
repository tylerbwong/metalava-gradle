plugins {
    `kotlin-dsl`
    kotlin("jvm")
    id("maven-publish")
}

group = "me.tylerbwong.gradle"
version = "1.0.0-alpha01"

gradlePlugin {
    plugins {
        register("metalavaPlugin") {
            id = "me.tylerbwong.gradle.metalava"
            implementationClass = "me.tylerbwong.gradle.metalava.plugin.MetalavaPlugin"
        }
    }
}

publishing {
    publications {
        create("pluginPublication", MavenPublication::class) {
            from(components["java"])
            groupId = "${project.group}"
            artifactId = rootProject.name
            version = "${project.version}"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:gradle:4.2.0-alpha16")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
}

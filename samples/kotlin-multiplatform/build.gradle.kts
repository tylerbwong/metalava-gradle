plugins {
    kotlin("multiplatform")
    id("me.tylerbwong.gradle.metalava")
}

kotlin {
    ios()
    js {
        nodejs()
    }
    jvm()
    linuxX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val linuxX64Main by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
    }
}

metalava {
    filename = "api/$name-api.txt"
}

plugins {
    kotlin("multiplatform")
    id("me.tylerbwong.gradle.metalava")
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    js {
        nodejs()
    }
    jvm()
    linuxX64()
}

metalava {
    filename.set("api/$name-api.txt")
}

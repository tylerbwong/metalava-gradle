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
}

metalava {
    filename = "api/$name-api.txt"
}

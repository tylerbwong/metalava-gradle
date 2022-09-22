plugins {
    kotlin("multiplatform")
    alias(libs.plugins.metalavaGradle)
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
    filename.set("api/$name-api.txt")
}

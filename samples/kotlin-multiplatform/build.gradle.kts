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
    filename = "api/$name-api.txt"
}

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.metalavaGradle)
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

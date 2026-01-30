plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmpLibrary)
    alias(libs.plugins.metalava)
}

kotlin {
    android {
        namespace = "me.tylerbwong.gradle.metalava.sample"
        compileSdk = 34
        minSdk = 21
        androidResources.enable = true
    }
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
    filename = "api/$name-api.txt"
}

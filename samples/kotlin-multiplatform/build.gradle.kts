plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmpLibrary)
    alias(libs.plugins.metalava)
}

kotlin {
    android {
        namespace = "me.tylerbwong.gradle.metalava.sample"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
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

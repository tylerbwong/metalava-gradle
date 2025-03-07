plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("me.tylerbwong.gradle.metalava")
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    js {
        nodejs()
    }
    jvm()
    linuxX64()
}

android {
    namespace = "me.tylerbwong.gradle.metalava.sample"
    compileSdk = 34

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

metalava {
    filename.set("api/$name-api.txt")
}

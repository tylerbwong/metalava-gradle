import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.metalava)
}

kotlin {
    androidTarget {
        compilerOptions.jvmTarget = JvmTarget.JVM_17
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

android {
    namespace = "me.tylerbwong.gradle.metalava.sample"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

metalava {
    filename = "api/$name-api.txt"
}

import me.tylerbwong.gradle.metalava.Format

plugins {
    id("com.android.library")
    id("me.tylerbwong.gradle.metalava")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

metalava {
    outputFileName = "$name-api.txt"
}

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
}

metalava {
    outputFileName = "$name-api.txt"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
}

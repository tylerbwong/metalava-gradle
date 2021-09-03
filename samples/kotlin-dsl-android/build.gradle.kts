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
    filename = "api/$name-api.txt"
}

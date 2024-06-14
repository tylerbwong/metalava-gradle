plugins {
    id("com.android.library")
    id("me.tylerbwong.gradle.metalava")
}

android {
    namespace = "me.tylerbwong.gradle.metalava.sample"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }
}

metalava {
    filename.set("api/$name-api.txt")
}

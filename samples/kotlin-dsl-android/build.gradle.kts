plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.metalava)
}

android {
    namespace = "me.tylerbwong.gradle.metalava.sample"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
}

metalava {
    filename = "api/$name-api.txt"
}

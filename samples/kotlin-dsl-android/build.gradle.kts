plugins {
    id("com.android.library")
    alias(libs.plugins.metalavaGradle)
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}

metalava {
    filename = "api/$name-api.txt"
}

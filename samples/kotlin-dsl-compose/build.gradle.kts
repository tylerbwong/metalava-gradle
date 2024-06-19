plugins {
    id("com.android.library")
    alias(libs.plugins.compose.compiler)
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
    apiCompatAnnotations.set(listOf("androidx.compose.runtime.Composable"))
}

dependencies {
    implementation(libs.androidx.compose.runtime)
}

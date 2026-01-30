import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compiler)
    alias(libs.plugins.metalava)
}

android {
    namespace = "me.tylerbwong.gradle.metalava.sample"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
    }
}

metalava {
    filename = "api/$name-api.txt"
    apiCompatAnnotations = listOf("androidx.compose.runtime.Composable")
}

dependencies {
    implementation(libs.androidx.compose.runtime)
}

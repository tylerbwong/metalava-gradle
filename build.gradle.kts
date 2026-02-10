import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kmpLibrary) apply false
    alias(libs.plugins.kotlin.compiler) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
}

allprojects {
    plugins.withType<AndroidBasePlugin>().configureEach {
        extensions.configure<CommonExtension> {
            compileOptions.apply {
                sourceCompatibility(libs.versions.jvmTarget.get())
                targetCompatibility(libs.versions.jvmTarget.get())
            }
        }
    }

    plugins.withType<JavaBasePlugin>().configureEach {
        extensions.configure<JavaPluginExtension> {
            setSourceCompatibility(libs.versions.jvmTarget.get())
            setTargetCompatibility(libs.versions.jvmTarget.get())
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(libs.versions.jvmTarget.get())
        }
    }
}

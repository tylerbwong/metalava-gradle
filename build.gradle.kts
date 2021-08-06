buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
        classpath("me.tylerbwong.gradle:metalava-gradle:0.1.9")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

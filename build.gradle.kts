buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
        classpath("me.tylerbwong.gradle:metalava-gradle:0.1.8")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

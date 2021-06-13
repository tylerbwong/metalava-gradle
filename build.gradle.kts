buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath("me.tylerbwong.gradle:metalava-gradle:0.1.8")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

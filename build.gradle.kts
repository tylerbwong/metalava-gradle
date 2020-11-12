buildscript {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
        mavenLocal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha16")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
        classpath("me.tylerbwong.gradle:metalava-gradle:0.1.0-alpha05")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

buildscript {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
        mavenLocal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
        classpath("me.tylerbwong.gradle:metalava-gradle:0.1.0-alpha06")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

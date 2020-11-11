buildscript {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha16")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
        classpath("me.tylerbwong.gradle:metalava-gradle:0.1.0-alpha02-SNAPSHOT")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

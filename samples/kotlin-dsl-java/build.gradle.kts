plugins {
    `java-library`
    id("me.tylerbwong.gradle.metalava")
}

metalava {
    outputFileName = "api/$name-api.txt"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
}

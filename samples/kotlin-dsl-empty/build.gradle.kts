plugins {
    kotlin("jvm")
    id("me.tylerbwong.gradle.metalava")
}

metalava {
    filename.set("api/$name-api.txt")
    excludedSourceSets.setFrom("src/main/kotlin")
}

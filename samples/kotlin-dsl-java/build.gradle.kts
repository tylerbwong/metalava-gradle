plugins {
    `java-library`
    id("me.tylerbwong.gradle.metalava")
}

metalava {
    filename.set("api/$name-api.txt")
}

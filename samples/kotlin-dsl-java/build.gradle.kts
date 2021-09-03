plugins {
    `java-library`
    id("me.tylerbwong.gradle.metalava")
}

metalava {
    filename = "api/$name-api.txt"
}

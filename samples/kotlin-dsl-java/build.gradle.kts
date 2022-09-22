plugins {
    `java-library`
    alias(libs.plugins.metalavaGradle)
}

metalava {
    filename.set("api/$name-api.txt")
}

plugins {
    `java-library`
    alias(libs.plugins.metalavaGradle)
}

metalava {
    filename = "api/$name-api.txt"
}

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.metalava)
}

metalava { filename = "api/$name-api.txt" }

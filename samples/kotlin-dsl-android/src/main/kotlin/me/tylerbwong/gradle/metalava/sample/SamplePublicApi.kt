package me.tylerbwong.gradle.metalava.sample

interface SamplePublicApi {
  val publicApiProperty: String

  fun publicApiFunction()

  fun publicApiFunctionWithDefaultValueParam(value: Int = 0)
}

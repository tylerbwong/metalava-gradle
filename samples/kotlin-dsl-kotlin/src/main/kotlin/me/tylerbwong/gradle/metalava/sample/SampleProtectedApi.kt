package me.tylerbwong.gradle.metalava.sample

abstract class SampleProtectedApi {
  abstract val publicApiProperty: String

  protected fun protectedApiFunction(): String {
    return publicApiProperty
  }
}

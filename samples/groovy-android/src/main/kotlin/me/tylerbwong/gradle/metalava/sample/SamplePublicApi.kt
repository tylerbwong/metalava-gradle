package me.tylerbwong.gradle.metalava.sample

import android.content.Context

interface SamplePublicApi {
    val publicApiProperty: String
    fun publicApiFunction()
    fun publicAndroidApi(context: Context)
}

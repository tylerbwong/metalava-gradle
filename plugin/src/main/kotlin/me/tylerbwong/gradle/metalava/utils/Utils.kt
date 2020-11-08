package me.tylerbwong.gradle.metalava.utils

internal val Boolean.flagValue: String get() = if (this) "yes" else "no"
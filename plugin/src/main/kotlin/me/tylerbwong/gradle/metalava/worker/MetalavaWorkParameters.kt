package me.tylerbwong.gradle.metalava.worker

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters

internal interface MetalavaWorkParameters : WorkParameters {
  val classpath: ConfigurableFileCollection
  val arguments: Property<String>
}

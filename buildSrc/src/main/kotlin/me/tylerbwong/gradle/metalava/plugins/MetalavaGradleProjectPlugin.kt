package me.tylerbwong.gradle.metalava.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class MetalavaGradleProjectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.registerSetupPublishCredentialsTask()
    }

    /**
     * Attempts to grab Gradle Publish credentials from environment variables if the properties are not
     * present. Will be done before plugin publish.
     */
    private fun Project.registerSetupPublishCredentialsTask() {
        val setupPublishCredentialsTask = tasks.register("setupPublishCredentials") {
            doFirst {
                val keyProperty = "gradle.publish.key"
                val secretProperty = "gradle.publish.secret"

                if (!project.hasProperty(keyProperty) || !project.hasProperty(secretProperty)) {
                    logger.warn("`$keyProperty` or `$secretProperty` were not set. Attempting to configure from environment variables")

                    val key = System.getenv("GRADLE_PUBLISH_KEY")
                    val secret = System.getenv("GRADLE_PUBLISH_SECRET")
                    if (key != null && secret != null) {
                        System.setProperty(keyProperty, key)
                        System.setProperty(secretProperty, secret)
                    } else {
                        logger.warn("Publishing key or secret was null")
                    }
                }
            }
        }
        tasks.getByName("publishPlugins").dependsOn(setupPublishCredentialsTask)
    }
}

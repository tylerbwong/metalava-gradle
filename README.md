# metalava-gradle
[![CI](https://github.com/tylerbwong/metalava-gradle/workflows/CI/badge.svg)](https://github.com/tylerbwong/metalava-gradle/actions?query=workflow%3ACI)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/me/tylerbwong/gradle/metalava/me.tylerbwong.gradle.metalava.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=Gradle%20Plugin%20Portal)](https://plugins.gradle.org/plugin/me.tylerbwong.gradle.metalava)

A Gradle plugin for [Metalava](https://android.googlesource.com/platform/tools/metalava/), AOSP's tool for API metadata extraction and compatibility tracking. This plugin is currently in active development and does not yet fully support all of Metalava's features. Currently it is only capable of generating API signature descriptor files.

### How it Works
This Gradle plugin serves as a wrapper for invoking the Metalava JAR directly. It will first download the JAR if needed from [here](https://storage.googleapis.com/android-ci/metalava-full-1.3.0-SNAPSHOT.jar) if no local JAR is found and then run it using the passed in arguments from `MetalavaExtension`.

### Supported Plugins

This plugin currently supports the following plugins:

* `com.android.library`
* `java-library`
* `kotlin("multiplatform")

### Setup

#### Kotlin

```kt
buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("me.tylerbwong.gradle:metalava-gradle:<current_version>")
    }
}

apply(plugin = "me.tylerbwong.gradle.metalava")
```

or, using the new plugin API

```kt
plugins {
    id("me.tylerbwong.gradle.metalava") version "<current_version>"
}
```

#### Groovy

```groovy
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "me.tylerbwong.gradle:metalava-gradle:<current_version>"
    }
}

apply plugin: "me.tylerbwong.gradle.metalava"
```

### Usage

Applying this plugin to a given module will generate two tasks:

1. `downloadMetalavaJar` - Downloads and caches the Metalava JAR in the root project's build folder if one does not exist at the same location already.
2. `metalavaSignature` - Executes the Metalava JAR with the following arguments configured in `MetalavaExtension`. If no custom JAR location is provided, it will use the result of the download task.

The plugin can also be configured using the `metalava` extension block

```kt
plugins {
    id("me.tylerbwong.gradle.metalava") version "<current_version>"
}

...

metalava {
    documentation = Documentation.PUBLIC
    shouldOutputKotlinNulls = false
    shouldIncludeSignatureVersion = false
    ...
}
```

See [`MetalavaExtension`](https://github.com/tylerbwong/metalava-gradle/blob/main/plugin/src/main/kotlin/me/tylerbwong/gradle/metalava/extension/MetalavaExtension.kt) for all configurable options.

### License

    Copyright 2020 Tyler Wong

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

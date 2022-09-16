# metalava-gradle
[![Build](https://github.com/tylerbwong/metalava-gradle/actions/workflows/build.yml/badge.svg)](https://github.com/tylerbwong/metalava-gradle/actions/workflows/build.yml)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/me/tylerbwong/gradle/metalava/me.tylerbwong.gradle.metalava.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=Gradle%20Plugin%20Portal)](https://plugins.gradle.org/plugin/me.tylerbwong.gradle.metalava)
[![Metalava](https://img.shields.io/badge/Metalava-1.0.0--alpha06-orange)](https://maven.google.com/web/index.html#com.android.tools.metalava:metalava:1.0.0-alpha06)

A Gradle plugin for [Metalava](https://android.googlesource.com/platform/tools/metalava/), AOSP's tool for API metadata extraction and compatibility tracking.

### Supported Plugins

This plugin currently supports the following plugins:

* `com.android.library`
* `java-library`
* `kotlin("multiplatform")`

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

Also ensure that [Google's Maven Repository](https://docs.gradle.org/current/userguide/declaring_repositories.html#sub:maven_google) is added to your project.

### Usage

This plugin registers the following tasks:

`metalavaGenerateSignature` - Generates a Metalava signature descriptor file at a specified location.

`metalavaCheckCompatibility` - Checks API compatibility between the code base and the current API.

The plugin can also be configured using the `metalava` extension block

```kt
plugins {
    id("me.tylerbwong.gradle.metalava") version "<current_version>"
}

...

metalava {
    documentation = Documentation.PUBLIC
    outputKotlinNulls = false
    includeSignatureVersion = false
    ...
}
```

Check out the [samples](https://github.com/tylerbwong/metalava-gradle/tree/main/samples) for more example usages and see [`MetalavaExtension`](https://github.com/tylerbwong/metalava-gradle/blob/main/plugin/src/main/kotlin/me/tylerbwong/gradle/metalava/extension/MetalavaExtension.kt) for all configurable options.

### License

    Copyright 2022 Tyler Wong

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

# metalava-gradle
![CI](https://github.com/tylerbwong/metalava-gradle/workflows/CI/badge.svg)

A Gradle plugin for [Metalava](https://android.googlesource.com/platform/tools/metalava/), AOSP's tool for API compatibility tracking. This plugin is currently in active development and does not yet fully support all of Metalava's features. Currently it is only capable of generating API signature descriptor files.

### Supported Plugins

This plugin currently supports the following plugins:

* `com.android.library`
* `java-library`

### Setup

<details>
<summary>Groovy</summary>

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
</details>
<details open>
<summary>Kotlin</summary>

```kotlin
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

```kotlin
plugins {
    id("me.tylerbwong.gradle.metalava") version "<current_version>"
}
```
</details>

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

# 📦 Dependencies JSON Plugin

[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/dev.mbo.djp.dependencies-json)](https://plugins.gradle.org/plugin/dev.mbo.djp.dependencies-json)

> A Gradle plugin to export resolved dependencies as a neatly formatted JSON file — grouped by configuration (e.g.,
`implementation`, `testImplementation`, etc.)

---

## ✨ Features

- 🔍 Outputs all resolved dependencies
- 📄 Grouped by configuration (`implementation`, `test`, etc.)
- 💾 Exports to a customizable JSON file
- ⚡ Lightweight, fast, and zero dependencies
- ⬆️ Http POST the generated file to a configurable server with optional basic credentials.
- ⬆️ There is also a spring boot api to receive the json file content sent with the plugin.
  See [dependencies-json-server](https://github.com/mbogner/dependencies-json-server)

---

## 🚀 Getting Started

### ✅ Apply the Plugin

```kotlin
plugins {
    id("dev.mbo.djp.dependencies-json") version ("1.0.0")
}
```

> 📦 The plugin is available from
> the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/dev.mbo.djp.dependencies-json)

---

### ⚙️ Usage

#### dependencies-json

Run the task from the command line:

```bash
./gradlew dependencies-json
```

By default, the plugin will generate a file at:

```
<project-root>/build/dependencies.json
```

#### dependencies-json-upload

The upload task can be run with:

```bash
./gradlew dependencies-json-upload
```

with a mandatory configuration where you set the server:

```kotlin
dependenciesJson {
    postUrl.set("URL to post the file to as application/json")
}
```

You can set credentials by using environment variables:

- `DJP_HTTP_USER`: username
- `DJP_HTTP_PASS`: password

Both need to be present so that they get used. This adds an Authorization header like:

```text
Authorization: Basic base64encoded(username:password)
```

This is automatically using the generated file from `dependencies-json` and if that isn't run before
this is done automatically.

---

### 🧩 Configuration

You can customize the file output location of `dependencies-json` using the `dependenciesJson` extension:

```kotlin
dependenciesJson {
    outputFile.set(layout.buildDirectory.file("custom-location/my-deps.json"))
}
```

---

## 📦 Sample Output

```json
{
  "implementation": [
    {
      "group": "com.google.guava",
      "name": "guava",
      "version": "31.1-jre",
      "file": "guava-31.1-jre.jar"
    }
  ],
  "testImplementation": [
    {
      "group": "org.junit.jupiter",
      "name": "junit-jupiter-api",
      "version": "5.12.2",
      "file": "junit-jupiter-api-5.12.2.jar"
    }
  ]
}
```

---

## 🧪 Running Tests

To run both unit and functional tests:

```bash
./gradlew check
```

You can find functional test reports under:

```
build/reports/tests/functionalTest
```

---

## 🙌 Contributing

1. Fork the repo
2. Create your feature branch: `git checkout -b feature/my-thing`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/my-thing`
5. Open a pull request

---

## 📄 License

see [LICENSE.txt](LICENSE.txt)

---

## 💬 Feedback

Have an idea or found a bug? [Open an issue](https://github.com/mbogner/dependencies-json-plugin/issues) or contribute a
PR!

## Release

```shell
./gradlew release \
  -Prelease.useAutomaticVersion=true \
  -Prelease.releaseVersion=1.0.0 \
  -Prelease.newVersion=1.0.1-SNAPSHOT \
  --no-configuration-cache
```
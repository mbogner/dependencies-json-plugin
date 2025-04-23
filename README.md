# 📦 Dependencies JSON Plugin

[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/dev.mbo.djp.dependencies-json)](https://plugins.gradle.org/plugin/dev.mbo.djp.dependencies-json)

> A Gradle plugin to export resolved dependencies as a neatly formatted JSON file — grouped by configuration (e.g., `implementation`, `testImplementation`, etc.)

---

## ✨ Features

- 🔍 Outputs all resolved dependencies
- 📄 Grouped by configuration (`implementation`, `test`, etc.)
- 💾 Exports to a customizable JSON file
- ⚡ Lightweight, fast, and zero dependencies

---

## 🚀 Getting Started

### ✅ Apply the Plugin

```kotlin
plugins {
    id("dev.mbo.djp.dependencies-json") version("1.0.0")
}
```

> 📦 The plugin is available from the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/dev.mbo.djp.dependencies-json)

---

### ⚙️ Usage

Run the task from the command line:

```bash
./gradlew dependencies-json
```

By default, the plugin will generate a file at:

```
<project-root>/build/dependencies.json
```

---

### 🧩 Configuration

You can customize the output location using the `dependenciesJson` extension:

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

Have an idea or found a bug? [Open an issue](https://github.com/mbogner/dependencies-json-plugin/issues) or contribute a PR!

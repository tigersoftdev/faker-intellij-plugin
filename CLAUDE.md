# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

IntelliJ plugin that integrates the [datafaker](https://github.com/datafaker-net/datafaker) library into IntelliJ's Generate menu (Alt+Insert). It dynamically builds a hierarchical menu of fake data categories/generators using reflection on the Faker class, and inserts generated text at the cursor position.

## Build Commands

```bash
# Build the plugin
./gradlew build

# Run tests
./gradlew test

# Run plugin in a sandboxed IntelliJ instance
./gradlew runIde

# Verify plugin compatibility across IntelliJ versions
./gradlew runPluginVerifier

# Build plugin distribution zip
./gradlew buildPlugin
```

## Architecture

- **FakerActionGroup** (`actions/FakerActionGroup.java`) — Entry point. Extends IntelliJ's `ActionGroup`, delegates to `UiHelper` inner class for building the two-level popup menu and `UiUpdater` for inserting text into the editor.

- **ReflectionUtils** (`actions/utils/ReflectionUtils.java`) — Handles reflection-based discovery of faker categories and subcategories, including method filtering (excluded classes/methods) and sorting.

- **MethodWrapper** (`actions/utils/MethodWrapper.java`) — Wraps a `Method` with pretty name formatting and safe invocation with error handling via `FakerNotifier`.

- **FakerNotifier** (`FakerNotifier.java`) — Notification wrapper using IntelliJ's `NotificationGroupManager` with the "FakerEvents" group.

Plugin entry point is registered in `src/main/resources/META-INF/plugin.xml` as an action added to IntelliJ's `GenerateGroup`.

## Key Technical Details

- Java 17 source/target compatibility
- Gradle build with IntelliJ Platform Gradle plugin (1.x)
- Targets IntelliJ 2021.2+ (sinceBuild 212, no upper bound)
- Plugin description and changelog are in `src/main/resources/META-INF/description.html` and `changes.html`, injected via `patchPluginXml` task
- Dependency versions are managed in `gradle.properties`
- Avoid `AnActionEvent.getRequiredData()` — it is scheduled for removal. Use `getData()` with null checks instead.
- When updating datafaker version, update the expected category count in `ReflectionUtilsTest`
- Mockito and JUnit versions must stay compatible with the IntelliJ Gradle plugin 1.x test runner (JUnit 5.10.x, avoid junit-platform 1.12+)
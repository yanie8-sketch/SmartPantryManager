# Smart Pantry Manager

## Description

Smart Pantry Manager is a Java-based Android application designed to help users manage their pantry ingredients and discover recipes based on the ingredients they currently have available.

The application allows users to add, edit and delete pantry ingredients while keeping track of quantities, units and expiry dates. It also provides recipe suggestions based on pantry availability.

## Features

* Add pantry ingredients
* Edit pantry ingredients
* Delete pantry ingredients
* Track ingredient quantity and unit
* Track expiry dates
* Clear all pantry ingredients
* View recipes that are ready to make
* View recipes that are almost ready to make
* View missing ingredients for partial recipes
* View recipe preparation methods
* Settings screen
* Local offline database
* Bottom navigation between main sections
* Responsive Android user interface

## Technology Used

* Java
* Android Studio
* Android SDK
* SQLite
* RecyclerView
* Android XML layouts

## Database

The application uses **SQLite** through Android's `SQLiteOpenHelper`.

SQLite was selected because it is built into the Android platform, works without an external database server, supports offline use and is suitable for storing the application's local pantry and recipe information.

The database contains tables for:

* Pantry items
* Recipes
* Recipe ingredients

## Main Screens

### Pantry

The Pantry screen allows users to view their stored ingredients and add, edit or delete items.

### Ready to Make

The Ready to Make screen displays recipes that can be prepared using the ingredients currently available in the pantry.

### Almost There

The Almost There screen displays recipes where the user has some, but not all, of the required ingredients.

### Recipe Details

The Recipe Details screen displays the recipe description, required ingredients and preparation method.

### Settings

The Settings screen provides access to application settings.

## Setup and Installation

1. Clone or download this repository.
2. Open the project in Android Studio.
3. Allow Android Studio to complete the Gradle sync.
4. Make sure the Android SDK is configured.
5. Connect an Android device or create an Android Emulator.
6. Build the project.
7. Run the application.

## Project Structure


SmartPantryManager/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/smartpantry/
│           │
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   └── values/
│           │
│           └── AndroidManifest.xml
│
├── gradle/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

## Database Design

The application uses a local SQLite database with the following main entities:

**Pantry Items**

Stores the ingredients currently available to the user.

**Recipes**

Stores recipe names, descriptions and preparation methods.

**Recipe Ingredients**

Stores the ingredients required by each recipe and associates them with the relevant recipe.

## Recipe Matching

The application compares pantry ingredients with recipe requirements.

Recipes are separated into:

* **Ready to Make** — all required ingredients and quantities are available.
* **Almost There** — some required ingredients are available, but one or more ingredients are still missing.

This allows the user to quickly identify recipes they can prepare immediately as well as recipes they could prepare after obtaining the missing ingredients.

## Offline Support

The core pantry and recipe functionality is stored locally using SQLite. This allows the application to operate without requiring an internet connection or an external database server.

## Screenshots

The repository contains screenshots demonstrating the application's interface and functionality.

## Project

Smart Pantry Manager was developed as an Android application using Java and Android Studio.

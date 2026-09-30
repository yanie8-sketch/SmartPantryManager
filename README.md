# Smart Pantry Manager

A Java + SQLite Android Studio project for Mobile App Development 700.

## What the app does
Smart Pantry Manager stores ingredients the user already has and only suggests recipes when **every required ingredient is present in at least the required quantity**. The app includes pantry CRUD, seeded recipes, strict matching, recipe details and settings.

## Assignment coverage
- Java-only Android application
- SQLiteOpenHelper local persistence
- Pantry Create / Read / Update / Delete
- RecyclerView + custom adapters
- 20 seeded recipes
- Strict quantity-aware recipe matching
- Basic singular/plural normalization
- Pantry, Add/Edit, Suggestions, Recipe Detail and Settings activities
- Intents between activities
- Input validation
- Navigation buttons
- Green/dark-green professional UI
- Hover-state button styling plus touch press scale/ripple-style visual feedback
- Empty-state feedback when there are no strict matches

## Open and run
1. Open this `SmartPantryManager` folder in Android Studio.
2. Allow Android Studio to sync Gradle.
3. Use JDK 17.
4. Create/select an Android emulator or connect a physical Android device.
5. Run the `app` configuration.

## Database choice
SQLite was chosen because it is local, simple to demonstrate during a video, persists data after the application closes, and directly demonstrates the module's persistent-storage concepts.

## Strict matching
`IngredientMatcher.canMake()` builds a normalized pantry quantity map. Each recipe ingredient is then checked. If any required ingredient is missing or the available quantity is too small, the entire recipe is rejected. A recipe is therefore never displayed as suggested when it is only an "almost match".

## UI interaction effects
`bg_primary_button.xml` and `bg_secondary_button.xml` include `state_hovered` and `state_pressed` selectors. `MotionFeedback` adds a small scale/alpha animation on touch-down and touch-release so mobile taps feel deliberate and responsive.

## Suggested demo test
Add:
- 2 eggs
- 1 tomato
- 0.5 onion
- 1 g salt

Then open Suggested Recipes. Tomato Omelette should appear. Delete tomato and refresh Suggestions; it should disappear and appear in the Almost There window. This demonstrates the strict rule.



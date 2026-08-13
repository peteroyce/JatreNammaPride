# Jatre Namma Pride

An Android app that acts as a digital companion for a Karnataka *jatre* (village fair): the event schedule, a lost-and-found board, a venue guide with walking directions, and short cultural write-ups. It is built to be useful on a fairground, so the whole app runs from a local database and works with no network and no backend configured.

Kotlin · Jetpack Compose · Material 3 · Room · MIT licensed

## Features

- **Event schedule** with category filters (Religious, Cultural, Sports, Food) backed by a Room query per category.
- **Live countdown** to each event start, recomputed on a one-second tick inside a Compose `LaunchedEffect`.
- **Lost and found board** — post a Lost or Found entry with description, contact, last-seen location and an optional photo picked via `ActivityResultContracts`; entries can be marked resolved, which removes them from the active list.
- **Venue guide** listing parking, first aid, stalls, entry and exit markers, filterable by marker type.
- **Walking directions** — each marker opens `google.navigation:q=<lat>,<lng>&mode=w` in Google Maps, falling back to the Maps web URL if the app is not installed.
- **Cultural stories** — short narratives tied to a named jatre, preloaded on first launch.
- **Admin screen** for creating and deleting events and moderating lost-item posts.
- **Seeded on first run** — each repository preloads sample rows only if its table is empty, so a fresh install has something to show without any setup.

## Architecture

MVVM with a single-source-of-truth Room database. ViewModels expose `StateFlow`; screens collect them. Filtering is done in the ViewModel over a DAO `Flow`, so a filter change never re-queries the database on the main thread.

```
MainActivity ──> NavGraph ──> HomeScreen (bottom nav, 4 tabs)
                    │              ├── ScheduleScreen  ── ScheduleViewModel  ┐
                    │              ├── LostFoundScreen ── LostFoundViewModel ┤
                    │              ├── MapScreen       ── MapViewModel       ┤ (markers are in-memory)
                    │              └── StoriesScreen   ── StoriesViewModel   ┤
                    ├── PostLostItemScreen ────────────── LostFoundViewModel ┤
                    └── AdminScreen ───────────────────── AdminViewModel     ┘
                                                                             │
                                          EventRepository / LostItemRepository / StoryRepository
                                                                             │
                                                        Room (AppDatabase, v1, 3 entities)
```

Only `home`, `post_lost_item` and `admin` are navigation destinations. The four main sections are tabs swapped inside `HomeScreen`, which keeps their state alive across tab switches without a nav backstack.

| Package | Contents |
|---|---|
| `data/local` | `AppDatabase` singleton and DAOs for events, lost items, stories |
| `data/model` | `Event`, `LostItem`, `Story` (Room entities) and `MapMarker` (plain data class) |
| `data/remote` | `FirebaseDataSource` — a Firestore/Storage source with local fallback, written but not yet wired into any repository |
| `data/repository` | Repository per entity, including first-run seeding |
| `ui/*` | One package per screen: composables plus its ViewModel |
| `util` | `DateUtils` — time, date, range and relative-time formatting |

Firebase dependencies (Firestore, Storage, Auth, Analytics) are declared in Gradle, and `FirebaseDataSource` implements snapshot listeners with a graceful fallback when `google-services.json` is absent. The active data path today is Room only: `LostItemRepository.uploadImage` returns a placeholder URL rather than uploading, and `FirebaseDataSource` has no callers. Treat the cloud layer as scaffolding, not a live sync.

## Quickstart

Requires JDK 17 and the Android SDK (compile/target 34). The Gradle wrapper pins Gradle 8.5.

```bash
git clone https://github.com/peteroyce/JatreNammaPride.git
cd JatreNammaPride

./gradlew assembleDebug        # build the debug APK
./gradlew installDebug         # build and install on a connected device
```

Or open the project root in Android Studio and run the `app` configuration.

No API keys or config files are needed. `google-services.json` may be dropped into `app/` if you intend to develop the Firebase path; the app builds and runs without it. Minimum SDK is 28 (Android 9). The only manifest permission is `INTERNET`.

## Usage

Entities are plain Room data classes, so seeding or extending the schedule is a matter of inserting rows:

```kotlin
val repository = EventRepository(AppDatabase.getInstance(context))

repository.addEvent(
    Event(
        title = "Rathotsava Procession",
        description = "The grand chariot procession through the main streets.",
        startTime = System.currentTimeMillis() + 2 * 3_600_000L,
        endTime = System.currentTimeMillis() + 5 * 3_600_000L,
        location = "Temple Main Road",
        category = "Religious",   // Religious | Cultural | Sports | Food
        status = "Upcoming"       // Upcoming | Ongoing | Completed
    )
)

repository.getEventsByCategory("Religious")   // Flow<List<Event>>
```

`AppDatabase` uses `fallbackToDestructiveMigration()`. Schema changes wipe local data — acceptable for an app whose content is seeded and remotely sourced, but worth changing before any release that stores user-authored posts long term.

## Tech stack

Kotlin 1.9.22 · Jetpack Compose (BOM 2024.02.00) · Material 3 · Navigation Compose 2.7.7 · Room 2.6.1 with KSP · Coil 2.5.0 · Coroutines 1.7.3 · Firebase BOM 32.7.2 · AGP 8.2.2 · Gradle 8.5 · JVM target 17

## Testing

Local JVM unit tests cover the data models and `DateUtils`:

```bash
./gradlew test
```

Espresso and Compose UI test dependencies are configured, but no instrumented tests are committed.

## License

MIT — see [LICENSE](LICENSE).

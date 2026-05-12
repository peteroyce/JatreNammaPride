# Jatre Namma Pride

**Digital Guide to the Village Fair** — An Android app serving as a digital companion for Karnataka's traditional Jatres (village fairs).

## Features

- **Live Schedule** — Real-time event tracking with countdown timers and category filters (Religious, Cultural, Sports, Food)
- **Lost & Found** — Photo-based lost/found item reporting with mark-as-resolved functionality
- **Venue Guide Map** — Interactive schematic map with GPS directions to parking, first-aid, stalls, entry/exit points
- **Cultural Stories** — Curated narratives about Jatre heritage and traditions
- **Admin Panel** — Event management, lost item moderation, and content control

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Architecture | MVVM (Model-View-ViewModel) |
| Local DB | Room Database |
| Cloud | Firebase Firestore, Storage, Auth |
| Image Loading | Coil |
| Navigation | Navigation Compose |
| Async | Kotlin Coroutines & Flow |

## Architecture

```
app/
├── data/
│   ├── local/       # Room DAOs & Database
│   ├── model/       # Entity classes (Event, LostItem, Story, MapMarker)
│   ├── remote/      # Firebase data source with local fallback
│   └── repository/  # Repository layer
├── navigation/      # NavGraph & screen routes
├── ui/
│   ├── admin/       # Admin panel screen & ViewModel
│   ├── components/  # Reusable composables (EventCard, StoryCard, etc.)
│   ├── home/        # Home screen with bottom navigation
│   ├── lostfound/   # Lost & Found screens & ViewModel
│   ├── map/         # Venue guide map screen & ViewModel
│   ├── schedule/    # Event schedule screen & ViewModel
│   ├── stories/     # Cultural stories screen & ViewModel
│   └── theme/       # Material3 theme, colors, typography
└── util/            # Date formatting utilities
```

## Building

1. Open in Android Studio (Hedgehog or later)
2. Add your `google-services.json` to `app/` (optional — app works offline without it)
3. Build & run on device/emulator (min SDK 28, Android 9.0+)

## Author

**Pete Royce Saldanha**
MindMatrix Internship — Android App Development using Gen AI (Feb–May 2026)

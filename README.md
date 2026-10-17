# RateWise

RateWise is a currency exchange rate tracker built with Kotlin Multiplatform. Business logic, networking and
persistence live in a shared module; the Android app is built with Jetpack Compose and Material 3.

This repository is a scaffold: the screens, view models, DI and data layers are wired together, but the
features are mostly placeholders.

## Screens

| Screen | Purpose |
| --- | --- |
| Home | List of currency rates for the base currency |
| Converter | Convert an amount between two currencies |
| Favorites | Saved currencies |
| Settings | Choose the base currency |

## Tech stack

- Kotlin Multiplatform (Android target for now)
- Jetpack Compose + Material 3, Navigation Compose
- Kotlin Coroutines and Flow
- Ktor client (shared) with kotlinx.serialization
- Room (KMP) with the bundled SQLite driver
- Hilt for dependency injection (Android)
- `kotlin.test` for unit tests
- Min SDK 26, target/compile SDK 35

Rates come from the free [Frankfurter API](https://api.frankfurter.app/latest).

## Architecture

Clean Architecture with data / domain / presentation layers.

```
:shared (KMP)
  commonMain/com/ratewise/shared
    domain/   model, repository interface, use cases (GetRatesUseCase, ConvertCurrencyUseCase)
    data/     remote (Ktor + CurrencyApiService), local (Room entity/DAO/database), repository impl
  commonTest/ unit tests for the use cases

:androidApp
  ui/         home, converter, favorites, settings (Compose screen + Hilt ViewModel each)
  navigation/ NavGraph.kt (bottom navigation)
  di/         AppModule.kt (Hilt bindings for Ktor, Room, repository, use cases)
```

Dependencies point inward: `androidApp` -> `shared.domain` <- `shared.data`.

## Build and test

Requires JDK 17 and the Android SDK (API 35).

```bash
./gradlew assembleDebug            # build the app
./gradlew :shared:testDebugUnitTest # run shared unit tests
```

CI (`.github/workflows/ci.yml`) runs both commands on every push.

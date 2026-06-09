# ViewPoint-LocationLib (KMP-Locations)

A **Kotlin Multiplatform** library that exposes device location and reverse geocoding behind one shared interface, so common code can observe position updates without touching platform SDKs. Part of the ViewPoint ecosystem.

[![KMP](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF)]() [![Targets](https://img.shields.io/badge/targets-Android%20%C2%B7%20iOS%20%C2%B7%20JVM-blue)]() ![version](https://img.shields.io/badge/version-1.0.0-green)

## Capabilities

- Start/stop streaming location updates from shared code.
- A platform-neutral `Coordinate(latitude, longitude)` type.
- Reverse geocoding (coordinate → place name) via a pluggable service.

```kotlin
interface LocationManager {
    val location: Flow<Coordinate?>   // observed position
    fun startUpdates()
    fun stopUpdates()
}

data class Coordinate(val latitude: Double, val longitude: Double)

interface ReverseGeocodingService {
    suspend fun placeName(coordinate: Coordinate): String?
}
```

| Platform | Backing API |
|----------|-------------|
| Android  | `FusedLocationProviderClient` / `Geocoder` |
| iOS      | `CLLocationManager` / `CLGeocoder` |
| JVM      | Pluggable stub for desktop/tests |

**Targets:** `androidTarget`, `iosArm64`, `iosSimulatorArm64`, `iosX64`, `jvm`.

## Install

Published to **GitHub Packages** (internal):

```kotlin
repositories {
    maven("https://maven.pkg.github.com/tjmtic/KMP-Locations") {
        credentials {
            username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
            password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
implementation("com.abyxcz.viewpoint.location:location-lib:1.0.0")
```

## Usage

```kotlin
scope.launch { locationManager.location.collect { coord -> /* update UI */ } }
locationManager.startUpdates()
```

Permissions are handled by the sibling `ViewPoint-PermissionsLib`.

## ViewPoint KMP ecosystem

Works alongside `KMP-Haptic`, `KMP-Notifications`, `KMP-BackgroundJobLib`, `ViewPoint-PermissionsLib`, and the `ViewPoint-CoreLib` domain kernel.

## License

See repository. © Tim McArdle / abyxcz.

# Changelog

## 0.6.2

Based on [android-maps-compose 8.4.0](https://github.com/googlemaps/android-maps-compose).

**SDK Versions:**

- Google Maps iOS SDK: 10.8.0
- Google Play Services Maps: 20.0.0

### Features

- **Cached clustering icons** - Added `clusterItemIcon` for rendering unclustered items with cached `BitmapDescriptor`s, plus `clusterItemAnchor` for icon alignment. `clusterItemIcon` takes precedence over `clusterItemContent` and avoids generating a Compose bitmap for each item.
- **Marker content keys** - Added `clusterItemContentKey` to control when Compose item marker bitmaps are rebuilt.
- **Marker visibility** - Added `markerVisibility` to control cluster and item marker rendering without changing cluster membership or split/merge calculations.

### Bug Fixes

- **Clustering concurrency** - Serialized item updates and cluster calculations with a mutex and moved item updates to the configured coroutine context to prevent concurrent access to the algorithm's QuadTree.
- **Cluster hash stability** - Removed mutable item collections from `StaticCluster.hashCode()` to prevent hash changes after insertion into a set and Kotlin/Native rehash failures.
- **Android marker bitmap caching** - Stopped using the content lambda as a cache key in `rememberComposeBitmapDescriptor`, avoiding bitmap regeneration caused by changing lambda references during recomposition.
- **iOS marker image corruption** - Copied rendered marker images into UIKit-owned pixel buffers to prevent partial clipping or corrupted icons when Skia pixel memory is reused.

### Compatibility Changes

- `ClusterManager.setItems()` is now a suspending function. Direct callers must invoke it from a coroutine or another suspending function.
- Changes to content passed to Android `rememberComposeBitmapDescriptor` must be represented in its explicit keys to trigger bitmap regeneration.
- Removed the `iosX64` target from both libraries and the shared sample. The supported iOS targets are now `iosArm64` and `iosSimulatorArm64`.
- Raised `compileSdk` from 36 to 37 for both libraries and the Android sample modules. Android `minSdk` remains 24 and the sample's `targetSdk` remains 36.

### Build and Dependencies

- Updated Kotlin from 2.3.20 to 2.3.21, Compose Multiplatform from 1.10.3 to 1.11.0, AGP from 9.1.0 to 9.2.1, maps-compose from 8.3.0 to 8.4.0, and spmForKmp from 1.7.0 to 1.9.1.
- Configured the Gradle daemon to use JDK 21.
- Lowered the Google Maps Swift bridge's configured minimum iOS version from 17.0 to 16.0 in both the Gradle SPM configuration and exported Swift package.

### Publishing

- Added `bundleMavenCentral` to build a signed upload ZIP for both libraries locally, for manual upload through the Central Portal.
- Changed the Maven group for both libraries from `eu.buney.maps` to `io.github.hysonhou` for independent fork releases. Kotlin packages and Android namespaces remain unchanged.
- Updated Maven POM project and SCM URLs to the HysonHou fork and added its maintainer while retaining upstream attribution.

## 0.6.1

Based on [android-maps-compose 8.3.0](https://github.com/googlemaps/android-maps-compose).

**SDK Versions:**
- Google Maps iOS SDK: 10.8.0
- Google Play Services Maps: 20.0.0

### Bug Fixes

- **iOS: Fix `newLatLngBounds` padding interpretation** - `CameraUpdateFactory.newLatLngBounds` takes padding in physical pixels (Android SDK convention), but on iOS the value was passed unscaled to `cameraForBounds:insets:`, which expects points. On 2-3x displays the effective padding was inflated by the screen scale, and when the insets exceeded the viewport the SDK silently fell back to minimum zoom, showing the whole world. The padding is now converted via the view's display scale, so the same value produces the same visual result on both platforms. If you previously worked around this by pre-dividing the padding on iOS, remove the workaround (#12)

## 0.6.0

Based on [android-maps-compose 8.3.0](https://github.com/googlemaps/android-maps-compose).

**SDK Versions:**
- Google Maps iOS SDK: 10.8.0
- Google Play Services Maps: 20.0.0

### Features

- **`WmsTileOverlay` composable** - Cross-platform Web Map Service (WMS) tile overlay support in `kmp-maps-compose-utils`. Converts tile coordinates to EPSG:3857 bounding boxes and fetches WMS tiles via platform-specific HTTP (ports googlemaps/android-maps-compose#884)

### Bug Fixes

- **iOS: Fix dynamic MapProperties and MapUiSettings not updating** - Properties like `mapType`, `isTrafficEnabled`, and UI settings were captured once during subcomposition setup and never updated on iOS. Introduced `MapUpdaterState` following the android-maps-compose pattern to ensure dynamic changes are reflected (regression since 0.3.0)

### Other Changes

- Applied Compose stability configuration to `kmp-maps-compose-utils` module, matching upstream (ports googlemaps/android-maps-compose#874)
- Updated maps-compose to 8.3.0, Kotlin to 2.3.20, Compose Multiplatform to 1.10.3, Gradle to 9.4.1, BuildKonfig to 0.18.0, spmForKmp to 1.7.0

## 0.5.0

Based on [android-maps-compose 8.2.0](https://github.com/googlemaps/android-maps-compose).

**SDK Versions:**
- Google Maps iOS SDK: 10.8.0
- Google Play Services Maps: 20.0.0

### Features

- **`TileOverlay` composable** - Cross-platform tile overlay support wrapping `TileProvider`/`TileOverlay` on Android and `GMSSyncTileLayer` on iOS. Includes `TileOverlayState` for cache management, with fadeIn, transparency, visibility, and zIndex properties.
- **`clusterItemDecoration`** - New parameter on all `Clustering` overloads for rendering additional map content (circles, polylines, etc.) alongside each unclustered item, matching android-maps-compose 8.2.0

### Breaking Changes

- **`clusterItemDecoration` signature** - Changed from `(T) -> Unit` to `(T, LatLng) -> Unit` so decorations receive the animated position during cluster transitions

### Other Changes

- Updated maps-compose to 8.2.0, Compose Multiplatform to 1.10.2, AGP to 9.1.0

## 0.4.0

Based on [android-maps-compose 8.1.0](https://github.com/googlemaps/android-maps-compose).

**SDK Versions:**
- Google Maps iOS SDK: 10.8.0
- Google Play Services Maps: 20.0.0

### Features

- **Marker Clustering** - New `kmp-maps-compose-utils` module with Compose-first marker clustering. Includes `NonHierarchicalDistanceBasedAlgorithm` ported from android-maps-utils, animated split/merge transitions, and `DefaultClusterContent` matching android-maps-utils visual appearance. ([#6](https://github.com/yankeppey/kmp-maps-compose/issues/6))
- **`MapStyleOptions`** - New expect/actual class for custom map styling via JSON from the [Google Maps Styling Wizard](https://mapstyle.withgoogle.com/). Use `MapStyleOptions.fromJson()` with `MapProperties`. ([#5](https://github.com/yankeppey/kmp-maps-compose/issues/5))
- **`MapEffect` composable** - Cross-platform access to the underlying native map object (`GoogleMap` on Android, `GMSMapView` on iOS) via `NativeMap` type alias

### Improvements

- **iOS: Improved composable-to-UIImage rendering** - Rewritten rendering pipeline for marker and info window image capture

## 0.3.0

Based on [android-maps-compose 8.1.0](https://github.com/googlemaps/android-maps-compose).

**SDK Versions:**
- Google Maps iOS SDK: 10.8.0
- Google Play Services Maps: 20.0.0

### Breaking Changes

- **CameraUpdate-based API** - `animate()` and `move()` now take a `CameraUpdate` parameter instead of `CameraPosition` directly. Use `CameraUpdateFactory.newCameraPosition()`, `CameraUpdateFactory.newLatLngZoom()`, or `CameraUpdateFactory.newLatLngBounds()` to create updates. This aligns the API with android-maps-compose.
- **`animateToBounds()` removed** - Use `animate(CameraUpdateFactory.newLatLngBounds(bounds, padding))` instead.
- **`visibleBounds` removed** - Use `cameraPositionState.projection?.visibleBounds` instead, querying it when `isMoving` becomes false.
- **Default animation duration changed** - `animate()` now defaults to `Int.MAX_VALUE` (SDK default duration) instead of 300ms. Pass an explicit `durationMs` to control duration.
- **`rememberCameraPositionState` `key` parameter removed** - The function now uses positional `rememberSaveable` scoping.

### Features

- **`CameraUpdateFactory`** - New factory object with `newCameraPosition()`, `newLatLngZoom()`, and `newLatLngBounds()` methods matching the android-maps-compose API
- **`currentCameraPositionState`** - Composables inside `GoogleMap` content can now access the camera state implicitly via `currentCameraPositionState`, matching the android-maps-compose API
- **Camera state saved across configuration changes** - `rememberCameraPositionState()` now uses `rememberSaveable`, so camera position survives configuration changes and process death on Android
- **iOS: Coroutine-aware `animate()`** - `animate()` now properly suspends until the animation completes and supports cancellation, matching Android behavior. Uses `CATransaction.setCompletionBlock` for completion detection; cancellation snaps the camera to stop the animation.
- **iOS: Custom animation duration** - The `durationMs` parameter is now forwarded to the native SDK via `CATransaction.setAnimationDuration()`
- **iOS: Deferred camera operations** - `animate()` and `move()` called before the map is available are now deferred and executed when the map becomes available, matching Android behavior

### Architecture

- **`CameraPositionState` is now `expect/actual`** - On Android, delegates directly to android-maps-compose's `CameraPositionState` (single source of truth, no bidirectional sync). On iOS, manages `GMSMapView` directly via `setMap()`.
- **iOS: Node-based map property management** - Introduced `IOSMapPropertiesNode` for managing map properties, UI settings, content padding, and camera state binding via the Compose node tree, replacing the imperative `UIKitView` update approach.

### Other Changes

- Updated Kotlin to 2.3.10, Compose Multiplatform to 1.10.1, AGP to 9.0.1
- Updated maps-compose to 8.1.0, Gradle to 9.3.1

## 0.2.0

Based on [android-maps-compose 8.0.0](https://github.com/googlemaps/android-maps-compose).

**SDK Versions:**
- Google Maps iOS SDK: 10.8.0
- Google Play Services Maps: 20.0.0

### Features

- **Styled Polylines** - Added support for gradient and stamped polylines via new `StyleSpan`, `StrokeStyle`, and `StampStyle` APIs
- **Projection API** - Added `CameraPositionState.projection` for coordinate/screen conversions and `visibleBounds` for tracking the visible map region
- **Compose Resources Integration** - Added `rememberBitmapDescriptor()` for easy loading of marker icons from Compose Resources

### Fixes

- Fixed camera position feedback loop causing map trembling on Android
- Fixed Android camera position not updating on user gestures
- Fixed `@GoogleMapComposable` annotation mismatch warnings

### Other Changes

- Updated to Gradle 9.3.0 and AGP 9.0.0
- Updated Google Maps SDK versions
- Improved Compose stability configuration

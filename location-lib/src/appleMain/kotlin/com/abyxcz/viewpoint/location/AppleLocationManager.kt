package com.abyxcz.viewpoint.location

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.CoreLocation.*
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlinx.cinterop.useContents
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
class AppleLocationManager : LocationManager {

    private val locationManager = CLLocationManager()
    
    private val _currentLocation = MutableStateFlow<Coordinate?>(null)
    override val currentLocation: StateFlow<Coordinate?> = _currentLocation

    private val _currentHeading = MutableStateFlow(0.0)
    override val currentHeading: StateFlow<Double> = _currentHeading

    private val _horizontalAccuracy = MutableStateFlow<Double?>(null)
    override val horizontalAccuracy: StateFlow<Double?> = _horizontalAccuracy

    private val delegate = LocationDelegate(
        onLocationUpdate = { lat, lon, acc ->
            _currentLocation.value = Coordinate(lat, lon)
            _horizontalAccuracy.value = acc
        },
        onHeadingUpdate = { heading ->
            _currentHeading.value = heading
        }
    )

    init {
        locationManager.delegate = delegate
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.distanceFilter = 1.0
        locationManager.headingFilter = 1.0
    }

    override fun startUpdates() {
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
        locationManager.startUpdatingHeading()
    }

    override fun stopUpdates() {
        locationManager.stopUpdatingLocation()
        locationManager.stopUpdatingHeading()
    }

    private class LocationDelegate(
        private val onLocationUpdate: (Double, Double, Double) -> Unit,
        private val onHeadingUpdate: (Double) -> Unit
    ) : NSObject(), CLLocationManagerDelegateProtocol {
        
        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
            val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
            val coordinate = location.coordinate
            onLocationUpdate(
                coordinate.useContents { latitude },
                coordinate.useContents { longitude },
                location.horizontalAccuracy
            )
        }

        override fun locationManager(manager: CLLocationManager, didUpdateHeading: CLHeading) {
            val heading = didUpdateHeading.trueHeading
            if (heading >= 0) {
                onHeadingUpdate(heading)
            } else {
                onHeadingUpdate(didUpdateHeading.magneticHeading)
            }
        }

        override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
            // Handle error
        }
    }
}

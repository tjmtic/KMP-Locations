package com.abyxcz.viewpoint.location

import kotlinx.coroutines.flow.StateFlow



interface LocationManager {
    val currentLocation: StateFlow<Coordinate?>
    val currentHeading: StateFlow<Double>
    val horizontalAccuracy: StateFlow<Double?>
    fun startUpdates()
    fun stopUpdates()
}

package com.abyxcz.viewpoint.location

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.location.LocationManager as AndroidLocationManager

class AndroidLocationManager(private val context: Context) : LocationManager, LocationListener, SensorEventListener {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as AndroidLocationManager
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val _currentLocation = MutableStateFlow<Coordinate?>(null)
    override val currentLocation: StateFlow<Coordinate?> = _currentLocation

    private val _currentHeading = MutableStateFlow(0.0)
    override val currentHeading: StateFlow<Double> = _currentHeading

    private val _horizontalAccuracy = MutableStateFlow<Double?>(null)
    override val horizontalAccuracy: StateFlow<Double?> = _horizontalAccuracy

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    @SuppressLint("MissingPermission")
    override fun startUpdates() {
        try {
            locationManager.requestLocationUpdates(
                AndroidLocationManager.GPS_PROVIDER,
                1000L,
                1f,
                this
            )
            locationManager.requestLocationUpdates(
                AndroidLocationManager.NETWORK_PROVIDER,
                1000L,
                1f,
                this
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun stopUpdates() {
        locationManager.removeUpdates(this)
        sensorManager.unregisterListener(this)
    }

    override fun onLocationChanged(location: Location) {
        _currentLocation.value = Coordinate(location.latitude, location.longitude)
        _horizontalAccuracy.value = location.accuracy.toDouble()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }
        updateOrientationAngles()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        val azimuthRad = orientationAngles[0]
        val azimuthDeg = Math.toDegrees(azimuthRad.toDouble())
        _currentHeading.value = (azimuthDeg + 360) % 360
    }
}

package com.abyxcz.viewpoint.location

interface ReverseGeocodingService {
    suspend fun getAddress(lat: Double, lon: Double): String?
}

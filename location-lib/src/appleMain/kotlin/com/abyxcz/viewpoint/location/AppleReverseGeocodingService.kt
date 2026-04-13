package com.abyxcz.viewpoint.location

import com.abyxcz.viewpoint.location.ReverseGeocodingService

class AppleReverseGeocodingService : ReverseGeocodingService {
    override suspend fun getAddress(lat: Double, lon: Double): String? {
        // TODO: Implement using CLGeocoder for iOS/macOS
        return null
    }
}

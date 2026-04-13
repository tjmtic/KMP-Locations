package com.abyxcz.viewpoint.location

import android.content.Context
import android.location.Geocoder
import com.abyxcz.viewpoint.location.ReverseGeocodingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class AndroidReverseGeocodingService(private val context: Context) : ReverseGeocodingService {
    override suspend fun getAddress(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val addressLine = address.getAddressLine(0)
                addressLine
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

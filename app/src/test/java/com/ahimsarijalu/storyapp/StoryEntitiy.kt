package com.ahimsarijalu.storyapp

import com.google.android.gms.maps.model.LatLng
import okhttp3.RequestBody
import java.io.File

data class StoryEntitiy(
    val file: File,
    val description: String,
    val token: String,
    val location: LatLng
)
package com.example.tareaapis

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory

class PokeApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .respectCacheHeaders(false)   // reutiliza la imagen guardada aunque esté "vencida"
            .build()
}

package com.all.dwcarpentry.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object ImageDownloader
{
    suspend fun downloadImages(imageUrls: List<String>) : List<Bitmap>
    {
        val images = mutableListOf<Bitmap>()
        withContext(Dispatchers.IO)
        {
            for(urlString in imageUrls)
            {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream
                images.add(BitmapFactory.decodeStream(inputStream))
            }
        }
        //TODO(): Add downloaded images to Image Cache
        return images
    }
}
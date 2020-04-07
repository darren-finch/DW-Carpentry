package com.all.dwcarpentry.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.security.InvalidParameterException

object ImageDownloader
{
    suspend fun downloadHouseImages(imageUrls: List<String>) : List<Bitmap>
    {
        val urls = mutableListOf<String>()
        val images = mutableListOf<Bitmap>()
        withContext(Dispatchers.IO)
        {
            for(urlString in imageUrls)
            {
                val url = URL(urlString)
                if(urlString.isNotEmpty())
                {
                    var image = ImageCache.getImage(urlString)
                    if(image == null)
                    {
                        val connection = url.openConnection() as HttpURLConnection
                        connection.connect()
                        val inputStream = connection.inputStream
                        image = BitmapFactory.decodeStream(inputStream)
                    }
                    if(image != null)
                    {
                        urls.add(urlString)
                        images.add(image)
                    }
                    else
                        println("Couldn't get a house image with a url of $url")
                }
                else
                    throw InvalidParameterException("When downloading images, a given url was found to be empty.")
            }
        }
        for(i in urls.indices)
        {
            if(i < images.size - 1)
                ImageCache.putImage(urls[i],images[i])
        }
        return images
    }
    suspend fun downloadCoverImages(houseKeys: List<String>, imagesUrls: List<String>) : HashMap<String, Bitmap>
    {
        val keyBitmapHashMap = HashMap<String, Bitmap>()
        val urls = mutableListOf<String>()
        val images = mutableListOf<Bitmap>()

        if(houseKeys.size == imagesUrls.size)
        {
            withContext(Dispatchers.IO)
            {
                for(i in houseKeys.indices)
                {
                    val url = URL(imagesUrls[i])
                    if(imagesUrls[i].isNotEmpty())
                    {
                        var image = ImageCache.getImage(url.toString())
                        if(image == null)
                        {
                            val connection = url.openConnection() as HttpURLConnection
                            connection.connect()
                            val inputStream = connection.inputStream
                            image = BitmapFactory.decodeStream(inputStream)
                        }
                        if(image != null)
                        {
                            urls.add(url.toString())
                            images.add(image)
                            keyBitmapHashMap[houseKeys[i]] = image
                        }
                        else
                            println("Couldn't get an image with a url of $url")
                    }
                    else
                        throw InvalidParameterException("When downloading images, a given url was found to be empty.")
                }
            }
        }
        else
            throw InvalidParameterException("Tried to download cover images but the houseKeys and imageUrls array were different lengths.")
        for(i in urls.indices)
        {
            if(i < images.size - 1)
                ImageCache.putImage(urls[i],images[i])
        }

        return keyBitmapHashMap
    }
}
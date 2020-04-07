package com.all.dwcarpentry.data

import android.graphics.Bitmap

object ImageCache
{
    private val imageCache = hashMapOf<String,Bitmap>()
    fun putImage(url: String, bitmap: Bitmap)
    {
        imageCache[url] = bitmap
    }
    fun getImage(url: String) : Bitmap?
    {
        return imageCache[url]
    }
    fun removeImage(url: String)
    {
        imageCache.remove(url)
    }
    fun clear()
    {
        imageCache.clear()
    }
}
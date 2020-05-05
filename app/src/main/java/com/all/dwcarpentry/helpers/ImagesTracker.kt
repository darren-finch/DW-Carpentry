package com.all.dwcarpentry.helpers

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import java.lang.Exception

/*
* Utility class for AddEditHouseFragment
 */
class ImagesTracker
{
    private var imageUris = mutableListOf<Uri>()
    private var bitmaps = mutableListOf<Bitmap>()

    fun addImage(contentResolver: ContentResolver, uri: Uri)
    {
        val bitmap = Utilities.getImageFromMediaStore(contentResolver, uri)
        bitmap?.let {
            imageUris.add(uri)
            bitmaps.add(it)
        }
    }
    fun removeImageUri(i: Int)
    {
        try
        {
            bitmaps.removeAt(i)
            imageUris.removeAt(i)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
    fun clear()
    {
        bitmaps.clear()
        imageUris.clear()
    }
    fun getImageUriStrings() : List<String>
    {
        return imageUris.map { uri -> uri.toString() }
    }
    fun getImages() : List<Bitmap>
    {
        return bitmaps
    }
    fun getImage(i: Int): Bitmap
    {
        return bitmaps[i]
    }
}
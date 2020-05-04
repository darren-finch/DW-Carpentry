package com.all.dwcarpentry.helpers

import android.net.Uri

/*
* Utility class for AddEditHouseFragment
 */
class ImageUriTracker
{
    private var imageUris = mutableListOf<Uri>()
    private var totalImageListSize = 0

    fun setTotalImageListSize(totalImageListSize: Int)
    {
        this.totalImageListSize = totalImageListSize
    }
    fun addImageUri(uri: Uri)
    {
        imageUris.add(uri)
    }
    fun removeImageUri(i: Int)
    {
        val properIndex = transformIndex(i)
        if(properIndex < imageUris.size && properIndex >= 0)
            imageUris.removeAt(properIndex)
    }
    fun clear()
    {
        imageUris.clear()
    }
    fun isNewImageUri(i: Int) : Boolean
    {
        return imageUris.size > transformIndex(i)
    }
    fun getImageUriStrings() : List<String>
    {
        return imageUris.map { uri -> uri.toString() }
    }
    private fun transformIndex(i: Int) : Int
    {
        return i - (totalImageListSize - imageUris.size)
    }
}
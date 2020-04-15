package com.all.dwcarpentry.helpers

import android.net.Uri

/*
* Utility class for AddEditHouseFragment
 */
class ImageUriTracker
{
    private var newImageUris = mutableListOf<Uri>()
    private var totalImageListSize = 0

    fun setTotalImageListSize(totalImageListSize: Int)
    {
        this.totalImageListSize = totalImageListSize
    }
    fun addNewImageUri(uri: Uri)
    {
        newImageUris.add(uri)
    }
    fun removeImageUri(i: Int)
    {
        val properIndex = transformIndex(i)
        if(properIndex < newImageUris.size && properIndex >= 0)
            newImageUris.removeAt(properIndex)
    }
    fun clear()
    {
        newImageUris.clear()
    }
    fun isNewImageUri(i: Int) : Boolean
    {
        return newImageUris.size > transformIndex(i)
    }
    fun getNewHouseImages() : Array<Uri>
    {
        val newHouseImages = mutableListOf<Uri>()
        for(i in newImageUris.indices)
        {
            if(isNewImageUri(i))
                newHouseImages.add(newImageUris[i])
        }
        return newHouseImages.toTypedArray()
    }
    private fun transformIndex(i: Int) : Int
    {
        return i - (totalImageListSize - newImageUris.size)
    }
}
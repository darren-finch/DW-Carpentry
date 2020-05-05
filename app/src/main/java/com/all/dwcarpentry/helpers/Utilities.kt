package com.all.dwcarpentry.helpers

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.all.dwcarpentry.data.room.House


object Utilities
{
    fun getEmptyHouse() : House
    {
        return House(id = 0, homeOwnerName = "", homeAddress = "", materialsUsed = "",
            homeImagesUris = mutableListOf())
    }
    fun getImageFromMediaStore(contentResolver: ContentResolver, uri: Uri) : Bitmap?
    {
        val bitmap: Bitmap
        try
        {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            return bitmap
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return null
    }
    fun hideKeyboardFrom(context: Context, view: View)
    {
        val imm: InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
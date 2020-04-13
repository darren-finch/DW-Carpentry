package com.all.dwcarpentry.data

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class FirebaseStorageAccessor(private val firebaseStorageRef: StorageReference)
{
    private var listener: Listener? = null

    suspend fun uploadHouseImages(images: List<Bitmap>)
    {
        if(listener != null)
        {
            val imageUrls = mutableListOf<String>()
            val imageNames = mutableListOf<String>()

            for(bm in images)
            {
                val baos = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.JPEG, 75, baos)
                val data = baos.toByteArray()
                val uuid = UUID.randomUUID().toString()
                val ref = firebaseStorageRef.child(uuid)
                val taskSnapshot = ref.putBytes(data).await()
                if(taskSnapshot != null)
                {
                    val url = ref.downloadUrl.await().toString()
                    imageUrls.add(url)
                    imageNames.add(uuid)
                }
            }

            listener!!.onUploadedHouseImages(imageUrls, imageNames)
        }
        else
            Log.e("FirebaseStorageAccessor", "No listener was given to the FirebaseStorageClass, so it cannot upload images.")
    }
    fun deleteHouseImagesFromStorage(deletedHomeImageNames: List<String>)
    {
        for(uuid in deletedHomeImageNames)
        {
            try
            {
                if(uuid.isNotEmpty())
                    firebaseStorageRef.child(uuid).delete()
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }
    fun setListener(listener: Listener)
    {
        this.listener = listener
    }
    interface Listener
    {
        fun onUploadedHouseImages(imageUrls: List<String>, imagesNames: List<String>)
    }
}
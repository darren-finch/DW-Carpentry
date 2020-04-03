package com.all.dwcarpentry.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.all.dwcarpentry.MainActivity
import com.all.dwcarpentry.MainViewModel
import com.all.dwcarpentry.R
import com.all.dwcarpentry.helpers.InjectionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadingImagesFragment(private val mainActivity: MainActivity,
                              private val imagesToUpload: MutableList<Bitmap>,
                              private val houseKey: String) : RequireActivityFragment(mainActivity)
{
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.uploading_images_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, InjectionUtils.provideMainViewModelFactory()).get(MainViewModel::class.java)
        CoroutineScope(Dispatchers.IO).launch{
            uploadHouseImages()
        }
    }

    private suspend fun uploadHouseImages()
    {
        viewModel.uploadHouseImages(imagesToUpload, houseKey)
        mainActivity.goToFragment(AllHousesFragment(mainActivity), false)
        withContext(Dispatchers.Main)
        {
            Toast.makeText(context, "Images uploaded successfully.", Toast.LENGTH_SHORT).show()
        }
    }
}
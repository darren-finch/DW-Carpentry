package com.all.dwcarpentry.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.all.dwcarpentry.MainActivity
import com.all.dwcarpentry.MainViewModel
import com.all.dwcarpentry.R
import com.all.dwcarpentry.helpers.InjectionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UploadingImagesFragment(private val mainActivity: MainActivity,
                              private val imagesToUpload: MutableList<Bitmap>,
                              private val houseKey: String) : BaseFragment(mainActivity)
{
    private val parentJob = Job()

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
        CoroutineScope(Dispatchers.IO + parentJob).launch{
            uploadHouseImages()
        }
    }

    private suspend fun uploadHouseImages()
    {
        viewModel.uploadHouseImages(imagesToUpload, houseKey)
        mainActivity.goToFragment(AllHousesFragment(mainActivity), false)
    }
}
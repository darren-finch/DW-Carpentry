package com.all.dwcarpentry.ui.fragments

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.all.dwcarpentry.MainViewModel
import com.all.dwcarpentry.R
import com.all.dwcarpentry.helpers.InjectionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UploadingImagesFragment : Fragment()
{
    private val args: UploadingImagesFragmentArgs by navArgs()
    private val viewModel: MainViewModel by viewModels {
        InjectionUtils.provideMainViewModelFactory()
    }
    private var imagesToUpload = arrayOf<Uri>()
    private val parentJob = Job()
    private var isUploadingImages = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.uploading_images_fragment, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        imagesToUpload = args.imagesToUpload
        if (imagesToUpload.isEmpty() || !isUploadingImages)
        {
            navigateToAllHousesFragment()
            return
        }
        CoroutineScope(Dispatchers.IO + parentJob).launch(){
            uploadHouseImages()
        }
    }
    override fun onResume()
    {
        super.onResume()
        if(!isUploadingImages)
            navigateToAllHousesFragment()
    }
    private suspend fun uploadHouseImages()
    {
        isUploadingImages = true
        val bitmaps = getBitmapsFromUris()
        viewModel.uploadHouseImages(bitmaps, args.houseKey)
        isUploadingImages = false
        navigateToAllHousesFragment()
    }
    private fun getBitmapsFromUris() : List<Bitmap>
    {
        val bitmaps = mutableListOf<Bitmap>()
        for(uri in imagesToUpload)
        {
            bitmaps.add(MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri))
        }
        return bitmaps
    }

    private fun navigateToAllHousesFragment()
    {
        if(view != null)
        {
            val direction = UploadingImagesFragmentDirections.toAllHousesFragment()
            view!!.findNavController().navigate(direction)
        }
    }
}
package com.all.dwcarpentry.ui.fragments

import android.os.Bundle
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
import kotlinx.coroutines.launch

class UploadingImagesFragment : Fragment()
{
    private val args: UploadingImagesFragmentArgs by navArgs()
    private val viewModel: MainViewModel by viewModels {
        InjectionUtils.provideMainViewModelFactory()
    }

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
        if (args.imagesToUpload.isEmpty())
        {
            navigateToAllHousesFragment()
            return
        }
        lifecycleScope.launch{
            uploadHouseImages()
        }
    }

    private suspend fun uploadHouseImages()
    {
        viewModel.uploadHouseImages(args.imagesToUpload.toList(), args.houseKey)
        navigateToAllHousesFragment()
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
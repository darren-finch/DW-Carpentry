package com.all.dwcarpentry.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.all.dwcarpentry.R
import com.all.dwcarpentry.data.room.House
import com.all.dwcarpentry.databinding.AddEditHouseFragmentBinding
import com.all.dwcarpentry.helpers.Constants
import com.all.dwcarpentry.helpers.ImagesTracker
import com.all.dwcarpentry.helpers.InjectionUtils
import com.all.dwcarpentry.helpers.Utilities
import com.all.dwcarpentry.viewmodels.IMainViewModel
import com.all.dwcarpentry.viewmodels.MainViewModel

class AddEditHouseFragment : Fragment()
{
    //Data
    private val args: AddEditHouseFragmentArgs by navArgs()
    private lateinit var viewModel: IMainViewModel

    private var houseData: House = Utilities.getEmptyHouse()
    private var imageTracker = ImagesTracker()
    private var insertingHouse = false

    //UI Properties
    private var _binding: AddEditHouseFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        _binding = AddEditHouseFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(requireActivity(), InjectionUtils.provideMainViewModelFactory(requireActivity().application, context!!)).get(
            MainViewModel::class.java)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        init()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CHOOSE_IMAGE_REQUEST && data != null)
        {
            val uri = data.data
            if(uri != null)
            {
                try
                {
                    imageTracker.addImage(requireActivity().contentResolver, uri)
                    setupCarousel()
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.edit_house_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        saveHouse()
        when (item.itemId)
        {
            R.id.saveButton -> if (insertingHouse) viewModel.insertHouse(houseData) else viewModel.updateHouse(houseData)
            R.id.deleteButton -> viewModel.deleteHouse(houseData)
        }
        navigateToAllHousesFragment()
        return false
    }
    override fun onDestroyView()
    {
        super.onDestroyView()
        resetData()
    }

    private fun init()
    {
        insertingHouse = args.houseId < 0
        if (insertingHouse)
            initUI()
        else
            getHouse()
    }
    private fun getHouse()
    {
        viewModel.getHouse(args.houseId).observe(viewLifecycleOwner, Observer { house ->
            houseData = house
            initUI()
            if(houseData.homeImagesUris.isNotEmpty())
                loadImages()
        })
    }
    private fun resetData()
    {
        imageTracker.clear()

        binding.homeImageCarousel.pageCount = 0

        _binding = null
    }
    private fun addHouseImage() = startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), Constants.CHOOSE_IMAGE_REQUEST)
    private fun removeHouseImage(i: Int)
    {
        imageTracker.removeImageUri(i)
        setupCarousel()
    }
    private fun loadImages()
    {
        for (uriString in houseData.homeImagesUris)
        {
            val uri = Uri.parse(uriString)
            imageTracker.addImage(requireActivity().contentResolver, uri)
        }
        setupCarousel()
        setupImages()
    }
    private fun initUI()
    {
        binding.homeOwnerEditText.setText(houseData.homeOwnerName)
        binding.homeAddressEditText.setText(houseData.homeAddress)
        binding.materialsUsedEditText.setText(houseData.materialsUsed)
        binding.addImageButton.setOnClickListener{
            addHouseImage()
        }
        binding.removeImageButton.setOnClickListener{
            if(imageTracker.getImages().isNotEmpty())
                removeHouseImage(binding.homeImageCarousel.currentItem)
        }
        setupImages()
    }
    private fun setupImages() = if(houseData.homeImagesUris.isEmpty()) binding.noImagesLayout.visibility = View.VISIBLE else binding.noImagesLayout.visibility = View.GONE
    private fun setupCarousel()
    {
        binding.homeImageCarousel.stopCarousel()
        binding.homeImageCarousel.setImageListener { position, imageView -> imageView.setImageBitmap(imageTracker.getImage(position)) }
        binding.homeImageCarousel.pageCount = imageTracker.getImages().size
        binding.imagesLoadingLayout.visibility = View.GONE
    }
    private fun saveHouse()
    {
        val houseId = houseData.id
        val newHouse = House(houseId,
            binding.homeOwnerEditText.text.toString(),
            binding.homeAddressEditText.text.toString(),
            binding.materialsUsedEditText.text.toString(),
            imageTracker.getImageUriStrings())

        houseData = newHouse
    }
    private fun navigateToAllHousesFragment()
    {
        if (view != null)
        {
            if (context != null && view != null)
                Utilities.hideKeyboardFrom(context!!, view!!)

            val directions = AddEditHouseFragmentDirections.toAllHousesFragment()
            view!!.findNavController().navigate(directions)
        }
    }
}
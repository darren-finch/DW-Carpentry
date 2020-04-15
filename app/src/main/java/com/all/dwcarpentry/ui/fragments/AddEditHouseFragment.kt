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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.all.dwcarpentry.MainViewModel
import com.all.dwcarpentry.R
import com.all.dwcarpentry.data.House
import com.all.dwcarpentry.databinding.AddEditHouseFragmentBinding
import com.all.dwcarpentry.helpers.Constants
import com.all.dwcarpentry.helpers.ImageUriTracker
import com.all.dwcarpentry.helpers.InjectionUtils
import com.bumptech.glide.Glide
import kotlinx.coroutines.*

class AddEditHouseFragment : Fragment()
{
    //Data
    private val args: AddEditHouseFragmentArgs by navArgs()
    private val viewModel: MainViewModel by lazy{
        ViewModelProviders.of(requireActivity(), InjectionUtils.provideMainViewModelFactory()).get(MainViewModel::class.java)
    }

    private lateinit var houseData: House
    private var displayedHouseImages = mutableListOf<Bitmap>()
    private var imageUriTracker = ImageUriTracker()
    private var deletedHouseImageNames = mutableListOf<String>()
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
                    val bm = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                    displayedHouseImages.add(bm)
                    imageUriTracker.addNewImageUri(uri)
                    imageUriTracker.setTotalImageListSize(displayedHouseImages.size)
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
        when (item.itemId)
        {
            R.id.saveButton -> if (insertingHouse) insertHouse() else updateHouse()
            R.id.deleteButton -> deleteHouse()
        }
        return false
    }
    override fun onDestroyView()
    {
        super.onDestroyView()
        resetData()
    }
    private fun init()
    {
        insertingHouse = args.houseKey.isEmpty()
        if (insertingHouse)
        {
            houseData = House()
            initUI()
        }
        else
            getHouse()
    }
    private fun getHouse()
    {
        viewModel.getHouse(args.houseKey).observe(viewLifecycleOwner, Observer { house ->
            houseData = house
            initUI()
            if(houseData.homeImagesUrls.size > 0)
                loadImages()
        })
    }
    private fun resetData()
    {
        houseData.homeAddress = ""
        houseData.homeOwnerName = ""
        houseData.materialsUsed = ""
        houseData.homeImagesUrls = mutableListOf()
        houseData.homeImagesNames = mutableListOf()

        displayedHouseImages.clear()
        imageUriTracker.clear()

        binding.homeImageCarousel.pageCount = 0

        _binding = null
    }
    private fun addHouseImage()
    {
        startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), Constants.CHOOSE_IMAGE_REQUEST)
    }
    private fun removeHouseImage(i: Int)
    {
        val isNewImage = imageUriTracker.isNewImageUri(i)
        imageUriTracker.removeImageUri(i)
        if(i < displayedHouseImages.size)
            displayedHouseImages.removeAt(i)

        if (!isNewImage)
        {
            val imageUrl = houseData.homeImagesUrls[i]
            val imageName = houseData.homeImagesNames[i]
            deletedHouseImageNames.add(imageName)
            houseData.homeImagesUrls.remove(imageUrl)
            houseData.homeImagesNames.remove(imageName)
        }
        setupCarousel()
    }
    private fun loadImages()
    {
        //TODO: If we ever create a refresh button, when loading images, clearing the entire house image list will erase all newly added images that haven't been uploaded yet. Change this.
        displayedHouseImages.clear()
        setupCarousel()

        lifecycleScope.launch{
            downloadImages()
            withContext(Dispatchers.Main)
            {
                setupCarousel()
            }
        }
    }
    private suspend fun downloadImages()
    {
        try
        {
            withContext(Dispatchers.IO)
            {
                for(imageUrl in houseData.homeImagesUrls)
                {
                    val futureTarget = Glide.with(this@AddEditHouseFragment).asBitmap().load(imageUrl).submit()
                    displayedHouseImages.add(futureTarget.get())
                }
            }
        }
        catch (e: java.lang.Exception)
        {
            e.printStackTrace()
        }
        imageUriTracker.setTotalImageListSize(displayedHouseImages.size)
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
            if(displayedHouseImages.size > 0)
                removeHouseImage(binding.homeImageCarousel.currentItem)
        }
        if(houseData.homeImagesUrls.size < 1) binding.noImagesLayout.visibility = View.VISIBLE else binding.noImagesLayout.visibility = View.GONE
        if(houseData.homeImagesUrls.size < 1) binding.imagesLoadingLayout.visibility = View.GONE else binding.imagesLoadingLayout.visibility = View.VISIBLE
    }
    private fun setupCarousel()
    {
        binding.homeImageCarousel.stopCarousel()
        binding.homeImageCarousel.setImageListener { position, imageView -> imageView.setImageBitmap(displayedHouseImages[position]) }
        binding.homeImageCarousel.pageCount = displayedHouseImages.size
        binding.imagesLoadingLayout.visibility = View.GONE
    }
    private fun insertHouse()
    {
        lifecycleScope.launch{
            saveHouse()
            val newHouseImages = imageUriTracker.getNewHouseImages()
            val newHouseKey = viewModel.insertHouse(houseData)
            withContext(Dispatchers.Main)
            {
                navigateToUploadingImagesFragment(newHouseKey, newHouseImages)
            }
        }
    }
    private fun updateHouse()
    {
        saveHouse()
        val newHouseImages = imageUriTracker.getNewHouseImages()
        viewModel.updateHouse(houseData, deletedHouseImageNames)
        navigateToUploadingImagesFragment(args.houseKey, newHouseImages)
    }
    private fun deleteHouse()
    {
        viewModel.deleteHouse(houseData.key)
        navigateToAllHousesFragment()
    }
    private fun saveHouse()
    {
        houseData.homeOwnerName = binding.homeOwnerEditText.text.toString()
        houseData.homeAddress = binding.homeAddressEditText.text.toString()
        houseData.materialsUsed = binding.materialsUsedEditText.text.toString()
    }
    //Returns only the images from the carousel that the user added. Not the ones that were downloaded.
    private fun navigateToUploadingImagesFragment(houseKey: String, newHouseImages: Array<Uri>)
    {
        if(view != null)
        {
            val directions = AddEditHouseFragmentDirections.toUploadingImagesFragment(houseKey, newHouseImages)
            view!!.findNavController().navigate(directions)
        }
    }
    private fun navigateToAllHousesFragment()
    {
        if (view != null)
        {
            val directions = AddEditHouseFragmentDirections.toAllHousesFragment()
            view!!.findNavController().navigate(directions)
        }
    }
}
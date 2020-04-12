package com.all.dwcarpentry.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
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
import com.all.dwcarpentry.helpers.InjectionUtils
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import java.util.*

class AddEditHouseFragment : Fragment()
{
    //Data
    private val args: AddEditHouseFragmentArgs by navArgs()
    private val viewModel: MainViewModel by lazy{
        ViewModelProviders.of(requireActivity(), InjectionUtils.provideMainViewModelFactory()).get(MainViewModel::class.java)
    }

    private lateinit var houseData: House
    private var houseKey: String = ""
    private var houseImages = mutableListOf<Bitmap>()
    private var isNewImageList = mutableListOf<Boolean>()
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
            try
            {
                val bm = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
                houseImages.add(bm)
                isNewImageList.add(true)
                setupCarousel()
            }
            catch (e: Exception)
            {
                e.printStackTrace()
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

        houseImages.clear()
        isNewImageList.clear()

        binding.homeImageCarousel.pageCount = 0

        _binding = null
    }
    private fun addHouseImage()
    {
        startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), Constants.CHOOSE_IMAGE_REQUEST)
    }
    private fun removeHouseImage(i: Int)
    {
        val isNewImage = isNewImageList[i]
        houseImages.removeAt(i)
        isNewImageList.removeAt(i)
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
        lifecycleScope.launch{
            //TODO: If we ever create a refresh button, when loading images, clearing the entire house image list will erase all newly added images that haven't been uploaded yet. Change this.
            houseImages.clear()
            isNewImageList.clear()

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
                    houseImages.add(futureTarget.get())
                }
            }
        }
        catch (e: java.lang.Exception)
        {
            e.printStackTrace()
        }
        for(i in houseImages.indices)
            isNewImageList.add(false)
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
            removeHouseImage(binding.homeImageCarousel.currentItem)
        }
        if(houseData.homeImagesUrls.size < 1) binding.noImagesLayout.visibility = View.VISIBLE else binding.noImagesLayout.visibility = View.GONE
        if(houseData.homeImagesUrls.size < 1) binding.imagesLoadingLayout.visibility = View.GONE else binding.imagesLoadingLayout.visibility = View.VISIBLE
    }
    private fun setupCarousel()
    {
        binding.homeImageCarousel.stopCarousel()
        binding.homeImageCarousel.setImageListener { position, imageView -> imageView.setImageBitmap(houseImages[position]) }
        binding.homeImageCarousel.pageCount = houseImages.size
        binding.imagesLoadingLayout.visibility = View.GONE
    }
    private fun insertHouse()
    {
        lifecycleScope.launch {
            saveHouse()
            val newHouseImages = getNewHouseImages()
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
        val newHouseImages = getNewHouseImages()
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
    private fun getNewHouseImages() : Array<Bitmap>
    {
        val newHouseImages = mutableListOf<Bitmap>()
        for(i in houseImages.indices)
        {
            if(isNewImageList[i])
                newHouseImages.add(houseImages[i])
        }
        return newHouseImages.toTypedArray()
    }

    private fun navigateToUploadingImagesFragment(houseKey: String, newHouseImages: Array<Bitmap>)
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
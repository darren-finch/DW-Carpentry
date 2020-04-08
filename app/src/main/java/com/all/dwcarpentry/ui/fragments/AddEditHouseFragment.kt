package com.all.dwcarpentry.ui.fragments

import android.accounts.NetworkErrorException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import com.all.dwcarpentry.MainActivity
import com.all.dwcarpentry.R
import com.all.dwcarpentry.data.House
import com.all.dwcarpentry.databinding.AddEditHouseFragmentBinding
import com.all.dwcarpentry.helpers.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import kotlinx.coroutines.*

class AddEditHouseFragment(private val mainActivity: MainActivity, private val houseData: House) : BaseFragment(mainActivity)
{
    //Data
    private var houseImages = mutableListOf<Bitmap>()
    private var isNewImageList = mutableListOf<Boolean>()
    private var deletedHouseImageNames = mutableListOf<String>()

    //UI Properties
    private var _binding: AddEditHouseFragmentBinding? = null
    private val binding get() = _binding!!

    private val parentJob = Job()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        _binding = AddEditHouseFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if(houseData.homeImagesUrls.size > 0)
            loadImages()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        initUI()
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
            R.id.saveButton -> saveHouse()
            R.id.deleteButton -> deleteHouse()
        }
        return false
    }
    override fun onDestroyView()
    {
        super.onDestroyView()
        parentJob.cancel()
        _binding = null
    }

    private fun addHouseImage()
    {
        startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), Constants.CHOOSE_IMAGE_REQUEST)
    }
    private fun removeHouseImage()
    {
        val i = binding.homeImageCarousel.currentItem
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
        CoroutineScope(Dispatchers.IO + parentJob).launch{
            houseImages.clear() //TODO: If we ever create a refresh button, when loading images,
                                // clearing the entire house image list will erase all newly added images that haven't been uploaded yet. Change this.
            isNewImageList.clear()
            try
            {
                for(imageUrl in houseData.homeImagesUrls)
                {
                    val futureTarget = Glide.with(this@AddEditHouseFragment).asBitmap().load(imageUrl).submit()
                    houseImages.add(futureTarget.get())
                }
            }
            catch (e: java.lang.Exception)
            {
                e.printStackTrace()
            }

            for(i in houseImages.indices)
                isNewImageList.add(false)

            withContext(Dispatchers.Main)
            {
                setupCarousel()
            }
        }
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
            removeHouseImage()
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
    private fun saveHouse()
    {
        val newHouseImages = mutableListOf<Bitmap>()
        for(i in houseImages.indices)
        {
            if(isNewImageList[i])
                newHouseImages.add(houseImages[i])
        }
        houseData.homeOwnerName = binding.homeOwnerEditText.text.toString()
        houseData.homeAddress = binding.homeAddressEditText.text.toString()
        houseData.materialsUsed = binding.materialsUsedEditText.text.toString()
        viewModel.updateHouse(houseData, deletedHouseImageNames)
        mainActivity.goToFragment(UploadingImagesFragment(mainActivity, newHouseImages, houseData.key), false)
    }
    private fun deleteHouse()
    {
        viewModel.deleteHouse(houseData.key)
        mainActivity.goToFragment(AllHousesFragment(mainActivity), false)
    }

    override fun onDetach()
    {
        super.onDetach()

    }
}
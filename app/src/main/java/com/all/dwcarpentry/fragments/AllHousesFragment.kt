package com.all.dwcarpentry.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.all.dwcarpentry.MainActivity
import com.all.dwcarpentry.R
import com.all.dwcarpentry.data.House
import com.all.dwcarpentry.databinding.AllHousesFragmentBinding
import com.all.dwcarpentry.recyclerviews.HousesRecyclerViewAdapter
import com.all.dwcarpentry.recyclerviews.HousesRecyclerViewAdapter.OnHouseCardClickedListener
import com.all.dwcarpentry.recyclerviews.MarginItemDecoration
import kotlinx.coroutines.*

class AllHousesFragment(private val mainActivity: MainActivity) : BaseFragment(mainActivity)
{
    private lateinit var housesRecyclerViewAdapter: HousesRecyclerViewAdapter

    private var _binding: AllHousesFragmentBinding? = null
    private val binding get() = _binding!!

    private var isDownloadingImages = false

    private val parentJob = Job()

    //region Listeners
    private val onHouseCardClickedListener = object : OnHouseCardClickedListener
    {
        override fun onHouseCardClicked(house: House)
        {
            mainActivity.goToFragment(AddEditHouseFragment(mainActivity, house))
        }
    }
    //endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        _binding = AllHousesFragmentBinding.inflate(inflater, container, false)
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
        viewModel.getHouses().observe(viewLifecycleOwner, Observer{ houses ->
            if(!isDownloadingImages)
            {
                CoroutineScope(Dispatchers.IO + parentJob).launch{
                    updateHouses(houses)
                }
            }
        })
        initUI()
    }
    private suspend fun updateHouses(houses: MutableList<House>)
    {
        if(houses.size < 1)
        {
            setNoHouses(true)
            return
        }
        else
        {
            setNoHouses(false)
        }

        val coverImages = getCoverImages(houses)

        withContext(Dispatchers.Main)
        {
            housesRecyclerViewAdapter.updateHouses(houses, coverImages)
            binding.loadingLayout.visibility = View.GONE
        }
    }
    private suspend fun getCoverImages(houses: MutableList<House>) : HashMap<String, Bitmap>
    {
        val houseImageUrls = mutableListOf<String>()
        val houseKeys = mutableListOf<String>()
        for(house in houses)
        {
            if(house.homeImagesUrls.size > 0 && house.homeImagesUrls[0].isNotEmpty())
            {
                houseKeys.add(house.key)
                houseImageUrls.add(house.homeImagesUrls[0])
            }
        }
        return if(houseKeys.size > 0)
            viewModel.downloadCoverImages(houseKeys, houseImageUrls)
        else
            hashMapOf()
    }
    private fun initUI()
    {
        housesRecyclerViewAdapter = HousesRecyclerViewAdapter(mutableListOf(), onHouseCardClickedListener)
        binding.housesRecyclerView.adapter = housesRecyclerViewAdapter
        binding.housesRecyclerView.addItemDecoration(MarginItemDecoration(16))
        binding.housesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.addHouseFab.setOnClickListener{
            viewModel.insertHouse(House("", "No Homeowner", "123 Default Road", "5 - 2x4", mutableListOf(), mutableListOf()))
        }
    }
    private suspend fun setNoHouses(noHouses: Boolean)
    {
        withContext(Dispatchers.Main)
        {
            if(noHouses) binding.noHousesLayout.visibility = View.VISIBLE else binding.noHousesLayout.visibility = View.GONE
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val searchView = menu.findItem(R.id.searchButton).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { return false }
            override fun onQueryTextChange(newText: String): Boolean {

                return true
            }
        })
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
//        parentJob.cancel()
        _binding = null
    }
}

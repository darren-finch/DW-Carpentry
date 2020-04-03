package com.all.dwcarpentry.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.all.dwcarpentry.MainActivity
import com.all.dwcarpentry.helpers.InjectionUtils
import com.all.dwcarpentry.MainViewModel
import com.all.dwcarpentry.R
import com.all.dwcarpentry.data.House
import com.all.dwcarpentry.databinding.AllHousesFragmentBinding
import com.all.dwcarpentry.recyclerviews.HousesRecyclerViewAdapter
import com.all.dwcarpentry.recyclerviews.HousesRecyclerViewAdapter.OnHouseCardClickedListener
import com.all.dwcarpentry.recyclerviews.MarginItemDecoration

class AllHousesFragment(private val mainActivity: MainActivity) : RequireActivityFragment(mainActivity)
{
    private lateinit var viewModel: MainViewModel
    private lateinit var housesRecyclerViewAdapter: HousesRecyclerViewAdapter

    private var _binding: AllHousesFragmentBinding? = null
    private val binding get() = _binding!!

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
        val factory = InjectionUtils.provideMainViewModelFactory()
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        viewModel.getHouses().observe(viewLifecycleOwner, Observer{ houses ->
            updateHouses(houses)
        })
        initUI()
    }
    private fun updateHouses(houses: MutableList<House>)
    {
        if(houses.size < 1) binding.noHousesLayout.visibility = View.VISIBLE else binding.noHousesLayout.visibility = View.GONE
        housesRecyclerViewAdapter.updateHouses(houses)
        binding.loadingLayout.visibility = View.GONE
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val searchView = menu.findItem(R.id.searchButton).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { return false }
            override fun onQueryTextChange(newText: String): Boolean {
                //TODO: Implement list querying
                return true
            }
        })
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }
}

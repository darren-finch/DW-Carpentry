package com.all.dwcarpentry.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.all.dwcarpentry.MainViewModel
import com.all.dwcarpentry.R
import com.all.dwcarpentry.databinding.AllHousesFragmentBinding
import com.all.dwcarpentry.helpers.InjectionUtils
import com.all.dwcarpentry.ui.recyclerviews.HousesRecyclerViewAdapter
import com.all.dwcarpentry.ui.recyclerviews.HousesRecyclerViewAdapter.OnHouseCardClickedListener
import com.all.dwcarpentry.ui.recyclerviews.MarginItemDecoration

class AllHousesFragment : Fragment()
{
    //UI Stuff
    private var _binding: AllHousesFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var housesRecyclerViewAdapter: HousesRecyclerViewAdapter

    //region Listeners
    private val onHouseCardClickedListener = object : OnHouseCardClickedListener
    {
        override fun onHouseCardClicked(houseKey: String)
        {
            navigateToAddEditHouseFragment(houseKey)
        }
    }
    private val onAddHouseFabClicked = View.OnClickListener{
        navigateToAddEditHouseFragment("")
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
        val viewModel = ViewModelProviders.of(requireActivity(), InjectionUtils.provideMainViewModelFactory()).get(MainViewModel::class.java)
        viewModel.getHouses().observe(viewLifecycleOwner, Observer{ houses ->
            housesRecyclerViewAdapter.updateHouses(houses)
            postUpdateHouses(houses.size < 1)
        })
        initUI()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val searchView = menu.findItem(R.id.searchButton).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { return false }
            override fun onQueryTextChange(newText: String): Boolean {
                housesRecyclerViewAdapter.filter.filter(newText)
                return true
            }
        })
    }
    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    private fun initUI()
    {
        housesRecyclerViewAdapter = HousesRecyclerViewAdapter(this, mutableListOf(), onHouseCardClickedListener)
        binding.housesRecyclerView.adapter = housesRecyclerViewAdapter
        binding.housesRecyclerView.addItemDecoration(MarginItemDecoration(16))
        binding.housesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.addHouseFab.setOnClickListener{
            onAddHouseFabClicked.onClick(view)
        }
    }
    private fun postUpdateHouses(noHouses: Boolean)
    {
        binding.loadingLayout.visibility = View.GONE
        if(noHouses) binding.noHousesLayout.visibility = View.VISIBLE else binding.noHousesLayout.visibility = View.GONE
    }
    private fun navigateToAddEditHouseFragment(houseKey: String)
    {
        if(view != null)
        {
            val direction = AllHousesFragmentDirections.toAddEditHouseFragment(houseKey)
            view!!.findNavController().navigate(direction)
        }
    }
}

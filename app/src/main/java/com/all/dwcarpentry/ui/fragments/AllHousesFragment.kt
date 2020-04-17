package com.all.dwcarpentry.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.all.dwcarpentry.MainViewModel
import com.all.dwcarpentry.R
import com.all.dwcarpentry.databinding.AllHousesFragmentBinding
import com.all.dwcarpentry.helpers.InjectionUtils
import com.all.dwcarpentry.ui.recyclerviews.HousesRecyclerViewAdapter
import com.all.dwcarpentry.ui.recyclerviews.MarginItemDecoration
import com.all.dwcarpentry.ui.recyclerviews.OnHouseCardClickedListener


class AllHousesFragment : Fragment()
{
    //View model
    private lateinit var viewModel: MainViewModel
    //UI Stuff
    private var _binding: AllHousesFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var housesRecyclerViewAdapter: HousesRecyclerViewAdapter

    private lateinit var layoutManager: LinearLayoutManager
    private var firstLoad = true
    private var loading = true
    private var pastVisibleItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0

    //region Listeners
    private val onRecyclerViewScroll = object : RecyclerView.OnScrollListener()
    {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
        {
            if(dy > 0)
            {
                visibleItemCount = layoutManager.childCount
                totalItemCount = layoutManager.itemCount
                pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                if(loading)
                {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount)
                    {
                        loading = false;
                        Log.v("...", "Last Item Wow !");
                        viewModel.loadMoreHouses()
                    }
                }
            }
        }
    }
    private val onHouseCardClickedListener = object :
        OnHouseCardClickedListener
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
        observeHouses()
        viewModel.loadMoreHouses()
        initUI()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val searchView = menu.findItem(R.id.searchButton).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { return false }
            override fun onQueryTextChange(newText: String): Boolean {
//                housesRecyclerViewAdapter.filter.filter(newText)
                return true
            }
        })
    }
    override fun onDestroyView()
    {
        super.onDestroyView()
        binding.housesRecyclerView.removeOnScrollListener(onRecyclerViewScroll)
        _binding = null
    }

    private fun observeHouses()
    {
        viewModel = ViewModelProviders.of(requireActivity(), InjectionUtils.provideMainViewModelFactory()).get(MainViewModel::class.java)
        viewModel.getHouses().observe(viewLifecycleOwner, androidx.lifecycle.Observer { houses ->
            housesRecyclerViewAdapter.updateHouses(houses)
            postUpdateHouses(houses.size < 1)
            if(!firstLoad)
                loading = false
            firstLoad = false
        })
    }

    private fun initUI()
    {
        initRecyclerView()
        binding.addHouseFab.setOnClickListener{
            onAddHouseFabClicked.onClick(view)
        }
    }

    private fun initRecyclerView()
    {
        housesRecyclerViewAdapter = HousesRecyclerViewAdapter(mutableListOf(), this, onHouseCardClickedListener)
        val housesRecyclerView = binding.housesRecyclerView
        housesRecyclerView.addOnScrollListener(onRecyclerViewScroll)
        housesRecyclerView.adapter = housesRecyclerViewAdapter
        housesRecyclerView.addItemDecoration(MarginItemDecoration(16))

        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        housesRecyclerView.layoutManager = layoutManager
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

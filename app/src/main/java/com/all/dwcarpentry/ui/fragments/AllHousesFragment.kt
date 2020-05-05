package com.all.dwcarpentry.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.all.dwcarpentry.viewmodels.MainViewModel
import com.all.dwcarpentry.databinding.AllHousesFragmentBinding
import com.all.dwcarpentry.helpers.InjectionUtils
import com.all.dwcarpentry.ui.recyclerviews.HousesRecyclerViewAdapter
import com.all.dwcarpentry.ui.recyclerviews.MarginItemDecoration
import com.all.dwcarpentry.ui.recyclerviews.OnHouseCardClickedListener
import com.all.dwcarpentry.viewmodels.IMainViewModel
import java.lang.Exception


class AllHousesFragment : Fragment()
{
    //View model
    private lateinit var viewModel: IMainViewModel
    //UI Stuff
    private var _binding: AllHousesFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var housesRecyclerViewAdapter: HousesRecyclerViewAdapter

    private lateinit var layoutManager: LinearLayoutManager
    private var loading = false

    //region Listeners
    private val onHouseCardClickedListener = object :
        OnHouseCardClickedListener
    {
        override fun onHouseCardClicked(houseId: Int)
        {
            navigateToAddEditHouseFragment(houseId)
        }
    }
    private val onAddHouseFabClicked = View.OnClickListener{
        navigateToAddEditHouseFragment(-1)
    }
    //endregion

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), InjectionUtils.provideMainViewModelFactory(requireActivity().application, context!!)).get(
            MainViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        _binding = AllHousesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        observeHouses()
        initUI()
    }
    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    private fun observeHouses()
    {
        viewModel.getAllHouses().observe(viewLifecycleOwner, androidx.lifecycle.Observer { houses ->
            println("Printing houseIds:")
            houses.forEach { house -> println("${house.id}") }
            housesRecyclerViewAdapter.updateHouses(houses)
            postUpdateHouses(houses.isEmpty())
            loading = false
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
        housesRecyclerView.adapter = housesRecyclerViewAdapter
        housesRecyclerView.addItemDecoration(MarginItemDecoration(16))

        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        housesRecyclerView.layoutManager = layoutManager
    }

    private fun postUpdateHouses(isEmpty: Boolean)
    {
        binding.loadingLayout.visibility = View.GONE
        if(isEmpty) binding.noHousesLayout.visibility = View.VISIBLE else binding.noHousesLayout.visibility = View.GONE
    }

    private fun navigateToAddEditHouseFragment(houseId: Int)
    {
        if(view != null)
        {
            val direction = AllHousesFragmentDirections.toAddEditHouseFragment(houseId)
            view!!.findNavController().navigate(direction)
        }
    }
}

package com.all.dwcarpentry.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.all.dwcarpentry.MainActivity
import com.all.dwcarpentry.helpers.InjectionUtils
import com.all.dwcarpentry.MainViewModel
import com.all.dwcarpentry.R
import com.all.dwcarpentry.recyclerviews.HousesRecyclerViewAdapter
import com.all.dwcarpentry.recyclerviews.HousesRecyclerViewAdapter.OnHouseCardClickedListener
import com.all.dwcarpentry.recyclerviews.MarginItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AllHousesFragment(private val mainActivity: MainActivity) : RequireActivityFragment(mainActivity)
{
    private lateinit var viewModel: MainViewModel
    private lateinit var housesRecyclerView: RecyclerView
    private lateinit var housesRecyclerViewAdapter: HousesRecyclerViewAdapter
    private lateinit var addNewHouseFab: FloatingActionButton
    private lateinit var loadingHousesLayout: LinearLayout
    private lateinit var noHousesLayout: LinearLayout

    //region Listeners
    private val onHouseCardClickedListener = object : OnHouseCardClickedListener
    {
        override fun onHouseCardClicked(houseKey: String)
        {
            mainActivity.viewHouse(houseKey)
        }
    }
    //endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.all_houses_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        val factory = InjectionUtils.provideMainViewModelFactory(context!!)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        viewModel.getHouses().observe(viewLifecycleOwner, Observer{
            housesRecyclerViewAdapter.updateHouses(it)
        })
        initUI()
    }

    private fun initUI()
    {
        housesRecyclerView = view!!.findViewById(R.id.housesRecyclerView)
        addNewHouseFab = activity!!.findViewById(R.id.addHouse)
        loadingHousesLayout = activity!!.findViewById(R.id.loadingLayout)
        noHousesLayout = activity!!.findViewById(R.id.noHousesLayout)
        loadingHousesLayout.visibility = View.GONE
        housesRecyclerViewAdapter = HousesRecyclerViewAdapter(mutableListOf(), onHouseCardClickedListener)
        housesRecyclerView.adapter = housesRecyclerViewAdapter
        housesRecyclerView.addItemDecoration(MarginItemDecoration(16))
        housesRecyclerView.layoutManager = LinearLayoutManager(context)

        addNewHouseFab.setOnClickListener{

        }
    }
}

package com.all.dwcarpentry.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.all.dwcarpentry.MainActivity
import com.all.dwcarpentry.MainViewModel
import com.all.dwcarpentry.data.House
import com.all.dwcarpentry.helpers.InjectionUtils

class AddEditHouseFragment(private val mainActivity: MainActivity, private val houseKey: String) : RequireActivityFragment(mainActivity)
{
    private lateinit var viewModel: MainViewModel
    private var houseData : House? = null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val factory = InjectionUtils.provideMainViewModelFactory(context!!)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        houseData = viewModel.getHouseWithKey(houseKey)
        if(houseData == null)
        {
            Toast.makeText(context, "Was not able to get a house from the key of $houseKey", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(context, "Got a house from the database. The homeOwner's name is ${houseData!!.homeOwnerName}", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
    }
}
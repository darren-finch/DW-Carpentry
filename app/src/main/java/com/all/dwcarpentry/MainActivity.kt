package com.all.dwcarpentry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.all.dwcarpentry.fragments.AddEditHouseFragment
import com.all.dwcarpentry.fragments.AllHousesFragment
import com.all.dwcarpentry.fragments.RequireActivityFragment
import com.all.dwcarpentry.helpers.InjectionUtils

class MainActivity : AppCompatActivity()
{
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val factory = InjectionUtils.provideMainViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)

        if (savedInstanceState == null)
        {
            goToFragment(AllHousesFragment(this))
        }
    }

    private fun goToFragment(fragment: RequireActivityFragment)
    {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commitNow()
    }

    fun viewHouse(houseKey: String)
    {
        goToFragment(AddEditHouseFragment(this, houseKey))
    }
}

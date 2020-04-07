package com.all.dwcarpentry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.all.dwcarpentry.fragments.AllHousesFragment
import com.all.dwcarpentry.fragments.BaseFragment
import com.all.dwcarpentry.helpers.InjectionUtils

class MainActivity : AppCompatActivity()
{
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val factory = InjectionUtils.provideMainViewModelFactory()
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)

        if (savedInstanceState == null)
        {
            goToFragment(AllHousesFragment(this), false)
        }
    }

    fun goToFragment(fragment: BaseFragment, addToBackStack: Boolean = true, fragmentTag: String = "")
    {
        val transaction = supportFragmentManager.beginTransaction()
        if(addToBackStack)
            transaction.addToBackStack(fragmentTag)
        transaction.replace(R.id.container, fragment).commit()
    }
}

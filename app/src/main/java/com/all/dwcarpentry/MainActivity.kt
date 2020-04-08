package com.all.dwcarpentry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.all.dwcarpentry.ui.fragments.AllHousesFragment
import com.all.dwcarpentry.ui.fragments.BaseFragment
import com.all.dwcarpentry.helpers.InjectionUtils

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

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

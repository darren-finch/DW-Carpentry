package com.all.dwcarpentry.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.all.dwcarpentry.MainActivity
import com.all.dwcarpentry.MainViewModel
import com.all.dwcarpentry.helpers.InjectionUtils

abstract class BaseFragment(private val mainActivity: MainActivity) : Fragment()
{
    protected lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, InjectionUtils.provideMainViewModelFactory()).get(MainViewModel::class.java)
    }
}
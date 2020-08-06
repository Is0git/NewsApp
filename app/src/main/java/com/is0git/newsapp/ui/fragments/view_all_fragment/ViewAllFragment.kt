package com.is0git.newsapp.ui.fragments.view_all_fragment

import android.os.Bundle
import android.view.View
import com.is0git.newsapp.R
import com.is0git.newsapp.databinding.ViewAllFagmentLayoutBinding
import com.is0git.newsapp.ui.fragments.BaseFragment
import com.is0git.newsapp.utils.args.ArgsResolver

abstract class ViewAllFragment :
    BaseFragment<ViewAllFagmentLayoutBinding>(R.layout.view_all_fagment_layout),
    ArgsResolver {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resolveViewsWithArgs()
        super.onViewCreated(view, savedInstanceState)
    }
}
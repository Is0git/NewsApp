package com.is0git.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.is0git.newsapp.R
import com.is0git.newsapp.utils.buildDialogProgressBar

// can't use generic viewModel templates cause of di viewModel extension as it takes reified as type param..
abstract class BaseFragment<T : ViewDataBinding>(@LayoutRes val layoutId: Int) : Fragment() {

    lateinit var binding: T
    val progressDialog by lazy { requireActivity().buildDialogProgressBar(R.layout.progress_dialog_view) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    abstract fun observeData()
}
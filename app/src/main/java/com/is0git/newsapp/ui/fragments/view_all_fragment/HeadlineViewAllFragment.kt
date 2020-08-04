package com.is0git.newsapp.ui.fragments.view_all_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.is0git.newsapp.R
import com.is0git.newsapp.ui.adapters.AllHeadlinesAdapter
import com.is0git.newsapp.vm.category_view_all.HeadlineViewAllViewModel
import com.is0git.newsapp.vm.top_headlines.TopHeadLinesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HeadlineViewAllFragment : ViewAllFragment() {

    val args: HeadlineViewAllFragmentArgs by navArgs()
    val viewModel: HeadlineViewAllViewModel by viewModels()
    private val headlinesViewModel: TopHeadLinesViewModel by this.navGraphViewModels(R.id.main_nav) {defaultViewModelProviderFactory}
    @Inject lateinit var allHeadlinesAdapter: AllHeadlinesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.categoryList.adapter = allHeadlinesAdapter
    }

    override fun observeData() {
        lifecycleScope.launch {
            viewModel.pager.flow.collect {
                allHeadlinesAdapter.submitData(it)
            }
        }
    }

    override fun resolveArgs() {

    }

    override fun resolveViewsWithArgs() {

    }

}
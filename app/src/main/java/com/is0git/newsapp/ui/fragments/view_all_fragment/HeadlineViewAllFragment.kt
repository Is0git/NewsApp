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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HeadlineViewAllFragment : ViewAllFragment() {

    val args: HeadlineViewAllFragmentArgs by navArgs()
    val viewModel: HeadlineViewAllViewModel by viewModels()
    private val headlinesViewModel: TopHeadLinesViewModel by this.navGraphViewModels(R.id.main_nav) {defaultViewModelProviderFactory}
    @Inject lateinit var allHeadlinesAdapter: AllHeadlinesAdapter
    private var pagerJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.categoryList.adapter = allHeadlinesAdapter
//        initPagerStream(args.categoryType, headlinesViewModel.countryLiveData.value!!)
        initArticlesDataStream(args.categoryType, headlinesViewModel.countryLiveData.value!!)
    }

    override fun observeData() {

    }

    //Paging3 supports loading from database cache and getting data from network, don't have time to implement it fully...
//    private fun initPagerStream(category: String, country: String) {
//        pagerJob = lifecycleScope.launch {
//            viewModel.getAllArticlesStream(category, country).collectLatest {
//                allHeadlinesAdapter.itemCount
//                allHeadlinesAdapter.submitData(it)
//                allHeadlinesAdapter.itemCount
//            }
//        }
//    }


    fun initArticlesDataStream(category: String, country: String) {
        pagerJob =  lifecycleScope.launch {
            viewModel.getAllArticlesOnlyNetworkStream(category, country).collectLatest {
                allHeadlinesAdapter.submitData(it)
            }
        }
    }

    override fun resolveArgs() {

    }

    override fun resolveViewsWithArgs() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        pagerJob?.cancel()
    }
}
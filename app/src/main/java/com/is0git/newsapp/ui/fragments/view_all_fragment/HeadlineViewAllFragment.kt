package com.is0git.newsapp.ui.fragments.view_all_fragment

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.paging.LoadState
import com.is0git.newsapp.R
import com.is0git.newsapp.models.common.ArticlesItem
import com.is0git.newsapp.ui.adapters.AllHeadlinesAdapter
import com.is0git.newsapp.ui.fragments.top_headlines_fragment.HeadlinesFragment.Companion.startArticleBrowseIntentWithConnectionCheck
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

    private val args: HeadlineViewAllFragmentArgs by navArgs()
    private val viewModel: HeadlineViewAllViewModel by viewModels()
    private val headlinesViewModel: TopHeadLinesViewModel by this.navGraphViewModels(R.id.main_nav) { defaultViewModelProviderFactory }

    @Inject
    lateinit var allHeadlinesAdapter: AllHeadlinesAdapter
    private var pagerJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setTransitions()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.categoryName.transitionName = "categoryNameTransition"
        binding.categoryList.adapter = allHeadlinesAdapter
        super.onViewCreated(view, savedInstanceState)
        initArticlesDataStream(args.categoryType, headlinesViewModel.countryLiveData.value!!)
        setActions()
        allHeadlinesAdapter.onClickListener = ::onArticleItemClick
        allHeadlinesAdapter.onLinkButtonClick = ::startBrowseIntent
    }

    override fun observeData() {
        allHeadlinesAdapter.addLoadStateListener {
            binding.apply {
                val refreshVisibility = it.source.refresh is LoadState.Loading
                loadProgressBar.isVisible = refreshVisibility
                val isEmpty =
                    it.source.refresh is LoadState.Error && allHeadlinesAdapter.itemCount == 0
                emptyText.isVisible = isEmpty
                emptyView.isVisible = isEmpty
                categoryList.isVisible = it.source.refresh is LoadState.NotLoading
                refreshButton.isVisible = it.source.refresh is LoadState.Error
                val errorState = it.source.append as? LoadState.Error
                    ?: it.source.prepend as? LoadState.Error
                    ?: it.append as? LoadState.Error
                    ?: it.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun startBrowseIntent(articlesItem: ArticlesItem) {
        requireActivity().startArticleBrowseIntentWithConnectionCheck(articlesItem)
    }

    private fun setActions() {
        binding.refreshButton.setOnClickListener {
            if (view != null) {
                refreshAnimation(it)
                allHeadlinesAdapter.refresh()
            }
        }
    }

    private fun refreshAnimation(view: View) {
        ObjectAnimator.ofFloat(view, "rotation", 0f, 360f).apply {
            this.repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
    // Paging3 supports loading from database cache and getting data from network, don't have time to implement it fully...
//    private fun initPagerStream(category: String, country: String) {
//        pagerJob = lifecycleScope.launch {
//            viewModel.getAllArticlesStream(category, country).collectLatest {
//                allHeadlinesAdapter.itemCount
//                allHeadlinesAdapter.submitData(it)
//                allHeadlinesAdapter.itemCount
//            }
//        }
//    }


    private fun initArticlesDataStream(category: String, country: String) {
        pagerJob = lifecycleScope.launch {
            viewModel.getAllArticlesOnlyNetworkStream(category, country).collectLatest {
                allHeadlinesAdapter.submitData(it)
            }
        }
    }

    private fun onArticleItemClick(article: ArticlesItem) {
        modifyArticleData(article)
        val directions =
            HeadlineViewAllFragmentDirections.actionCategoryViewAllFragmentToFullNewsFragment(
                article
            )
        findNavController().navigate(directions)
    }

    override fun resolveViewsWithArgs() {
        binding.categoryName.text = args.categoryType
    }

    private fun modifyArticleData(article: ArticlesItem) {
        article.apply {
            category = args.categoryType
            country = headlinesViewModel.topHeadlinesRepository.country.value
        }
    }

    private fun setTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sharedElementEnterTransition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.shared_transition)
            sharedElementReturnTransition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.shared_transition)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pagerJob?.cancel()
    }
}
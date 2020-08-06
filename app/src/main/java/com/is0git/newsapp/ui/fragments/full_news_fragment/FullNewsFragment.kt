package com.is0git.newsapp.ui.fragments.full_news_fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.is0git.newsapp.R
import com.is0git.newsapp.databinding.FullNewsFragmentLayoutBinding
import com.is0git.newsapp.ui.fragments.BaseFragment
import com.is0git.newsapp.ui.fragments.top_headlines_fragment.HeadlinesFragment.Companion.startArticleBrowseIntentWithConnectionCheck
import com.is0git.newsapp.utils.DateUtils
import com.is0git.newsapp.utils.args.ArgsResolver
import com.is0git.newsapp.utils.loadImageWith
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class FullNewsFragment :
    BaseFragment<FullNewsFragmentLayoutBinding>(R.layout.full_news_fragment_layout),
    ArgsResolver,
    MotionLayout.TransitionListener {

    private val args: FullNewsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        resolveViewsWithArgs()
    }

    private fun setListeners() {
        binding.apply {
            (root as MotionLayout).also { motionLayout ->
                motionLayout.postDelayed(
                    {
                        motionLayout.transitionToEnd()
                    },
                    resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
                )
                motionLayout.setTransitionListener(this@FullNewsFragment)
                backButton.setOnClickListener {
                    motionLayout.transitionToStart()
                }
            }
            readMoreButton.setOnClickListener {
                requireActivity().startArticleBrowseIntentWithConnectionCheck(args.article)
            }
        }
    }

    override fun resolveViewsWithArgs() {
        binding.apply {
            val article = args.article
            authorImage loadImageWith article.urlToImage
            authorText.text = article.author
            contentText.text = article.content
            titleText.text = article.title
            dateText.text = DateUtils.formatDate(article.publishedAt)
            countryText.text = article.country
            categoryText.text = article.category
            newsImage loadImageWith article.urlToImage
        }

    }


    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        if (p1 == R.id.start) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        super.onDestroyView()
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
    override fun observeData() {}

}
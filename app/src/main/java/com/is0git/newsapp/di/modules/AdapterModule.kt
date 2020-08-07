package com.is0git.newsapp.di.modules

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
class AdapterModule {
//    @Provides
//    @AllListAdapter
//    fun getAllListAdapter(
//        allHeadlineAdapter: AllHeadlinesAdapter,
//        allListLoadStateAdapter: AllListLoadStateAdapter
//    ): ConcatAdapter {
//        return allHeadlineAdapter.withLoadStateFooter(allListLoadStateAdapter)
//    }
}
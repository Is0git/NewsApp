package com.is0git.newsapp.di.modules

import com.is0git.newsapp.ui.views.filter_card.ChipCreator
import com.is0git.newsapp.ui.views.filter_card.chip_creator.FilterChipCreator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewComponent

@Module
@InstallIn(ViewComponent::class)
abstract class ChipModule {
    @Binds
    abstract fun getFilterChipCreator(filterChipCreator: FilterChipCreator): ChipCreator
}
package com.is0git.newsapp.ui.views.filter_card

import com.google.android.material.chip.Chip
import com.is0git.newsapp.ui.views.filter_card.listeners.OnFilterCheckedListener

interface ChipCreator {
    fun createFilterView(filter: Filter): FilterMaterialCard.FilterView
    fun createChip(title: String): Chip
    fun setOnFilterCheckedListener(listener: OnFilterCheckedListener?)
    fun getOnFilterCheckedListener(): OnFilterCheckedListener?
}
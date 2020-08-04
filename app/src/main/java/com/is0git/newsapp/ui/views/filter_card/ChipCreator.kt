package com.is0git.newsapp.ui.views.filter_card

import android.view.View
import com.google.android.material.chip.Chip
import com.is0git.newsapp.ui.views.filter_card.listeners.OnFilterCheckedListener

interface ChipCreator {
    fun createChipGroup(filter: Filter): List<View>
    fun createChip(title: String): Chip
    fun setOnFilterCheckedListener(listener: OnFilterCheckedListener?)
}
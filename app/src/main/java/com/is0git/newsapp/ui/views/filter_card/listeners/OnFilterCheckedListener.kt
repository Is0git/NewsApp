package com.is0git.newsapp.ui.views.filter_card.listeners

import com.google.android.material.chip.Chip
import com.is0git.newsapp.ui.views.filter_card.Filter

interface OnFilterCheckedListener {
    fun onFilterSelectionCheck(chip: Chip?, filter: Filter)
}
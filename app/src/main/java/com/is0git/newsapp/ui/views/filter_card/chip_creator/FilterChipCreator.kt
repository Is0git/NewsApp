package com.is0git.newsapp.ui.views.filter_card.chip_creator

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import com.is0git.commonlibs.ScreenUnitUtils
import com.is0git.newsapp.R
import com.is0git.newsapp.ui.views.filter_card.ChipCreator
import com.is0git.newsapp.ui.views.filter_card.Filter
import com.is0git.newsapp.ui.views.filter_card.FilterMaterialCard
import com.is0git.newsapp.ui.views.filter_card.listeners.OnFilterCheckedListener
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


class FilterChipCreator @Inject constructor(@ActivityContext val context: Context) : ChipCreator {

    private val defaultMargin = ScreenUnitUtils.convertDpToPixel(24f, context).toInt()
    private var onFilterCheckedListener: OnFilterCheckedListener? = null

    override fun createFilterView(filter: Filter): FilterMaterialCard.FilterView {
        val viewsList = mutableListOf<View>()
        val chipGroupTitle: TextView = createTitleTextView(filter)
        val chipGroup = ChipGroup(context, null, R.attr.chipStyle).apply {
            isSingleSelection = true
            isSelectionRequired = true
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                .also { setLayoutParamsMargin(it, defaultMargin) }
        }
        val divider = View(context).apply {
            val height = ScreenUnitUtils.convertDpToPixel(1f, context).toInt()
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, height)
                .also { setLayoutParamsMargin(it, defaultMargin) }
            val bgColor = ResourcesCompat.getColor(resources, R.color.dividerColor, null)
            setBackgroundColor(bgColor)
        }
        for (s in filter.selection) {
            val chip = createChip(s)
            chip.id = ViewCompat.generateViewId()
            chipGroup.addView(chip)
        }
        viewsList.add(chipGroupTitle)
        viewsList.add(divider)
        viewsList.add(chipGroup)
        val filterView: FilterMaterialCard.FilterView =
            FilterMaterialCard.FilterView(chipGroup, filter, viewsList)
        filterView.group.setOnCheckedChangeListener { group, checkedId ->
            val chip: Chip? = group?.findViewById(checkedId)
            val position = group.getChipPositionById(checkedId)
            onFilterCheckedListener?.onFilterSelectionCheck(chip, filterView, position)
        }
        return filterView
    }

    override fun createChip(title: String): Chip {
        return Chip(context, null, R.attr.chipStyle).apply {
            text = title
        }
    }

    private fun createTitleTextView(filter: Filter): MaterialTextView {
        return MaterialTextView(context).apply {
            TextViewCompat.setTextAppearance(this, R.style.FilterMaterialCard_ChipGroup_Title)
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                .also { setLayoutParamsMargin(it, defaultMargin) }
            text = filter.displayName
        }
    }

    override fun setOnFilterCheckedListener(listener: OnFilterCheckedListener?) {
        this.onFilterCheckedListener = listener
    }

    override fun getOnFilterCheckedListener(): OnFilterCheckedListener? {
        return onFilterCheckedListener
    }

    private fun <T : LinearLayout.LayoutParams> setLayoutParamsMargin(
        layoutParams: T,
        margin: Int
    ) {
        layoutParams.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                marginStart = margin
                marginEnd = margin
            }
            topMargin = ScreenUnitUtils.convertDpToPixel(8f, context).toInt()
            leftMargin = margin
            rightMargin = margin
        }
    }
}

fun ChipGroup.getChipPositionById(id: Int): Int? {
    for (c in 0 until childCount) {
        val chip = getChildAt(c)
        if (chip.id == id) {
            return c
        }
    }
    return null
}
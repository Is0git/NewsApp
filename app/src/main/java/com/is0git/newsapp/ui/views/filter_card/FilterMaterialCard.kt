package com.is0git.newsapp.ui.views.filter_card

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.core.widget.TextViewCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import com.is0git.commonlibs.ScreenUnitUtils
import com.is0git.newsapp.R
import com.is0git.newsapp.ui.views.filter_card.listeners.OnFilterCheckedListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// gonna break solid, no time
@AndroidEntryPoint
class FilterMaterialCard : MaterialCardView {

    @Inject
    lateinit var filterChipCreator: ChipCreator
    lateinit var titleText: MaterialTextView
    val filters = mutableMapOf<String, FilterView>()
    private val margin = ScreenUnitUtils.convertDpToPixel(24f, context).toInt()
    private val linearLayout =
        LinearLayout(context).also {
            it.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            it.orientation = LinearLayout.VERTICAL
        }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet? = null) {
        var title: String? = null
        if (attrs != null) {
            var typedArray: TypedArray? = null
            try {
                typedArray = context.obtainStyledAttributes(attrs, R.styleable.FilterMaterialCard)
                title = typedArray.getString(R.styleable.FilterMaterialCard_text)
            } catch (e: Exception) {

            } finally {
                typedArray?.recycle()
            }
        }
        titleText = MaterialTextView(context).apply {
            text = title ?: context.getString(R.string.filter)
            layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).also { params ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.marginStart = margin
                    params.marginEnd = margin
                }
                params.leftMargin = margin
                params.rightMargin = margin
                params.topMargin = margin
            }
            TextViewCompat.setTextAppearance(this, R.style.FilterMaterialCard_Title)
        }
        addView(linearLayout)
        linearLayout.addView(titleText)
    }

    fun addFilter(filter: Filter) {
        val filterView = filterChipCreator.createFilterView(filter)
        for (v in filterView.views) {
            linearLayout.addView(v)
        }
        filters[filter.displayName] = filterView
    }

    fun getFilterView(key: String): FilterView? {
        return filters[key]
    }

    fun selectChip(filterKey: String, chipPosition: Int) {
        val filterView = getFilterView(filterKey)
        val chip = filterView?.group?.getChildAt(chipPosition) as Chip?
        if (chip != null) {
            filterView?.group?.check(chip.id)
        }

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (parent is NestedScrollView) (parent as NestedScrollView).isNestedScrollingEnabled =
            false
    }

    fun setOnFilterCheckedListener(listener: OnFilterCheckedListener?) {
        filterChipCreator.setOnFilterCheckedListener(listener)
    }

    class FilterView(val group: ChipGroup, val filter: Filter, var views: List<View>)
}

infix fun ChipGroup.findChipById(id: Int): Chip? {
    for (i in 0 until this.childCount) {
        val chip = this.getChildAt(i)
        if (chip.id == id) {
            return chip as Chip
        }
    }
    return null
}
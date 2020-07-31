package com.is0git.multicategorylayout.category_data


import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


/**
 * [T] source data object used in category list
 */
open class Category<T>(
    var id: String,
    var categoryName: String,
    var isExpendable: Boolean,
    var flags: Int,
    @IdRes var categoryViewId: Int,
    var adapter: ListAdapter<*, out RecyclerView.ViewHolder>,
    @AttrRes var textAppearanceRes: Int?,
    @StyleRes var buttonStyleRes: Int?,
    var hasDivider: Boolean,
    @ColorRes var dividerColor: Int
) {
    companion object {
        const val FLAG_NONE = 0
        const val FLAG_GRID = 1
        const val FLAG_HORIZONTAL = 2
    }

    class Builder<T> {
        private var title: String? = null
        private var isViewAllEnabled: Boolean = false
        private var id: String? = null
        private var adapter: ListAdapter<T, out RecyclerView.ViewHolder>? = null
        private var flags: Int = FLAG_NONE
        private var isExpendable = true
        private var categoryData: MutableList<T> = mutableListOf()
        @AttrRes
        private var textAppearanceId: Int? = null
        @StyleRes
        private var buttonStyleId: Int? = null
        private var hasDivider = false
        @ColorRes
        private var dividerColorRes: Int = com.is0git.multicategorylayout.R.color.colorPrimary

        fun setTitle(title: String?): Builder<T> {
            this.title = title
            return this
        }

        fun enabledViewAll(): Builder<T> {
            isViewAllEnabled = true
            return this
        }

        fun <VH : RecyclerView.ViewHolder> addAdapter(adapter: ListAdapter<T, VH>): Builder<T> {
            this.adapter = adapter
            return this
        }

        fun addFlags(flags: Int): Builder<T> {
            this.flags = flags
            return this
        }

        fun setId(id: String): Builder<T> {
            this.id = id
            return this
        }

        fun setExpandable(isExpandable: Boolean): Builder<T> {
            this.isExpendable = isExpandable
            return this
        }

        fun addCategoryData(items: List<T>): Builder<T> {
            categoryData.addAll(items)
            return this
        }

        fun setTitleTextAppearance(@AttrRes textAppearanceId: Int): Builder<T> {
            this.textAppearanceId = textAppearanceId
            return this
        }

        /**
         * [dividerColorRes] if null, divider will have default color
         */
        fun addDivider(@ColorRes dividerColorRes: Int?): Builder<T> {
            hasDivider = true
            if (dividerColorRes != null) this.dividerColorRes = dividerColorRes
            return this
        }

        fun build(): Category<T> {
            if (title == null) title = "empty"
            if (adapter == null) throw KotlinNullPointerException("adapter can't be null")
            if (id == null) id = title
            return Category(
                title!!,
                id!!,
                isExpendable,
                flags,
                ViewCompat.generateViewId(),
                adapter!!,
                textAppearanceId,
                buttonStyleId,
                hasDivider,
                dividerColorRes
            )
        }
    }
}

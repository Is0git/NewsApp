package com.is0git.multicategorylayout.ui.view_creators

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.TypefaceCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.is0git.commonlibs.ScreenUnitUtils
import com.is0git.multicategorylayout.R
import com.is0git.multicategorylayout.category_data.Category.Companion.FLAG_GRID
import com.is0git.multicategorylayout.category_data.Category.Companion.FLAG_HORIZONTAL
import com.is0git.multicategorylayout.utils.hasEnabled

object CategoryUIFactory {

    fun createTitleTextView(context: Context, title: String): TextView {
        return MaterialTextView(context).apply {
            text = title
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.material_text_headline5)
            )
            setSingleLine()
            ellipsize = TextUtils.TruncateAt.END
            typeface = TypefaceCompat.create(
                context,
                ResourcesCompat.getFont(context, R.font.playfairbold),
                Typeface.BOLD
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                transitionName = "categoryNameTransition"
            }
//            setTextColor(ResourcesCompat.getColor(resources, , null))
        }
    }

    fun createSeeAllButton(context: Context, @StringRes textId: Int): Button {
        return MaterialButton(context).apply {
            this.text = context.getString(textId)
            cornerRadius = ScreenUnitUtils.convertDpToPixel(25f, context).toInt()
            typeface = TypefaceCompat.create(context, Typeface.SANS_SERIF, Typeface.BOLD)
            icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_next, null)
            iconGravity = MaterialButton.ICON_GRAVITY_END
            iconSize = ScreenUnitUtils.convertDpToPixel(15f, context).toInt()
        }
    }

    fun createRecyclerView(context: Context, flag: Int, adapter: ListAdapter<*, *>): RecyclerView {
        return RecyclerView(context).apply {
            val flags = flag hasEnabled FLAG_GRID
            val mLayoutManager = if (flags) {
                GridLayoutManager(context, 2)
            } else {
                LinearLayoutManager(context)
            }
            if (flag hasEnabled FLAG_HORIZONTAL) {
                mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            }
            layoutManager = mLayoutManager
            this.adapter = adapter
            itemAnimator = null
            id = ViewCompat.generateViewId()
            isNestedScrollingEnabled = false
        }
    }

    fun createDivider(context: Context, @ColorRes dividerColor: Int): View {
        return View(context).apply {
            setBackgroundColor(ResourcesCompat.getColor(resources, dividerColor, null))
            id = ViewCompat.generateViewId()
        }
    }

    fun getSpan(width: Float = 210f, context: Context): Int {
        return 2.coerceAtLeast(
            context.resources.displayMetrics.widthPixels / ScreenUnitUtils.convertDpToPixel(
                width,
                context
            ).toInt()
        )
    }
}



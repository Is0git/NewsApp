package com.is0git.newsapp.ui.views.view_all_custom_motion

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout

class ViewAllCustomMotion : MotionLayout, AppBarLayout.OnOffsetChangedListener {

    var appBarLayout: AppBarLayout? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (parent is AppBarLayout) {
            appBarLayout = parent as AppBarLayout
            appBarLayout!!.addOnOffsetChangedListener(this)
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (appBarLayout != null) {
            val position = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
            progress = position
        }
    }
}
package com.is0git.newsapp.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.is0git.cosmoplanetview.ui.CosmoPlanetView
import com.is0git.newsapp.R

const val HEADLINE_MOTION_LAYOUT_TAG = "headMotionTag"

class HeadlinesMotionLayout : MotionLayout, AppBarLayout.OnOffsetChangedListener {

    var appBarLayout: AppBarLayout? = null
    lateinit var filterButton: MaterialButton
    lateinit var cosmoView: CosmoPlanetView

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
            filterButton = appBarLayout!!.findViewById(R.id.filtersButton)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        cosmoView = findViewById(R.id.cosmoView)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (appBarLayout != null) {
            val position = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
            progress = position
            filterButton.translationY = 10 * position
            if (position > 0.60f) filterButton.alpha = 1 * ((1f - position) / 0.40f)
            val newRotationValue = 0.35f + (0.75f * position)
//            Log.d(HEADLINE_MOTION_LAYOUT_TAG, "scrollradius: $newRotationValue")
//            cosmoView.setAtmosphereRotationAnimated(newRotationValue.coerceAtMost(1f))
//            cosmoView.invalidate()
        }
    }
}
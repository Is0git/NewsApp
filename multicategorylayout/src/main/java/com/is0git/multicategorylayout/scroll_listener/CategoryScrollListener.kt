package com.is0git.multicategorylayout.scroll_listener

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView


class CategoryScrollView : NestedScrollView, Runnable {
    private var lastScrollUpdate: Long = -1
    private var scrollListener: ScrollListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (lastScrollUpdate == -1L) {
            scrollListener?.onScrollStart()
            postDelayed(this, 100)
        }
        lastScrollUpdate = System.currentTimeMillis()
    }

    override fun run() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastScrollUpdate > 100) {
            lastScrollUpdate = -1
            scrollListener?.onScrollEnd()
        } else {
            postDelayed(this, 100)
        }
    }

    interface ScrollListener {
        fun onScrollStart()
        fun onScrollEnd()
    }

    fun setScrollListener(listener: ScrollListener) {
        this.scrollListener = listener
    }
}
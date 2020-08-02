package com.is0git.multicategorylayout.scroll_listener

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView


class CategoryScrollView : NestedScrollView {
    var listener: OnScrollStopListener? = null

    interface OnScrollStopListener {
        fun onScrollStopped(y: Int)
    }

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_UP -> checkIfScrollStopped()
        }
        return super.onTouchEvent(ev)
    }

    var initialY = 0
    private fun checkIfScrollStopped() {
        initialY = scrollY
        postDelayed({
            val updatedY = scrollY
            if (updatedY == initialY) {
                //we've stopped
                if (listener != null) {
                    listener!!.onScrollStopped(scrollY)
                }
            } else {
                initialY = updatedY
                checkIfScrollStopped()
            }
        }, 50)
    }

    fun setOnScrollStoppedListener(yListener: OnScrollStopListener?) {
        listener = yListener
    }
}
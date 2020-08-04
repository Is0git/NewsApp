package com.is0git.multicategorylayout.ui.animation

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class AllListAnimator(val list: RecyclerView) :
    UIAnimator {
    override fun playRevealAnimation() {
        list.visibility = View.VISIBLE
    }

    override fun playHideAnimation() {
        list.visibility = View.INVISIBLE
    }
}

package com.is0git.multicategorylayout.ui.animation

import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach

class CategoriesAnimator(val viewGroup: ViewGroup, val listId: Int) : UIAnimator {
    override fun playRevealAnimation() {
        hideOrShow(View.INVISIBLE)
    }

    override fun playHideAnimation() {
        hideOrShow(View.VISIBLE)
    }

    private fun hideOrShow(visibility: Int) {
        viewGroup.forEach {
            if (it.id != listId) {
                it.visibility = visibility
            }
        }
    }
}
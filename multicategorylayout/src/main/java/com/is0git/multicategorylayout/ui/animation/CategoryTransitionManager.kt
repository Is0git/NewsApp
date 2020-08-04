package com.is0git.multicategorylayout.ui.animation

import android.os.Build
import android.transition.Transition
import android.transition.TransitionManager
import android.view.ViewGroup

class CategoryTransitionManager(var viewGroup: ViewGroup, var transtion: Transition) {

    var listOfAnimators: MutableList<UIAnimator>  = mutableListOf()

    fun show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(viewGroup, transtion)
        }
        listOfAnimators.forEach {
            it.playRevealAnimation()
        }
    }

    fun hide() {
        listOfAnimators.forEach {
            it.playHideAnimation()
        }
    }

    fun addAnimator(uiAnimator: UIAnimator) {
        listOfAnimators.add(uiAnimator)
    }
}
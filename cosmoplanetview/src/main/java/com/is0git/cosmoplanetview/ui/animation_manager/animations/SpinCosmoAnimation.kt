package com.is0git.cosmoplanetview.ui.animation_manager.animations

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import com.is0git.cosmoplanetview.ui.CosmoPlanetView

class SpinCosmoAnimation(private val cosmoPlanetView: CosmoPlanetView) : CosmoAnimation {

    private val spinValueAnimator = ValueAnimator()

    init {
        buildAnimations()
    }

    override fun play() {
        spinValueAnimator.start()
    }

    override fun stop() {
        spinValueAnimator.cancel()
    }

    override fun pause() {
        spinValueAnimator.pause()
    }

    private fun buildAnimations() {
        val spinPlaceHolder =
            PropertyValuesHolder.ofFloat("spinX", 0f, cosmoPlanetView.skinWidth.toFloat() - 20f)
        spinValueAnimator.apply {
            setValues(spinPlaceHolder)
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = 30000
        }
        spinValueAnimator.setValues(spinPlaceHolder)
        spinValueAnimator.addUpdateListener {
            val spinXValue = it.getAnimatedValue("spinX")
            if (spinXValue is Float) {
                cosmoPlanetView.setSpin(spinXValue)
            }
            cosmoPlanetView.invalidate()
        }
    }

    override fun initAnimator() {
        buildAnimations()
    }
}
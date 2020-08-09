package com.is0git.cosmoplanetview.ui.animation_manager.animations

import android.animation.AnimatorSet
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.github.ybq.android.spinkit.animation.interpolator.Ease
import com.is0git.cosmoplanetview.R
import com.is0git.cosmoplanetview.ui.CosmoPlanetView

class EnterCosmoAnimation(private val cosmoPlanetView: CosmoPlanetView) : CosmoAnimation {

    var onEnd: (() -> Unit)? = null
    lateinit var enterAnimatorSet: AnimatorSet
    val context: Context = cosmoPlanetView.context

    init {
        initAnimator()
    }

    override fun play() {
        enterAnimatorSet.start()
    }

    override fun stop() {
        enterAnimatorSet.cancel()
    }

    override fun pause() {
        enterAnimatorSet.pause()
    }

    override fun resume() {
        enterAnimatorSet.resume()
    }

    override fun initAnimator() {
        val startAnimation = ValueAnimator()
        val scaleProperty = PropertyValuesHolder.ofFloat("scale", 2.2f, 1.7f)
        val alphaProperty = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        val translationYProperty = PropertyValuesHolder.ofFloat("translationY", 0f, 0f)
        val spinYProperty = PropertyValuesHolder.ofFloat("spinY", 0f, -1000f)
        startAnimation.apply {
            setValues(scaleProperty, alphaProperty, translationYProperty, spinYProperty)
            interpolator = LinearInterpolator()
            duration =
                cosmoPlanetView.resources.getInteger(R.integer.cosmo_enter_default_time).toLong()
            addUpdateListener {
                val scale = it.getAnimatedValue("scale")
                if (scale is Float) {
                    cosmoPlanetView.scaleX = scale
                    cosmoPlanetView.scaleY = scale
                }
                val alpha = it.getAnimatedValue("alpha")
                if (alpha is Float) cosmoPlanetView.alpha = alpha
                val translationY = it.getAnimatedValue("translationY")
                if (translationY is Float) {
                    cosmoPlanetView.translationY = translationY
                }
                val spinY = it.getAnimatedValue("spinY")
                if (spinY is Float) {
                    cosmoPlanetView.setSpin(spinX = spinY)
                    cosmoPlanetView.invalidate()
                }
            }
        }
        val endAnimation = ValueAnimator().apply {
            duration = 600
            addUpdateListener {
                val scale = it.getAnimatedValue("scale") as Float
                cosmoPlanetView.scaleX = scale
                cosmoPlanetView.scaleY = scale
                val spin = it.getAnimatedValue("spinY") as Float
                cosmoPlanetView.setSpin(spin)
                cosmoPlanetView.invalidate()
            }
        }
        val spinYPropertyEnd = PropertyValuesHolder.ofFloat("spinY", -1000f, -3000f)
        val scalePropertyEnd = PropertyValuesHolder.ofFloat("scale", 1.7f, 1f)
        endAnimation.setValues(scalePropertyEnd, spinYPropertyEnd)
        enterAnimatorSet = AnimatorSet().apply {
            playSequentially(startAnimation, endAnimation)
            doOnEnd {
                onEnd?.invoke()
            }
        }
    }
}
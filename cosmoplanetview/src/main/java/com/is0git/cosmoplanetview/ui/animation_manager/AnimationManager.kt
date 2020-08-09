package com.is0git.cosmoplanetview.ui.animation_manager

import com.is0git.cosmoplanetview.ui.CosmoPlanetView
import com.is0git.cosmoplanetview.ui.animation_manager.animations.CosmoAnimation
import com.is0git.cosmoplanetview.ui.animation_manager.animations.EnterCosmoAnimation
import com.is0git.cosmoplanetview.ui.animation_manager.animations.SpinCosmoAnimation

class AnimationManager(cosmoPlanetView: CosmoPlanetView) {
    val spinAnimator: CosmoAnimation = SpinCosmoAnimation(cosmoPlanetView)
    val enterAnimator: CosmoAnimation = EnterCosmoAnimation(cosmoPlanetView)

    fun playCosmoAnimation(cosmoAnimation: CosmoAnimation) {
        cosmoAnimation.play()
    }

    fun pauseCosmoSpinAnimation(cosmoAnimation: CosmoAnimation) {
        cosmoAnimation.pause()
    }

    fun resumeCosmoAnimation(cosmoAnimation: CosmoAnimation) {
        cosmoAnimation.resume()
    }

    fun cancelCosmoAnimation(cosmoAnimation: CosmoAnimation) {
        cosmoAnimation.stop()
    }
}
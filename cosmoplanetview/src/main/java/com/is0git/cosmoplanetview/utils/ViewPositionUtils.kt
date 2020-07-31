package com.is0git.cosmoplanetview.utils

object ViewPositionUtils {
    fun getMiddleInSpace(totalWidth: Float, objectWidth: Float): Float {
        return (totalWidth / 2f) - (objectWidth / 2f)
    }
}
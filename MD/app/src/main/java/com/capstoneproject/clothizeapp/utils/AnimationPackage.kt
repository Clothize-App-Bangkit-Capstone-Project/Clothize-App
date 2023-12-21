package com.capstoneproject.clothizeapp.utils

import android.animation.ObjectAnimator
import android.view.View

object AnimationPackage {
    fun fadeIn(view: View, duration: Long) =
        ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(duration)

    fun fadeOut(view: View, duration: Long) =
        ObjectAnimator.ofFloat(view, View.ALPHA, 0f).setDuration(duration)

    fun translateX(view: View, duration: Long, vararg position: Float) = ObjectAnimator.ofFloat(
        view, View.TRANSLATION_X, position[0],
        position[1]
    ).setDuration(duration)

    fun translateY(view: View, duration: Long, vararg position: Float) = ObjectAnimator.ofFloat(
        view, View.TRANSLATION_Y,
        position[0],
        position[1]
    ).setDuration(duration)
}
package com.example.calculator_app.extensions

import android.view.View
import android.view.animation.AnimationUtils
import com.example.calculator_app.R

fun View.slideUp() {
    val animation = AnimationUtils.loadAnimation(
        context,
        R.anim.slide_in_bottom)
    this.animation = animation
}
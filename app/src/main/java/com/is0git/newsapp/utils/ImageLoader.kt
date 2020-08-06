package com.is0git.newsapp.utils

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.is0git.newsapp.R

infix fun ImageView.loadImageWith(url: String?) {
    Glide.with(this.context)
        .load(url)
        .centerInside()
        .placeholder(R.drawable.image_placeholder)
        .into(this)
}

infix fun ImageView.loadImageWith(@DrawableRes drawableId: Int) {
    Glide.with(this.context)
        .load(drawableId)
        .centerCrop()
        .into(this)
}
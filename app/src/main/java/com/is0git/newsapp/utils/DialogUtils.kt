package com.is0git.newsapp.utils

import android.app.Activity
import androidx.annotation.LayoutRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder


fun Activity.buildDialogProgressBar(@LayoutRes progressLayout: Int): androidx.appcompat.app.AlertDialog {
    return MaterialAlertDialogBuilder(this)
        .setBackground(ResourcesCompat.getDrawable(resources, android.R.color.transparent, null))
        .setCancelable(false)
        .setView(progressLayout)
        .create()
}

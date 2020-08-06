package com.is0git.newsapp.utils

import android.content.Context
import androidx.core.os.ConfigurationCompat

object LocaleUtils {
    fun Context.getCurrentCountryCode(): String {
        return ConfigurationCompat.getLocales(resources.configuration).get(0).country
    }
}
package com.is0git.newsapp.utils

import java.text.SimpleDateFormat
import java.util.*


object DateUtils {

    const val pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val pattern2 = "EEE, d MMM yyyy HH:mm:ss"

    fun formatDate(
        date: String?,
        inPattern: String = pattern,
        outPattern: String = pattern2
    ): String? {
        if (date == null) return null
        val locale = Locale.getDefault()
        val simpleDateFormat = SimpleDateFormat(inPattern, locale)
        val mDate = simpleDateFormat.parse(date)
        val outSimpleDateFormat = SimpleDateFormat(outPattern, locale)
        return outSimpleDateFormat.format(mDate)
    }
}
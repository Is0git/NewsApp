package com.is0git.newsapp.utils

import android.view.View
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow


@ExperimentalCoroutinesApi
fun View.clicks(action: (view: View) -> Unit): Flow<View> =
    kotlinx.coroutines.flow.callbackFlow<View> {
        setOnClickListener {
            action(it)
            offer(it)
        }
        awaitClose { setOnClickListener(null) }
    }
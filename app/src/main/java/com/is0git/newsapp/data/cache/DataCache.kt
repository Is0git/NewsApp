package com.is0git.newsapp.data.cache

import androidx.lifecycle.LiveData

interface DataCache<T> {
     suspend fun cacheData(data: List<T>?)
     fun getCachedLiveData(): LiveData<List<T>>
}
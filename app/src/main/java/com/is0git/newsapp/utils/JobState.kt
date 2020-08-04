package com.is0git.newsapp.utils

import androidx.lifecycle.MutableLiveData

sealed class JobState {
    object Idle : JobState()
    object JobStarted : JobState()
    class JobFailed(val throwable: Throwable?) : JobState()
    object JobCompleted : JobState()


}

fun MutableLiveData<in JobState>.resetJob() {
    this.postValue(JobState.Idle)
}
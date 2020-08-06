package com.is0git.newsapp.vm.single_job

import androidx.lifecycle.MutableLiveData
import com.is0git.newsapp.utils.JobState
import javax.inject.Inject

open class DefaultSingleJobRepository @Inject constructor() : SingleJobRepository {
    @Inject
    lateinit var jobStates: MutableLiveData<JobState>

    override fun onJobStart() {
        jobStates.postValue(JobState.JobStarted)
    }

    override fun onJobFailed(throwable: Throwable) {
        jobStates.postValue(JobState.JobFailed(throwable))

    }

    override fun onJobCompleted() {
        jobStates.postValue(JobState.JobCompleted)

    }

    override fun onJobIdle() {
        jobStates.postValue(JobState.Idle)
    }
}
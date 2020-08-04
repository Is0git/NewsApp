package com.is0git.newsapp.job_handlers

import androidx.lifecycle.MutableLiveData
import com.is0git.newsapp.utils.JobState
import com.is0git.newsapp.vm.single_job_viewmodel.JobStateHandler

class DefaultJobHandler(val jobStates: MutableLiveData<JobState>) : JobStateHandler {
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
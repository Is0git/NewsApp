package com.is0git.newsapp.vm.single_job

interface JobStateHandler {
    fun onJobStart()
    fun onJobFailed(throwable: Throwable)
    fun onJobCompleted()
    fun onJobIdle()
}
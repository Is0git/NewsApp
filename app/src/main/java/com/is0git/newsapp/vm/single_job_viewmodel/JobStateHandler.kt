package com.is0git.newsapp.vm.single_job_viewmodel

interface JobStateHandler {
    fun onJobStart()
    fun onJobFailed(throwable: Throwable)
    fun onJobCompleted()
    fun onJobIdle()
}
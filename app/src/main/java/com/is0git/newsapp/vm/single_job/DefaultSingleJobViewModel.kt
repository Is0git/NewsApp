package com.is0git.newsapp.vm.single_job

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel

open class DefaultSingleJobViewModel @ViewModelInject constructor(val repo: DefaultSingleJobRepository) :
    ViewModel() {
    val jobStatesLiveData = repo.jobStates
}
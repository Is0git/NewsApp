package com.is0git.newsapp.di.modules

import androidx.lifecycle.MutableLiveData
import com.is0git.newsapp.job_handlers.DefaultJobHandler
import com.is0git.newsapp.utils.JobState
import com.is0git.newsapp.vm.single_job_viewmodel.JobStateHandler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class JobStatesModule {
    companion object {
        @Provides
        fun getJobStatesLiveData(): MutableLiveData<JobState> {
            return MutableLiveData(JobState.Idle)
        }
    }

    @Binds
    abstract fun defaultJobHandler(defaultJobHandler: DefaultJobHandler): JobStateHandler
}
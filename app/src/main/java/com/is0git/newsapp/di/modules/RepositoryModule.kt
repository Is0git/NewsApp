package com.is0git.newsapp.di.modules

import com.is0git.newsapp.vm.single_job_viewmodel.DefaultSingleJobRepository
import com.is0git.newsapp.vm.single_job_viewmodel.SingleJobRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun getDefaultSingleJobRepo(defaultSingleJobRepository: DefaultSingleJobRepository): SingleJobRepository
}
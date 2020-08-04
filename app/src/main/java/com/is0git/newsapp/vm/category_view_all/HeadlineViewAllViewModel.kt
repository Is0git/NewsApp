package com.is0git.newsapp.vm.category_view_all

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig

class HeadlineViewAllViewModel @ViewModelInject constructor(val repo: HeadlineViewAllRepository, @Assisted savedStateHandle: SavedStateHandle) : ViewModel() {

    val pager = Pager(
        config = PagingConfig(10),
        remoteMediator = repo.mediator
    ) {
        repo.pagingSource
    }
}
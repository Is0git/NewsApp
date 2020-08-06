package com.is0git.newsapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.is0git.newsapp.data.db.dao.HeadlineDao
import com.is0git.newsapp.data.db.dao.PagedHeadlineDao
import com.is0git.newsapp.data.db.dao.SourceDao
import com.is0git.newsapp.models.common.ArticlesItem
import com.is0git.newsapp.models.sources.SourcesItem


@Database(entities = [SourcesItem::class, ArticlesItem::class], version = 1, exportSchema = true)
abstract class MainDatabase : RoomDatabase() {
    abstract fun getSourceDao(): SourceDao
    abstract fun getHeadlinesDao(): HeadlineDao
    abstract fun pagedHeadlineDao(): PagedHeadlineDao
}
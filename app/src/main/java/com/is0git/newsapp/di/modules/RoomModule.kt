package com.is0git.newsapp.di.modules

import android.content.Context
import androidx.room.Room
import com.is0git.newsapp.data.db.MainDatabase
import com.is0git.newsapp.data.db.dao.HeadlineDao
import com.is0git.newsapp.data.db.dao.SourceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RoomModule {

    @Provides
    @Singleton
    @Synchronized
    fun getMainDatabase(@ApplicationContext context: Context): MainDatabase {
        return Room
            .databaseBuilder(context, MainDatabase::class.java, "main_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun getSourceDao(mainDatabase: MainDatabase): SourceDao {
        return mainDatabase.getSourceDao()
    }

    @Provides
    fun getHeadlinesDao(mainDatabase: MainDatabase): HeadlineDao {
        return mainDatabase.getHeadlinesDao()
    }
}
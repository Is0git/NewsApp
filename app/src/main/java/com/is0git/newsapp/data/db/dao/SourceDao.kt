package com.is0git.newsapp.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.is0git.newsapp.network.models.sources.SourcesItem

@Dao
abstract class SourceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSourceItems(list: List<SourcesItem>?)

    @Query("SELECT * FROM source_table LIMIT :itemsLimit")
    abstract fun getSourcesLiveData(itemsLimit: Int): LiveData<List<SourcesItem>>

    @Query("DELETE FROM source_table")
    abstract suspend fun deleteAll()
}
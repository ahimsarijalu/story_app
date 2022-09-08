package com.ahimsarijalu.storyapp.data.local.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ahimsarijalu.storyapp.data.remote.response.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ListStoryItem>)

    @Query("SELECT * FROM story")
    fun getAllStoryFromLocal(): PagingSource<Int, ListStoryItem>

    @Query("SELECT * FROM story")
    fun getAllStoryFromLocalWithLocation(): LiveData<List<ListStoryItem>>

    @Query("DELETE FROM story")
    suspend fun deleteAllStoryFromLocal()
}
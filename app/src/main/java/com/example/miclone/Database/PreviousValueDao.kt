package com.example.miclone.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.miclone.entities.PreviousValueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreviousValueDao {

    @Query("SELECT * FROM previous_value_table")
    fun readPreviousValue(): Flow<List<PreviousValueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreviousValue(previousValueEntity: PreviousValueEntity)
}
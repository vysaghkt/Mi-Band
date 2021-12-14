package com.example.miclone.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.miclone.entities.PreviousValueEntity

@Database(entities = [PreviousValueEntity::class], version = 1, exportSchema = false)
abstract class PreviousValueDatabase: RoomDatabase() {

    abstract fun previousValueDao(): PreviousValueDao
}
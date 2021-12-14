package com.example.miclone.di

import android.content.Context
import androidx.room.Room
import com.example.miclone.Database.PreviousValueDao
import com.example.miclone.Database.PreviousValueDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePreviousValueDao(previousValueDatabase: PreviousValueDatabase): PreviousValueDao {
        return previousValueDatabase.previousValueDao()
    }

    @Provides
    @Singleton
    fun providePreviousValueDatabase(@ApplicationContext context: Context): PreviousValueDatabase {
        return Room.databaseBuilder(
            context,
            PreviousValueDatabase::class.java,
            "database"
        ).build()
    }
}
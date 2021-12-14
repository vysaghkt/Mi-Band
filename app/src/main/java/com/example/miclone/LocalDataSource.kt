package com.example.miclone

import com.example.miclone.Database.PreviousValueDao
import com.example.miclone.entities.PreviousValueEntity
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class LocalDataSource @Inject constructor(
    private val previousValueDao: PreviousValueDao
){
    fun readPreviousValue():Flow<List<PreviousValueEntity>>{
        return previousValueDao.readPreviousValue()
    }

    suspend fun insertPreviousValue(previousValueEntity: PreviousValueEntity){
        previousValueDao.insertPreviousValue(previousValueEntity)
    }
}
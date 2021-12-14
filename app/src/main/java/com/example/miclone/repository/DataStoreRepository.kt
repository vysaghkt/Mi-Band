package com.example.miclone.repository

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.example.miclone.contants.Constants.DEFAULT_STEP_GOAL
import com.example.miclone.contants.Constants.PREFERENCE_NAME
import com.example.miclone.contants.Constants.PREFERENCE_STEP_GOAL
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferenceKeys{
        val goalStep = preferencesKey<Int>(PREFERENCE_STEP_GOAL)
    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCE_NAME
    )

    suspend fun storeStepGoal(count: Int){
        dataStore.edit {
            it[PreferenceKeys.goalStep] = count
        }
    }

    val getStepGoals = dataStore.data
        .catch { exception ->
            if (exception is IOException){
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }.map {
            val value = it[PreferenceKeys.goalStep] ?: DEFAULT_STEP_GOAL
            value
        }
}
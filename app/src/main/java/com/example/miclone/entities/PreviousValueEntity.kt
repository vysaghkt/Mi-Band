package com.example.miclone.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "previous_value_table")
data class PreviousValueEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val battery: String,
    val steps: String,
    val calories: String,
    val distance: String
)
package com.example.moneytracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pockets")
data class PocketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

package com.example.moneytracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PocketDao {

    @Query("SELECT * FROM pockets ORDER BY name ASC")
    fun getAll(): Flow<List<PocketEntity>>

    @Query("SELECT COUNT(*) FROM pockets")
    suspend fun count(): Int

    @Insert
    suspend fun insert(pocket: PocketEntity): Long
}

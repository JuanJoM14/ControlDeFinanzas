package com.example.moneytracker.data

import android.content.Context
import androidx.room.Room
import com.example.moneytracker.data.db.AppDatabase
import com.example.moneytracker.data.repository.TransactionRepository

object AppModule {
    fun provideRepository(context: Context): TransactionRepository {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "money_tracker_db"
        ).build()

        return TransactionRepository(db.transactionDao())
    }
}
package com.example.moneytracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TransactionEntity::class], version = 1)
@TypeConverters(TransactionTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}

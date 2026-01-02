package com.example.moneytracker.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.moneytracker.data.db.AppDatabase
import com.example.moneytracker.data.repository.TransactionRepository

object AppModule {
    
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create pockets table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS pockets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )
            """)
            
            // Insert default pocket with id=1 to match existing transactions
            // Using a fixed timestamp value to avoid SQL injection concerns
            val timestamp = System.currentTimeMillis()
            db.execSQL(
                "INSERT INTO pockets (id, name, createdAt) VALUES (?, ?, ?)",
                arrayOf(1, "General", timestamp)
            )
        }
    }
    
    fun provideRepository(context: Context): TransactionRepository {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "money_tracker_db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()

        return TransactionRepository(db.transactionDao(), db.pocketDao())
    }
}
package com.example.moneytracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moneytracker.data.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val amountCents: Long,
    val description: String,
    val person: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

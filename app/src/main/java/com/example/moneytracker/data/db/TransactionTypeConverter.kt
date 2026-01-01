package com.example.moneytracker.data.db

import androidx.room.TypeConverter
import com.example.moneytracker.data.model.TransactionType

class TransactionTypeConverter {
    @TypeConverter
    fun fromType(type: TransactionType): String = type.name

    @TypeConverter
    fun toType(value: String): TransactionType = TransactionType.valueOf(value)
}

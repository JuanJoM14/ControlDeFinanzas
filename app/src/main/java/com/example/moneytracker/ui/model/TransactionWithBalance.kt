package com.example.moneytracker.ui.model

import com.example.moneytracker.data.db.TransactionEntity

data class TransactionWithBalance(
    val transaction: TransactionEntity,
    val balanceAfterCents: Long
)

package com.example.moneytracker.data.db

data class PersonPocketDebtRow(
    val person: String,
    val pocketId: Long,
    val pocketName: String,
    val debtCents: Long
)

package com.example.moneytracker.data.repository

import com.example.moneytracker.data.db.TransactionDao
import com.example.moneytracker.data.db.TransactionEntity
import kotlinx.coroutines.flow.Flow
import com.example.moneytracker.data.db.PersonDebtRow

class TransactionRepository(private val dao: TransactionDao) {
    fun getAll(): Flow<List<TransactionEntity>> = dao.getAll()
    suspend fun add(tx: TransactionEntity) = dao.insert(tx)
    fun getDebtsByPerson() = dao.getDebtsByPerson()
    suspend fun update(tx: TransactionEntity) = dao.update(tx)
}

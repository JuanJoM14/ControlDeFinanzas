package com.example.moneytracker.data.repository

import com.example.moneytracker.data.db.PocketDao
import com.example.moneytracker.data.db.PocketEntity
import com.example.moneytracker.data.db.TransactionDao
import com.example.moneytracker.data.db.TransactionEntity
import com.example.moneytracker.data.db.PersonDebtRow
import com.example.moneytracker.data.db.PersonPocketDebtRow
import com.example.moneytracker.data.db.PocketSummaryRow
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val pocketDao: PocketDao
) {

    /* ---------------- TRANSACTIONS ---------------- */

    fun getAll(): Flow<List<TransactionEntity>> =
        transactionDao.getAll()

    fun getByPocket(pocketId: Long): Flow<List<TransactionEntity>> =
        transactionDao.getByPocket(pocketId)

    suspend fun add(tx: TransactionEntity) =
        transactionDao.insert(tx)

    suspend fun update(tx: TransactionEntity) =
        transactionDao.update(tx)

    /* ---------------- POCKETS ---------------- */

    fun getPockets(): Flow<List<PocketEntity>> =
        pocketDao.getAll()

    suspend fun createPocket(name: String): Long =
        pocketDao.insert(PocketEntity(name = name.trim()))

    suspend fun pocketsCount(): Int =
        pocketDao.count()

    /* ---------------- SUMMARY ---------------- */

    fun getPocketSummary(pocketId: Long): Flow<PocketSummaryRow> =
        transactionDao.getPocketSummary(pocketId)

    /* ---------------- DEBTS ---------------- */

    fun getDebtsByPerson(): Flow<List<PersonDebtRow>> =
        transactionDao.getDebtsByPerson()

    fun getDebtsByPersonAndPocket(): Flow<List<PersonPocketDebtRow>> =
        transactionDao.getDebtsByPersonAndPocket()
}

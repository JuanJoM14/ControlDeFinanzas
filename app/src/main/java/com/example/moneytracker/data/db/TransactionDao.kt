package com.example.moneytracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.Update

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(tx: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAll(): Flow<List<TransactionEntity>>
    @Query("""
    SELECT person, 
           SUM(CASE WHEN type = 'LENT' THEN amountCents ELSE 0 END) -
           SUM(CASE WHEN type = 'PAID_ME' THEN amountCents ELSE 0 END) AS balanceCents
    FROM transactions
    WHERE person IS NOT NULL AND person != ''
    GROUP BY person
    HAVING balanceCents != 0
    ORDER BY balanceCents DESC
""")
    fun getDebtsByPerson(): Flow<List<PersonDebtRow>>

    @Update
    suspend fun update(tx: TransactionEntity)
}

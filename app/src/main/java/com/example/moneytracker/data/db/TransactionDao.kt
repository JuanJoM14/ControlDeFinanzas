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

    @Query("""
    SELECT 
        t.person AS person,
        t.pocketId AS pocketId,
        p.name AS pocketName,
        SUM(CASE 
            WHEN t.type = 'LENT' THEN t.amountCents
            WHEN t.type = 'PAID_ME' THEN -t.amountCents
            ELSE 0
        END) AS debtCents
    FROM transactions t
    JOIN pockets p ON p.id = t.pocketId
    WHERE t.person IS NOT NULL AND t.person != ''
      AND (t.type = 'LENT' OR t.type = 'PAID_ME')
    GROUP BY t.person, t.pocketId
    HAVING debtCents > 0
    ORDER BY t.person ASC, p.name ASC
""")
    fun getDebtsByPersonAndPocket(): Flow<List<PersonPocketDebtRow>>

    @Query("SELECT * FROM transactions WHERE pocketId = :pocketId ORDER BY createdAt DESC")
    fun getByPocket(pocketId: Long): kotlinx.coroutines.flow.Flow<List<TransactionEntity>>

    @Query("""
    SELECT 
      SUM(CASE WHEN type IN ('RECEIVED','PAID_ME') THEN amountCents ELSE 0 END) AS incomeCents,
      SUM(CASE WHEN type IN ('SPENT','LENT') THEN amountCents ELSE 0 END) AS expenseCents
    FROM transactions
    WHERE pocketId = :pocketId
""")
    fun getPocketSummary(pocketId: Long): kotlinx.coroutines.flow.Flow<PocketSummaryRow>
}

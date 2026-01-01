package com.example.moneytracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.data.db.TransactionEntity
import com.example.moneytracker.data.model.TransactionType
import com.example.moneytracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import com.example.moneytracker.data.db.PersonDebtRow
import com.example.moneytracker.ui.model.TransactionWithBalance
import kotlinx.coroutines.flow.map


data class Summary(
    val balanceCents: Long,
    val incomeCents: Long,
    val expenseCents: Long
)
class TransactionViewModel(private val repo: TransactionRepository) : ViewModel() {

    val transactions: StateFlow<List<TransactionEntity>> =
        repo.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val transactionsWithBalance =
        transactions.map { list ->
            var runningBalance = 0L

            list
                .sortedBy { it.createdAt } // orden cronológico
                .map { tx ->
                    runningBalance += when (tx.type) {
                        TransactionType.RECEIVED,
                        TransactionType.PAID_ME -> tx.amountCents
                        TransactionType.SPENT,
                        TransactionType.LENT -> -tx.amountCents
                    }

                    TransactionWithBalance(
                        transaction = tx,
                        balanceAfterCents = runningBalance
                    )
                }
                .reversed() // volvemos a mostrar la más reciente arriba
        }
    val debtsByPerson: StateFlow<List<PersonDebtRow>> =
        repo.getDebtsByPerson()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val summary: StateFlow<Summary> =
        transactions.map { list ->
            val income = list.filter {
                it.type == TransactionType.RECEIVED || it.type == TransactionType.PAID_ME
            }.sumOf { it.amountCents }

            val expenses = list.filter {
                it.type == TransactionType.SPENT || it.type == TransactionType.LENT
            }.sumOf { it.amountCents }

            Summary(
                balanceCents = income - expenses,
                incomeCents = income,
                expenseCents = expenses
            )

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            Summary(0, 0, 0)
        )
    fun addTransaction(type: TransactionType, amountCents: Long, description: String, person: String?) {
        viewModelScope.launch {
            repo.add(
                TransactionEntity(
                    type = type,
                    amountCents = amountCents,
                    description = description.trim(),
                    person = person?.trim()?.takeIf { it.isNotBlank() }
                )
            )
        }
    }
    fun updateTransaction(tx: TransactionEntity) {
            viewModelScope.launch {
                repo.update(tx)
            }
        }
    }

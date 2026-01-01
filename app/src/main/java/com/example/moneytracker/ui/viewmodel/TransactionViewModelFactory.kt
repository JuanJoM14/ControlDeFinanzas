package com.example.moneytracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moneytracker.data.repository.TransactionRepository

class TransactionViewModelFactory(
    private val repo: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

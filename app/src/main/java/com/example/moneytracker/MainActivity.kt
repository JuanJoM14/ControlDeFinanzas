package com.example.moneytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneytracker.data.AppModule
import com.example.moneytracker.ui.MoneyTrackerScreen
import com.example.moneytracker.ui.viewmodel.TransactionViewModel
import com.example.moneytracker.ui.viewmodel.TransactionViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = AppModule.provideRepository(applicationContext)
        val factory = TransactionViewModelFactory(repo)

        setContent {
            MaterialTheme {
                val vm: TransactionViewModel = viewModel(factory = factory)
                MoneyTrackerScreen(vm)
            }
        }
    }
}

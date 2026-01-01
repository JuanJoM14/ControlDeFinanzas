package com.example.moneytracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.ui.viewmodel.TransactionViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtsScreen(vm: TransactionViewModel, onBack: () -> Unit) {
    val debts by vm.debtsByPerson.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deudas") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Atrás") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            if (debts.isEmpty()) {
                Text("No hay deudas registradas.")
            } else {
                LazyColumn {
                    items(debts) { row ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(row.person, style = MaterialTheme.typography.titleMedium)
                                Text("Balance: ${formatCOP(row.balanceCents)}")
                                Text(
                                    if (row.balanceCents > 0) "Te debe" else "Tú le debes",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

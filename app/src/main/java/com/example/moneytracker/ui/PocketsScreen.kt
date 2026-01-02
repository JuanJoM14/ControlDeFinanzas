package com.example.moneytracker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketsScreen(
    vm: TransactionViewModel,
    onSelect: (Long) -> Unit,
    onBack: () -> Unit
) {
    val pockets by vm.pockets.collectAsState()

    var showNewPocket by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bolsillos") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Atrás") }
                },
                actions = {
                    TextButton(onClick = { showNewPocket = true }) { Text("Nuevo") }
                }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp)) {
            items(pockets) { p ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onSelect(p.id) }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(p.name, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }

    if (showNewPocket) {
        AlertDialog(
            onDismissRequest = { showNewPocket = false },
            title = { Text("Nuevo bolsillo") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.createPocket(newName)
                    newName = ""
                    showNewPocket = false
                }) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { showNewPocket = false }) { Text("Cancelar") }
            }
        )
    }
}

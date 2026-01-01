package com.example.moneytracker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.data.model.TransactionType
import com.example.moneytracker.ui.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import com.example.moneytracker.data.db.TransactionEntity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyTrackerScreen(vm: TransactionViewModel) {
    val list by vm.transactionsWithBalance.collectAsState(initial = emptyList())

    val summary by vm.summary.collectAsState()
    var showDebts by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }

    var editingTx by remember { mutableStateOf<TransactionEntity?>(null) }


    if (showDebts) {
        DebtsScreen(vm = vm, onBack = { showDebts = false })
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control de dinero") },
                actions = {
                    TextButton(onClick = { showDebts = true }) { Text("Deudas") }
                }
            )
        },        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) { Text("+") }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Transacciones: ${list.size}")
            Card(
                Modifier.fillMaxWidth().padding(vertical = 12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Saldo: ${formatCOP(summary.balanceCents)}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("Ingresos: ${formatCOP(summary.incomeCents)}")
                    Text("Gastos: ${formatCOP(summary.expenseCents)}")
                }
            }
            Spacer(Modifier.height(12.dp))

            LazyColumn {
                items(list) { item ->
                    val tx = item.transaction
                    val balanceAfter = item.balanceAfterCents
                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(Date(tx.createdAt))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { editingTx = tx },
                        colors = CardDefaults.cardColors(
                            containerColor = cardColorByType(tx.type)
                        )
                    ) {
                        Column(Modifier.padding(12.dp)) {

                            // Título (tipo + monto)
                            Text(
                                text = "${labelType(tx.type)} ${formatCOP(tx.amountCents)}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            // Persona (si existe)
                            if (!tx.person.isNullOrBlank()) {
                                Text(
                                    text = "Persona: ${tx.person}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            // Descripción
                            Text(
                                text = tx.description,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // Saldo al momento
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Saldo al momento: ${formatCOP(balanceAfter)}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            // Fecha
                            Text(
                                text = date,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }


        if (showDialog) {
            AddTransactionDialog(
                onDismiss = { showDialog = false },
                onSave = { type, amountCents, desc, person ->
                    vm.addTransaction(type, amountCents, desc, person)
                    showDialog = false
                }
            )
        }

        // 2️⃣ Diálogo para EDITAR transacción (al tocar una tarjeta)
        editingTx?.let { txToEdit ->
            AddTransactionDialog(
                initialType = txToEdit.type,
                initialAmountCents = txToEdit.amountCents,
                initialDescription = txToEdit.description,
                initialPerson = txToEdit.person ?: "",
                title = "Editar transacción",
                confirmText = "Guardar cambios",
                onDismiss = { editingTx = null },
                onSave = { type, amountCents, desc, person ->
                    vm.updateTransaction(
                        txToEdit.copy(
                            type = type,
                            amountCents = amountCents,
                            description = desc,
                            person = person.ifBlank { null }
                        )
                    )
                    editingTx = null
                }
            )
        }
    }
}

@Composable
fun AddTransactionDialog(
    initialType: TransactionType = TransactionType.SPENT,
    initialAmountCents: Long = 0L,
    initialDescription: String = "",
    initialPerson: String = "",
    title: String = "Nueva transacción",
    confirmText: String = "Guardar",
    onDismiss: () -> Unit,
    onSave: (
        TransactionType,
        Long,
        String,
        String
    ) -> Unit
) {
    var type by remember { mutableStateOf(initialType) }
    var amountText by remember { mutableStateOf((initialAmountCents / 100).toString()) }
    var description by remember { mutableStateOf(initialDescription) }
    var person by remember { mutableStateOf(initialPerson) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val amount = amountText.trim().toLongOrNull()
                if (amount == null || amount <= 0) { error = "Monto inválido"; return@TextButton }
                if (description.trim().isBlank()) { error = "Escribe una descripción"; return@TextButton }

                val needsPerson = (type == TransactionType.LENT || type == TransactionType.PAID_ME)
                if (needsPerson && person.trim().isBlank()) { error = "Escribe el nombre"; return@TextButton }

                onSave(type, amount * 100, description, person.trim())
            }) { Text(text = confirmText) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text(text = title) },
        text = {
            Column {
                Text("Tipo")
                TypeSelector(type = type, onChange = { type = it })

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Monto (COP) sin puntos") },
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                val showPerson = (type == TransactionType.LENT || type == TransactionType.PAID_ME)
                if (showPerson) {
                    OutlinedTextField(
                        value = person,
                        onValueChange = { person = it },
                        label = { Text("Persona") },
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    minLines = 2
                )

                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

@Composable
private fun TypeSelector(type: TransactionType, onChange: (TransactionType) -> Unit) {
    Column {
        Row {
            RadioButton(type == TransactionType.SPENT, onClick = { onChange(TransactionType.SPENT) })
            Text("Gasté...")
        }
        Row {
            RadioButton(type == TransactionType.LENT, onClick = { onChange(TransactionType.LENT) })
            Text("Presté a...")
        }
        Row {
            RadioButton(type == TransactionType.PAID_ME, onClick = { onChange(TransactionType.PAID_ME) })
            Text("X me pagó...")
        }
        Row {
            RadioButton(type == TransactionType.RECEIVED, onClick = { onChange(TransactionType.RECEIVED) })
            Text("Recibí de...")
        }
    }
}

private fun labelType(type: TransactionType): String = when (type) {
    TransactionType.SPENT -> "Gasté"
    TransactionType.LENT -> "Presté"
    TransactionType.PAID_ME -> "Me pagaron"
    TransactionType.RECEIVED -> "Recibí"
}

fun cardColorByType(type: TransactionType): Color =
    when (type) {
        TransactionType.RECEIVED -> Color(0xFFE8F5E9) // verde suave
        TransactionType.SPENT -> Color(0xFFFAD1D1)    // rojo suave
        TransactionType.LENT -> Color(0xFFFFF3E0)     // naranja suave
        TransactionType.PAID_ME -> Color(0xFFE3F2FD)  // azul suave
    }

fun formatCOP(amountCents: Long): String {
    val cop = amountCents / 100
    return "$" + "%,d".format(cop).replace(',', '.')
}

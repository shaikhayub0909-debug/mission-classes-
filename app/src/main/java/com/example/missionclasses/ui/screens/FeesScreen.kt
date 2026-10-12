package com.example.missionclasses.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.missionclasses.data.model.FeeInvoice
import com.example.missionclasses.data.model.Student
import com.example.missionclasses.ui.viewmodel.MissionViewModel
import com.example.missionclasses.ui.viewmodel.UiState
import com.example.ui.theme.MissionBluePrimary
import com.example.ui.theme.MissionEmeraldSuccess
import com.example.ui.theme.MissionGoldAccent
import com.example.ui.theme.MissionRoseError

@Composable
fun FeesScreen(
    viewModel: MissionViewModel
) {
    val feesState by viewModel.fees.collectAsState()
    val studentsState by viewModel.students.collectAsState()

    val fees = (feesState as? UiState.Success)?.data ?: emptyList()
    val students = (studentsState as? UiState.Success)?.data ?: emptyList()

    var statusFilter by remember { mutableStateOf("ALL") }
    var showCreateInvoiceDialog by remember { mutableStateOf(false) }
    var payingInvoice by remember { mutableStateOf<FeeInvoice?>(null) }

    val totalAmount = fees.sumOf { it.amount }
    val totalCollected = fees.sumOf { it.paidAmount }
    val totalPending = totalAmount - totalCollected

    val filteredFees = fees.filter {
        statusFilter == "ALL" || it.status.equals(statusFilter, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize().testTag("fees_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Revenue Accounting Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Institutional Fee Revenue Summary",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FeeMetricCard(
                                title = "Collected",
                                value = "₹${totalCollected.toInt()}",
                                color = MissionEmeraldSuccess,
                                modifier = Modifier.weight(1f)
                            )
                            FeeMetricCard(
                                title = "Outstanding",
                                value = "₹${totalPending.toInt()}",
                                color = MissionRoseError,
                                modifier = Modifier.weight(1f)
                            )
                            FeeMetricCard(
                                title = "Total Billed",
                                value = "₹${totalAmount.toInt()}",
                                color = MissionBluePrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Status Filter Tabs
            item {
                val filters = listOf("ALL", "PENDING", "PARTIAL", "PAID")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filters) { f ->
                        val isSelected = statusFilter == f
                        Surface(
                            onClick = { statusFilter = f },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) MissionBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.height(34.dp)
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = f,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Invoices & Fee Plans (${filteredFees.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (filteredFees.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "No fee invoices match the selected filter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            } else {
                items(filteredFees, key = { it.id.ifEmpty { it.invoiceNo } }) { invoice ->
                    InvoiceCard(
                        invoice = invoice,
                        onRecordPayment = { payingInvoice = invoice }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // Add Invoice FAB
        FloatingActionButton(
            onClick = { showCreateInvoiceDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("create_fee_fab"),
            containerColor = MissionBluePrimary,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Invoice")
        }
    }

    if (showCreateInvoiceDialog) {
        CreateInvoiceDialog(
            students = students,
            onDismiss = { showCreateInvoiceDialog = false },
            onConfirm = { newInvoice ->
                viewModel.createFee(newInvoice)
                showCreateInvoiceDialog = false
            }
        )
    }

    payingInvoice?.let { inv ->
        RecordPaymentDialog(
            invoice = inv,
            onDismiss = { payingInvoice = null },
            onConfirm = { amount ->
                viewModel.recordFeePayment(inv, amount)
                payingInvoice = null
            }
        )
    }
}

@Composable
fun FeeMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
fun InvoiceCard(
    invoice: FeeInvoice,
    onRecordPayment: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = invoice.studentName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${invoice.invoiceNo} • Due: ${invoice.dueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (invoice.status) {
                        "PAID" -> MissionEmeraldSuccess.copy(alpha = 0.15f)
                        "PARTIAL" -> MissionGoldAccent.copy(alpha = 0.15f)
                        else -> MissionRoseError.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = invoice.status,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when (invoice.status) {
                            "PAID" -> MissionEmeraldSuccess
                            "PARTIAL" -> MissionGoldAccent
                            else -> MissionRoseError
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = invoice.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))
            val progress = if (invoice.amount > 0) (invoice.paidAmount / invoice.amount).toFloat() else 1f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (progress >= 1f) MissionEmeraldSuccess else MissionBluePrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Paid: ₹${invoice.paidAmount.toInt()} / ₹${invoice.amount.toInt()}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (invoice.status != "PAID") {
                    Button(
                        onClick = onRecordPayment,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MissionBluePrimary),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Record Pay", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateInvoiceDialog(
    students: List<Student>,
    onDismiss: () -> Unit,
    onConfirm: (FeeInvoice) -> Unit
) {
    var studentName by remember { mutableStateOf(students.firstOrNull()?.fullName ?: "Aarav Patel") }
    var title by remember { mutableStateOf("Term 2 Tuition Fee") }
    var amountStr by remember { mutableStateOf("35000") }
    var dueDate by remember { mutableStateOf("2026-11-15") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Fee Invoice", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Student Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Fee Particulars / Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Total Amount (₹) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (studentName.isNotBlank() && title.isNotBlank() && amount > 0) {
                        onConfirm(
                            FeeInvoice(
                                studentName = studentName,
                                invoiceNo = "INV-${System.currentTimeMillis() % 100000}",
                                title = title,
                                amount = amount,
                                paidAmount = 0.0,
                                dueDate = dueDate,
                                status = "PENDING"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MissionBluePrimary)
            ) {
                Text("Generate Invoice")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun RecordPaymentDialog(
    invoice: FeeInvoice,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    val remaining = (invoice.amount - invoice.paidAmount).coerceAtLeast(0.0)
    var payAmountStr by remember { mutableStateOf(remaining.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Fee Payment", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Student: ${invoice.studentName}\nInvoice: ${invoice.invoiceNo}\nRemaining Balance: ₹${remaining.toInt()}",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = payAmountStr,
                    onValueChange = { payAmountStr = it },
                    label = { Text("Payment Received (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pay = payAmountStr.toDoubleOrNull() ?: 0.0
                    if (pay > 0) {
                        onConfirm(pay)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MissionEmeraldSuccess)
            ) {
                Text("Confirm Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.missionclasses.data.model.AttendanceRecord
import com.example.missionclasses.data.model.Student
import com.example.missionclasses.ui.viewmodel.MissionViewModel
import com.example.missionclasses.ui.viewmodel.UiState
import com.example.ui.theme.MissionBluePrimary
import com.example.ui.theme.MissionEmeraldSuccess
import com.example.ui.theme.MissionGoldAccent
import com.example.ui.theme.MissionRoseError
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceScreen(
    viewModel: MissionViewModel
) {
    val studentsState by viewModel.students.collectAsState()
    val attendanceState by viewModel.attendance.collectAsState()

    val students = (studentsState as? UiState.Success)?.data ?: emptyList()
    val attendanceRecords = (attendanceState as? UiState.Success)?.data ?: emptyList()

    val todayDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // Map student name to latest status for today
    val todayMap = attendanceRecords
        .filter { it.date == todayDate }
        .associateBy({ it.studentName }, { it.status })

    val presentCount = todayMap.values.count { it == "PRESENT" }
    val absentCount = todayMap.values.count { it == "ABSENT" }
    val lateCount = todayMap.values.count { it == "LATE" }
    val excusedCount = todayMap.values.count { it == "EXCUSED" }
    val markedTotal = todayMap.size
    val totalStudents = students.size
    val attendancePct = if (markedTotal > 0) ((presentCount.toDouble() / markedTotal) * 100).toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("attendance_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date & Bulk Actions Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = MissionBluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Daily Roll Call",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Date: $todayDate (Today)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.markAllPresent(students, todayDate) },
                            colors = ButtonDefaults.buttonColors(containerColor = MissionEmeraldSuccess),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("mark_all_present_button")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("All Present", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Summary Stats Ribbon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AttendanceSummaryPill(
                            label = "Present",
                            count = "$presentCount",
                            color = MissionEmeraldSuccess,
                            modifier = Modifier.weight(1f)
                        )
                        AttendanceSummaryPill(
                            label = "Absent",
                            count = "$absentCount",
                            color = MissionRoseError,
                            modifier = Modifier.weight(1f)
                        )
                        AttendanceSummaryPill(
                            label = "Late",
                            count = "$lateCount",
                            color = MissionGoldAccent,
                            modifier = Modifier.weight(1f)
                        )
                        AttendanceSummaryPill(
                            label = "Rate",
                            count = "$attendancePct%",
                            color = MissionBluePrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Roster Students ($totalStudents)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(students, key = { it.id.ifEmpty { it.enrollmentNo } }) { student ->
            val currentStatus = todayMap[student.fullName] ?: "UNMARKED"
            StudentAttendanceRow(
                student = student,
                currentStatus = currentStatus,
                onStatusSelected = { status ->
                    viewModel.markAttendance(
                        AttendanceRecord(
                            studentName = student.fullName,
                            date = todayDate,
                            gradeClass = student.gradeClass,
                            batch = student.batch,
                            status = status
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun AttendanceSummaryPill(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
fun StudentAttendanceRow(
    student: Student,
    currentStatus: String,
    onStatusSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = student.fullName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${student.enrollmentNo} • ${student.gradeClass} (${student.batch})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (currentStatus) {
                        "PRESENT" -> MissionEmeraldSuccess.copy(alpha = 0.15f)
                        "ABSENT" -> MissionRoseError.copy(alpha = 0.15f)
                        "LATE" -> MissionGoldAccent.copy(alpha = 0.15f)
                        "EXCUSED" -> Color(0xFFDBEAFE)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = currentStatus,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when (currentStatus) {
                            "PRESENT" -> MissionEmeraldSuccess
                            "ABSENT" -> MissionRoseError
                            "LATE" -> MissionGoldAccent
                            "EXCUSED" -> MissionBluePrimary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Selector Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChoiceChip(
                    text = "Present",
                    isSelected = currentStatus == "PRESENT",
                    activeColor = MissionEmeraldSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusSelected("PRESENT") }
                )
                StatusChoiceChip(
                    text = "Absent",
                    isSelected = currentStatus == "ABSENT",
                    activeColor = MissionRoseError,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusSelected("ABSENT") }
                )
                StatusChoiceChip(
                    text = "Late",
                    isSelected = currentStatus == "LATE",
                    activeColor = MissionGoldAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusSelected("LATE") }
                )
                StatusChoiceChip(
                    text = "Leave",
                    isSelected = currentStatus == "EXCUSED",
                    activeColor = MissionBluePrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusSelected("EXCUSED") }
                )
            }
        }
    }
}

@Composable
fun StatusChoiceChip(
    text: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) activeColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = modifier.height(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

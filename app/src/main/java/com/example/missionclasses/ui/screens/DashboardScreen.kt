package com.example.missionclasses.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.missionclasses.data.model.Notice
import com.example.missionclasses.data.model.Student
import com.example.missionclasses.ui.viewmodel.MissionViewModel
import com.example.missionclasses.ui.viewmodel.UiState
import com.example.ui.theme.MissionBlueDark
import com.example.ui.theme.MissionBluePrimary
import com.example.ui.theme.MissionEmeraldSuccess
import com.example.ui.theme.MissionGoldAccent

@Composable
fun DashboardScreen(
    viewModel: MissionViewModel,
    onNavigateTab: (Int) -> Unit
) {
    val studentsState by viewModel.students.collectAsState()
    val attendanceState by viewModel.attendance.collectAsState()
    val feesState by viewModel.fees.collectAsState()
    val examsState by viewModel.exams.collectAsState()
    val noticesState by viewModel.notices.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    val students = (studentsState as? UiState.Success)?.data ?: emptyList()
    val attendance = (attendanceState as? UiState.Success)?.data ?: emptyList()
    val fees = (feesState as? UiState.Success)?.data ?: emptyList()
    val exams = (examsState as? UiState.Success)?.data ?: emptyList()
    val notices = (noticesState as? UiState.Success)?.data ?: emptyList()

    val totalStudents = students.size
    val presentCount = attendance.count { it.status == "PRESENT" }
    val totalAttendanceRecords = attendance.size.coerceAtLeast(1)
    val attendancePercentage = if (attendance.isEmpty()) 100 else ((presentCount.toDouble() / totalAttendanceRecords) * 100).toInt()

    val totalFeeTarget = fees.sumOf { it.amount }
    val totalFeeCollected = fees.sumOf { it.paidAmount }
    val feeCollectionRate = if (totalFeeTarget > 0) ((totalFeeCollected / totalFeeTarget) * 100).toInt() else 100

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Institute Hero Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(MissionBlueDark, MissionBluePrimary, Color(0xFF1E40AF))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = MissionGoldAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "MISSION CLASSES",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color.White
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MissionGoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = currentRole.replace("_", " "),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MissionGoldAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF93C5FD),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Kota Main Academic Campus • Session 2026-27",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE2E8F0)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "“Excellence in JEE, NEET & Board Coaching with Integrity”",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFFBAE6FD)
                        )
                    }
                }
            }
        }

        // Quick KPI Stats Grid (2x2)
        item {
            Text(
                text = "Key Institutional Metrics",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "Total Enrolled",
                    value = "$totalStudents",
                    subtitle = "Active Students",
                    icon = Icons.Default.Groups,
                    iconBg = Color(0xFFDBEAFE),
                    iconTint = MissionBluePrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(1) }
                )
                KpiCard(
                    title = "Attendance Rate",
                    value = "$attendancePercentage%",
                    subtitle = "$presentCount Marked Present",
                    icon = Icons.Default.CheckCircle,
                    iconBg = Color(0xFFD1FAE5),
                    iconTint = MissionEmeraldSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(2) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "Fee Collection",
                    value = "₹${(totalFeeCollected / 1000).toInt()}k",
                    subtitle = "$feeCollectionRate% of ₹${(totalFeeTarget / 1000).toInt()}k target",
                    icon = Icons.Default.MonetizationOn,
                    iconBg = Color(0xFFFEF3C7),
                    iconTint = MissionGoldAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(3) }
                )
                KpiCard(
                    title = "Top Exam Rank",
                    value = "${exams.firstOrNull()?.grade ?: "A+"}",
                    subtitle = "${exams.size} Exams Published",
                    icon = Icons.Default.TrendingUp,
                    iconBg = Color(0xFFEDE9FE),
                    iconTint = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(4) }
                )
            }
        }

        // Quick Action Shortcuts
        item {
            Text(
                text = "Administrative Quick Actions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    ActionShortcutChip(
                        label = "New Admission",
                        icon = Icons.Default.Groups,
                        onClick = { onNavigateTab(1) }
                    )
                }
                item {
                    ActionShortcutChip(
                        label = "Mark Attendance",
                        icon = Icons.Default.CheckCircle,
                        onClick = { onNavigateTab(2) }
                    )
                }
                item {
                    ActionShortcutChip(
                        label = "Collect Fees",
                        icon = Icons.Default.MonetizationOn,
                        onClick = { onNavigateTab(3) }
                    )
                }
                item {
                    ActionShortcutChip(
                        label = "LMS Homework",
                        icon = Icons.Default.MenuBook,
                        onClick = { onNavigateTab(5) }
                    )
                }
                item {
                    ActionShortcutChip(
                        label = "AI Assistant",
                        icon = Icons.Default.AutoAwesome,
                        onClick = { onNavigateTab(9) }
                    )
                }
            }
        }

        // Recent Official Notices & Circulars
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Latest Institute Circulars",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MissionBluePrimary,
                    modifier = Modifier.clickable { onNavigateTab(6) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (notices.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "No circulars published yet. Tap 'View All' to create a notice.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    notices.take(3).forEach { notice ->
                        NoticeSummaryCard(notice = notice, onClick = { onNavigateTab(6) })
                    }
                }
            }
        }

        // Today's Batch Routine & Hall Schedule
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
                        Text(
                            text = "Today's Class Schedule",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Live Batches",
                            style = MaterialTheme.typography.labelSmall,
                            color = MissionEmeraldSuccess
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    BatchScheduleRow(time = "08:30 AM", batch = "Class 12th JEE Adv", subject = "Physics (Wave Optics)", room = "Hall A-1")
                    Spacer(modifier = Modifier.height(8.dp))
                    BatchScheduleRow(time = "10:45 AM", batch = "Class 12th NEET", subject = "Biology (Human Physiology)", room = "Hall B-3")
                    Spacer(modifier = Modifier.height(8.dp))
                    BatchScheduleRow(time = "02:00 PM", batch = "Class 10th Champions", subject = "Mathematics (Trigonometry)", room = "Hall C-2")
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun ActionShortcutChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun NoticeSummaryCard(
    notice: Notice,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (notice.category) {
                            "URGENT" -> Color(0xFFFEE2E2)
                            "EXAM" -> Color(0xFFEDE9FE)
                            else -> Color(0xFFDBEAFE)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = when (notice.category) {
                        "URGENT" -> Color(0xFFDC2626)
                        "EXAM" -> Color(0xFF7C3AED)
                        else -> MissionBluePrimary
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notice.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = notice.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun BatchScheduleRow(
    time: String,
    batch: String,
    subject: String,
    room: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$batch • $room",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MissionBluePrimary
            )
            Text(
                text = subject,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = time,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

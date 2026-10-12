package com.example.missionclasses.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.missionclasses.ui.viewmodel.MissionViewModel
import com.example.ui.theme.MissionBlueDark
import com.example.ui.theme.MissionBluePrimary
import com.example.ui.theme.MissionGoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: MissionViewModel,
    onSignOut: () -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val actionStatus by viewModel.actionStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showRoleMenu by remember { mutableStateOf(false) }

    val roles = listOf("SUPER_ADMIN", "ADMIN", "TEACHER", "STUDENT", "PARENT", "ACCOUNTANT")

    val allTabs = listOf(
        "Dashboard" to Icons.Default.Dashboard,
        "Students" to Icons.Default.Groups,
        "Attendance" to Icons.Default.CheckCircle,
        "Fees" to Icons.Default.MonetizationOn,
        "Exams" to Icons.Default.School,
        "Homework" to Icons.Default.Assignment,
        "Notices" to Icons.Default.Campaign,
        "Syllabus" to Icons.Default.MenuBook,
        "Leaves" to Icons.Default.EventBusy,
        "AI Assistant" to Icons.Default.AutoAwesome
    )

    LaunchedEffect(actionStatus) {
        actionStatus?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearActionStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MissionGoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MissionBlueDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MISSION CLASSES",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "Kota Central Campus",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFBAE6FD)
                            )
                        }
                    }
                },
                actions = {
                    // Role Switcher
                    Box {
                        Surface(
                            onClick = { showRoleMenu = true },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E40AF),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentRole.replace("_", " "),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MissionGoldAccent
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            roles.forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = role.replace("_", " "),
                                            fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        viewModel.setRole(role)
                                        showRoleMenu = false
                                    }
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier.testTag("sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MissionBlueDark,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            // Material 3 Navigation Bar
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // Show 5 primary anchors: Dashboard, Students, Attendance, Fees, AI
                val bottomIndices = listOf(0, 1, 2, 3, 9)
                bottomIndices.forEach { idx ->
                    val (label, icon) = allTabs[idx]
                    val isSelected = selectedTab == idx
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(idx) },
                        icon = { Icon(imageVector = icon, contentDescription = label) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MissionBluePrimary,
                            selectedTextColor = MissionBluePrimary,
                            indicatorColor = Color(0xFFDBEAFE),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Secondary Horizontal Module Bar to access all 10 modules easily
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(allTabs) { index, (label, icon) ->
                        val isSelected = selectedTab == index
                        Surface(
                            onClick = { viewModel.setTab(index) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MissionBluePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Screen Content based on selectedTab
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> DashboardScreen(viewModel = viewModel, onNavigateTab = { viewModel.setTab(it) })
                    1 -> StudentsScreen(viewModel = viewModel)
                    2 -> AttendanceScreen(viewModel = viewModel)
                    3 -> FeesScreen(viewModel = viewModel)
                    4 -> ExamsScreen(viewModel = viewModel)
                    5 -> AssignmentsScreen(viewModel = viewModel)
                    6 -> NoticesScreen(viewModel = viewModel)
                    7 -> ResourcesScreen(viewModel = viewModel)
                    8 -> LeavesScreen(viewModel = viewModel)
                    9 -> AiAssistantScreen(viewModel = viewModel)
                }
            }
        }
    }
}

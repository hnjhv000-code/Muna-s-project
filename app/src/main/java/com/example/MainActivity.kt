package com.example

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.ui.screens.CustomAudioTrack
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppDatabase
import com.example.data.AchievementRepository
import com.example.ui.components.LanguageGlobeDialog
import com.example.ui.screens.AchievementsLandScreen
import com.example.ui.screens.ActiveSessionScreen
import com.example.ui.screens.AppScannerScreen
import com.example.ui.screens.FocusTimerScreen
import com.example.ui.screens.GrowthOption
import com.example.ui.screens.growthOptions
import com.example.ui.theme.ElMonaTheme
import com.example.util.AmbientTrack
import com.example.util.FocusAudioManager
import com.example.util.LanguageManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AchievementRepository(database.achievementDao())

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            ElMonaTheme(darkTheme = isDarkTheme) {
                MainAppStructure(
                    repository = repository,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppStructure(
    repository: AchievementRepository,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(1) } // 0: Apps, 1: Focus, 2: Land
    var showLanguageDialog by remember { mutableStateOf(false) }

    // App Scanner & Locking State
    var selectedApps by remember { mutableStateOf(setOf<String>()) }
    var lockEntirePhone by remember { mutableStateOf(false) }

    // Focus Timer Config State
    var durationHours by remember { mutableIntStateOf(0) }
    var durationMinutes by remember { mutableIntStateOf(10) }
    var selectedOption by remember { mutableStateOf(growthOptions[0]) } // Blessed Tree
    var selectedTrack by remember { mutableStateOf(AmbientTrack.RAIN) }
    var customAudioUri by remember { mutableStateOf<Uri?>(null) }
    var customAudioName by remember { mutableStateOf<String?>(null) }
    var customAudioPlaylist by remember { mutableStateOf<List<CustomAudioTrack>>(emptyList()) }
    var isStrictMode by remember { mutableStateOf(false) }

    // Active Timer Session State
    var isSessionActive by remember { mutableStateOf(false) }
    var totalSeconds by remember { mutableLongStateOf(600L) }
    var remainingSeconds by remember { mutableLongStateOf(600L) }
    var isCollapsing by remember { mutableStateOf(false) }

    // Block Back Button navigation during active session if Strict Mode is ON
    BackHandler(enabled = isSessionActive && isStrictMode) {
        Toast.makeText(
            context,
            LanguageManager.getString("strict_mode_active_warning"),
            Toast.LENGTH_SHORT
        ).show()
    }

    // Achievements DB Flow
    val achievements by repository.allAchievements.collectAsState(initial = emptyList())

    // Active Timer Loop Coroutine
    LaunchedEffect(isSessionActive, remainingSeconds) {
        if (isSessionActive && remainingSeconds > 0 && !isCollapsing) {
            delay(1000L)
            remainingSeconds -= 1
            if (remainingSeconds <= 0L) {
                // Session Completed Successfully!
                FocusAudioManager.stopTrack()
                FocusAudioManager.playSuccessSound()
                isSessionActive = false

                val totalMins = (totalSeconds / 60).toInt().coerceAtLeast(1)
                val title = LanguageManager.getString(selectedOption.titleKey)

                repository.insertAchievement(
                    type = selectedOption.type,
                    subType = selectedOption.subType,
                    title = title,
                    durationMinutes = totalMins
                )

                Toast.makeText(
                    context,
                    LanguageManager.getString("congrats"),
                    Toast.LENGTH_LONG
                ).show()

                selectedTab = 2 // Auto-navigate to Achievements Land!
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            FocusAudioManager.stopTrack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = LanguageManager.getString("app_name"),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("app_title")
                        )
                    }
                },
                navigationIcon = {
                    // Language Globe Selector
                    Surface(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showLanguageDialog = true }
                            .testTag("language_globe_button"),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = LanguageManager.currentLanguage.code,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                actions = {
                    // Theme Switcher (Sun / Crescent Moon)
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.NightsStay else Icons.Default.WbSunny,
                            contentDescription = "Theme Toggle",
                            tint = if (isDarkTheme) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Tab 0: Apps Scanner
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        if (selectedApps.isNotEmpty() || lockEntirePhone) {
                            BadgedBox(badge = {
                                Badge { Text(if (lockEntirePhone) "🔒" else "${selectedApps.size}") }
                            }) {
                                Icon(Icons.Default.Apps, contentDescription = "Apps")
                            }
                        } else {
                            Icon(Icons.Default.Apps, contentDescription = "Apps")
                        }
                    },
                    label = { Text(LanguageManager.getString("tab_apps")) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_apps_button")
                )

                // Tab 1: Focus Timer
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(Icons.Default.HourglassTop, contentDescription = "Focus")
                    },
                    label = { Text(LanguageManager.getString("tab_focus")) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_focus_button")
                )

                // Tab 2: Achievements Land
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        if (achievements.isNotEmpty()) {
                            BadgedBox(badge = {
                                Badge { Text("${achievements.size}") }
                            }) {
                                Icon(Icons.Default.Landscape, contentDescription = "Land")
                            }
                        } else {
                            Icon(Icons.Default.Landscape, contentDescription = "Land")
                        }
                    },
                    label = { Text(LanguageManager.getString("tab_land")) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_land_button")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> {
                    AppScannerScreen(
                        selectedApps = selectedApps,
                        lockEntirePhone = lockEntirePhone,
                        onAppSelectionChanged = { selectedApps = it },
                        onLockEntirePhoneChanged = { lockEntirePhone = it }
                    )
                }
                1 -> {
                    if (isSessionActive) {
                        ActiveSessionScreen(
                            elementType = selectedOption.type,
                            elementSubType = selectedOption.subType,
                            remainingSeconds = remainingSeconds,
                            totalSeconds = totalSeconds,
                            isCollapsing = isCollapsing,
                            strictMode = isStrictMode,
                            onStopSession = {
                                if (isStrictMode) {
                                    Toast.makeText(
                                        context,
                                        LanguageManager.getString("strict_mode_active_warning"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    scope.launch {
                                        isCollapsing = true
                                        FocusAudioManager.playErrorSound()
                                        delay(1200L) // Allow crumble animation
                                        FocusAudioManager.stopTrack()
                                        isSessionActive = false
                                        isCollapsing = false
                                    }
                                }
                            }
                        )
                    } else {
                        FocusTimerScreen(
                            durationHours = durationHours,
                            durationMinutes = durationMinutes,
                            selectedOption = selectedOption,
                            selectedTrack = selectedTrack,
                            customAudioPlaylist = customAudioPlaylist,
                            customAudioUri = customAudioUri,
                            customAudioName = customAudioName,
                            strictMode = isStrictMode,
                            onDurationHoursChanged = { durationHours = it },
                            onDurationMinutesChanged = { durationMinutes = it },
                            onOptionSelected = { selectedOption = it },
                            onTrackSelected = { selectedTrack = it },
                            onCustomAudioPlaylistUpdated = { customAudioPlaylist = it },
                            onCustomAudioPicked = { uri, name ->
                                customAudioUri = uri
                                customAudioName = name
                            },
                            onStrictModeChanged = { isStrictMode = it },
                            onStartFocus = {
                                val secs = (durationHours * 3600L) + (durationMinutes * 60L)
                                if (secs > 0L) {
                                    totalSeconds = secs
                                    remainingSeconds = secs
                                    isSessionActive = true
                                    isCollapsing = false
                                    FocusAudioManager.startTrack(
                                        context = context,
                                        track = selectedTrack,
                                        customUri = customAudioUri,
                                        customPlaylist = customAudioPlaylist.map { it.uri }
                                    )
                                }
                            }
                        )
                    }
                }
                2 -> {
                    AchievementsLandScreen(
                        achievements = achievements,
                        isDarkTheme = isDarkTheme,
                        onClearAll = {
                            scope.launch {
                                repository.clearAll()
                            }
                        }
                    )
                }
            }
        }

        if (showLanguageDialog) {
            LanguageGlobeDialog(
                onDismiss = { showLanguageDialog = false }
            )
        }
    }
}

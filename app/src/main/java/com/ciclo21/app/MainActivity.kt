package com.ciclo21.app

import android.Manifest
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.*
import com.ciclo21.app.ui.home.HomeScreen
import com.ciclo21.app.ui.home.HomeViewModel
import com.ciclo21.app.ui.onboarding.OnboardingScreen
import com.ciclo21.app.ui.symptoms.SymptomsScreen
import com.ciclo21.app.ui.calendar.CalendarScreen
import com.ciclo21.app.ui.insights.InsightsScreen
import com.ciclo21.app.ui.settings.SettingsScreen
import com.ciclo21.app.ui.theme.*
import com.ciclo21.app.data.local.PreferenceManager
import com.ciclo21.app.ui.components.LiquidNavigationBar
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.fragment.app.FragmentActivity
import com.ciclo21.app.data.util.BiometricAuthenticator
import com.ciclo21.app.data.util.SleepTracker

class MainActivity : FragmentActivity() {
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var biometricAuthenticator: BiometricAuthenticator
    private lateinit var sleepTracker: SleepTracker

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> sleepTracker.recordScreenOff()
                Intent.ACTION_SCREEN_ON -> lifecycleScope.launch { sleepTracker.recordScreenOn() }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        var keepSplashScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }
        
        lifecycleScope.launch {
            delay(2500)
            keepSplashScreen = false
        }

        enableEdgeToEdge()
        preferenceManager = PreferenceManager(this)
        biometricAuthenticator = BiometricAuthenticator(this)
        sleepTracker = SleepTracker(this)

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenReceiver, filter)

        checkPermissions()

        setContent {
            val homeViewModel: HomeViewModel = viewModel()
            val uiState by homeViewModel.uiState.collectAsState()
            
            var isOnboardingCompleted by remember { 
                mutableStateOf(preferenceManager.isOnboardingCompleted()) 
            }
            
            var isAuthenticated by remember { 
                mutableStateOf(!preferenceManager.isBiometricLockEnabled()) 
            }

            Ciclo21Theme(currentPhase = uiState.currentPhase) {
                if (!isOnboardingCompleted) {
                    OnboardingScreen(onFinish = { cycle, period, date, water, biometric, pill -> 
                        preferenceManager.saveCycleData(cycle, period, date)
                        preferenceManager.setWaterReminder(water)
                        preferenceManager.setBiometricLock(biometric)
                        preferenceManager.savePillSettings(pill, 6, 0, 21)
                        preferenceManager.setOnboardingCompleted(true)
                        isOnboardingCompleted = true 
                        isAuthenticated = true
                    })
                } else if (!isAuthenticated) {
                    AuthScreen {
                        biometricAuthenticator.authenticate(this, 
                            onSuccess = { isAuthenticated = true },
                            onError = { /* Handle error */ }
                        )
                    }
                } else {
                    MainNavigation(homeViewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenReceiver)
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }
}

@Composable
fun AuthScreen(onRetry: () -> Unit) {
    LaunchedEffect(Unit) { onRetry() }
    
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Lock, null, modifier = Modifier.size(64.dp), tint = Lutea)
            Spacer(modifier = Modifier.height(24.dp))
            Text("App Bloqueado", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Use sua biometria para acessar seus dados", color = Color.Gray)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Lutea)) {
                Text("Desbloquear")
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Hoje", Icons.Default.Home)
    object Calendar : Screen("calendar", "Ciclo", Icons.AutoMirrored.Filled.List)
    object Insights : Screen("insights", "Análise", Icons.Default.Favorite)
    object Settings : Screen("settings", "Ajustes", Icons.Default.Settings)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainNavigation(homeViewModel: HomeViewModel) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Calendar, Screen.Insights, Screen.Settings)
    val pagerState = rememberPagerState(pageCount = { screens.size })
    val coroutineScope = rememberCoroutineScope()
    val currentRoute = screens[pagerState.currentPage].route

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        NavHost(navController, startDestination = "main_pager") {
            composable("main_pager") {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    when (page) {
                        0 -> HomeScreen(onNavigateToSymptoms = { navController.navigate("symptoms") }, viewModel = homeViewModel)
                        1 -> CalendarScreen()
                        2 -> InsightsScreen()
                        3 -> SettingsScreen()
                    }
                }
            }
            composable("symptoms") {
                SymptomsScreen(onNavigateBack = { 
                    navController.popBackStack()
                    homeViewModel.refreshData()
                })
            }
        }

        // Navegação Flutuante
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp) // Flutua acima da borda inferior
                .navigationBarsPadding()
        ) {
            LiquidNavigationBar(
                screens = screens,
                selectedRoute = currentRoute,
                onItemSelected = { screen ->
                    val targetPage = screens.indexOf(screen)
                    coroutineScope.launch { pagerState.animateScrollToPage(targetPage) }
                },
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

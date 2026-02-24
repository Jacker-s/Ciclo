package com.ciclo21.app.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ciclo21.app.domain.model.CyclePhase
import com.ciclo21.app.ui.components.AnimatedSuggestionsBanner
import com.ciclo21.app.ui.theme.*
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSymptoms: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var showSheet by remember { mutableStateOf(false) }
    var sheetContent by remember { mutableStateOf<PopUpData?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Elementos decorativos de fundo
        Box(modifier = Modifier.size(300.dp).offset(x = (-150).dp, y = (-100).dp).clip(CircleShape).background(uiState.currentPhase.color.copy(alpha = 0.05f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Cabeçalho de Saudação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Olá, bem-vinda",
                        style = MaterialTheme.typography.bodyLarge,
                        color = iOSSecondaryLabel
                    )
                    Text(
                        text = "Hoje é ${LocalDate.now().dayOfMonth} de ${LocalDate.now().month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale("pt", "BR"))}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = uiState.currentPhase.color.copy(alpha = 0.1f)
                ) {
                    Icon(Icons.Default.Person, null, tint = uiState.currentPhase.color, modifier = Modifier.padding(12.dp))
                }
            }

            // Dashboard Principal Circular
            MainStatusCircle(uiState) {
                sheetContent = uiState.dailyPopUpData
                showSheet = true
            }

            // Banner Dinâmico de Sugestões
            AnimatedSuggestionsBanner(
                phase = if (uiState.isPregnancyMode) CyclePhase.FOLICULAR else uiState.currentPhase,
                onSuggestionClick = { data ->
                    sheetContent = data
                    showSheet = true
                }
            )

            // Cards de Métricas Rápidas
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Água",
                    value = "${uiState.waterIntake}ml",
                    icon = Icons.Default.Info,
                    color = iOSBlue
                )
                QuickMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Sono",
                    value = "${uiState.waterIntake / 100}h", // Dummy value
                    icon = Icons.Default.Info,
                    color = iOSPurple
                )
            }

            // Card de Anticoncepcional
            ContraceptiveCard(uiState)

            // Botão de Registro Flutuante (estilizado como card)
            Surface(
                onClick = onNavigateToSymptoms,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(20.dp),
                color = uiState.currentPhase.color,
                contentColor = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (uiState.isPregnancyMode) "Log da Gestação" else "Registrar hoje",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(Icons.Default.Add, null)
                }
            }

            Spacer(modifier = Modifier.height(140.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }

    if (showSheet && sheetContent != null) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState) {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 60.dp)) {
                Text(text = sheetContent!!.title.uppercase(), color = sheetContent!!.color, style = MaterialTheme.typography.labelLarge, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = sheetContent!!.description, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { showSheet = false }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = iOSBlue), shape = RoundedCornerShape(14.dp)) {
                    Text("Entendido")
                }
            }
        }
    }
}

@Composable
fun MainStatusCircle(uiState: HomeUiState, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(260.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { uiState.currentDay.toFloat() / uiState.cycleLength.toFloat() },
                modifier = Modifier.fillMaxSize().padding(12.dp),
                color = uiState.currentPhase.color,
                strokeWidth = 12.dp,
                trackColor = uiState.currentPhase.color.copy(alpha = 0.1f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Dia ${uiState.currentDay}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = uiState.currentPhase.color
                )
                Text(
                    text = uiState.currentPhase.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = iOSSecondaryLabel
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(uiState.currentPhase.color.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = uiState.pregnancyChance,
                        style = MaterialTheme.typography.labelSmall,
                        color = uiState.currentPhase.color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuickMetricCard(modifier: Modifier, title: String, value: String, icon: ImageVector, color: Color) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelSmall, color = iOSSecondaryLabel)
            }
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ContraceptiveCard(uiState: HomeUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(iOSGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = iOSGreen)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Anticoncepcional", style = MaterialTheme.typography.labelSmall, color = iOSSecondaryLabel)
                Text("Ciclo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = uiState.pillTaken,
                onCheckedChange = { },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = iOSGreen)
            )
        }
    }
}

package com.ciclo21.app.ui.calendar

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ciclo21.app.domain.manager.CycleManager
import com.ciclo21.app.ui.theme.*
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var displayedMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val cycleManager = remember { CycleManager() }
    
    var showTipDialog by remember { mutableStateOf(false) }
    var currentTip by remember { mutableStateOf<CycleTip?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Elementos decorativos de fundo
        Box(modifier = Modifier.size(400.dp).offset(x = 150.dp, y = (-100).dp).clip(CircleShape).background(Folicular.copy(alpha = 0.03f)))
        
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 100.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    "Calendário",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = iOSLabel
                )
            }

            item {
                ModernCalendarHeader(
                    month = displayedMonth,
                    onPrevious = { displayedMonth = displayedMonth.minusMonths(1) },
                    onNext = { displayedMonth = displayedMonth.plusMonths(1) }
                )
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DaysOfWeekHeader()
                        
                        val daysInMonth = displayedMonth.lengthOfMonth()
                        val firstDayOfWeek = displayedMonth.dayOfWeek.value % 7
                        
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(7),
                            userScrollEnabled = false,
                            modifier = Modifier.height(320.dp)
                        ) {
                            items(firstDayOfWeek) { Spacer(Modifier.fillMaxWidth()) }
                            items(daysInMonth) { index ->
                                val date = displayedMonth.withDayOfMonth(index + 1)
                                val status = cycleManager.getStatusForDate(date, uiState.lastPeriodStart, uiState.cycleLength, uiState.periodLength)
                                
                                ArtisticDayCell(
                                    day = index + 1,
                                    isSelected = date == selectedDate,
                                    isToday = date == LocalDate.now(),
                                    status = status
                                ) {
                                    selectedDate = date
                                    currentTip = getDetailedTip(status, date)
                                    showTipDialog = true
                                }
                            }
                        }
                    }
                }
            }

            item {
                PhaseLegend()
            }
        }

        // Pop-up solto na tela - Versão Premium Glass
        if (showTipDialog && currentTip != null) {
            FloatingTipPopup(
                tip = currentTip!!,
                onDismiss = { showTipDialog = false }
            )
        }
    }
}

@Composable
fun FloatingTipPopup(tip: CycleTip, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val glowScale by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowScale"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            // Glow effect behind the card
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .graphicsLayer(scaleX = glowScale, scaleY = glowScale)
                    .shadow(elevation = 40.dp, shape = CircleShape, spotColor = tip.color, ambientColor = tip.color)
            )

            Surface(
                modifier = Modifier
                    .width(320.dp)
                    .wrapContentHeight()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                    .clickable(enabled = false) { },
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(tip.color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(tip.icon, null, tint = tip.color, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null, tint = iOSSecondaryLabel)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = tip.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = iOSLabel,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = tip.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = iOSSecondaryLabel,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(tip.color.copy(alpha = 0.05f))
                            .padding(20.dp)
                    ) {
                        Text(
                            text = tip.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = iOSLabel,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = tip.color)
                    ) {
                        Text("Entendido", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ModernCalendarHeader(month: LocalDate, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = month.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = iOSLabel
            )
            Text(
                text = month.year.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = iOSSecondaryLabel
            )
        }
        Row(
            modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            IconButton(onClick = onPrevious) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = iOSBlue) }
            Box(modifier = Modifier.width(1.dp).height(24.dp).align(Alignment.CenterVertically).background(iOSSeparator))
            IconButton(onClick = onNext) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = iOSBlue) }
        }
    }
}

@Composable
fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB").forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = iOSSecondaryLabel,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ArtisticDayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    status: com.ciclo21.app.domain.manager.CycleStatus,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (status.isOvulation) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> iOSBlue
                    isToday -> iOSBlue.copy(alpha = 0.1f)
                    else -> Color.Transparent
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Indicadores de fase
        if (!isSelected) {
            if (status.isPeriod) {
                Box(modifier = Modifier.fillMaxSize().padding(4.dp).clip(CircleShape).background(Menstruacao.copy(alpha = 0.15f)))
            }
            if (status.isFertile) {
                Box(modifier = Modifier.fillMaxSize().padding(4.dp).clip(CircleShape).background(Ovulacao.copy(alpha = 0.15f)))
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = when {
                    isSelected -> Color.White
                    isToday -> iOSBlue
                    status.isPeriod -> Menstruacao
                    status.isFertile -> Ovulacao
                    else -> iOSLabel
                },
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = if (status.isOvulation && !isSelected) Modifier.graphicsLayer(scaleX = scale, scaleY = scale) else Modifier
            )
            if (status.isOvulation) {
                Box(
                    modifier = Modifier.size(4.dp).clip(CircleShape)
                        .background(if (isSelected) Color.White else Ovulacao)
                )
            }
        }
    }
}

@Composable
fun PhaseLegend() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(Menstruacao, "Fluxo")
            LegendItem(Ovulacao, "Fértil")
            LegendItem(iOSBlue, "Hoje")
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = iOSSecondaryLabel)
    }
}

data class CycleTip(
    val title: String,
    val date: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

private fun getDetailedTip(status: com.ciclo21.app.domain.manager.CycleStatus, date: LocalDate): CycleTip {
    val dateStr = date.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", Locale("pt", "BR")))
    return when {
        status.isOvulation -> CycleTip(
            "Pico de Fertilidade",
            dateStr,
            "Hoje é o seu dia de ovulação! Suas chances de concepção são máximas. Você pode sentir mais energia, libido elevada e uma leve pontada abdominal.",
            Icons.Default.Info,
            Ovulacao
        )
        status.isPeriod -> CycleTip(
            "Fase Menstrual",
            dateStr,
            "Seu corpo está em um processo de renovação. É normal sentir-se mais cansada. Tente manter-se aquecida, beba chás relaxantes e respeite o tempo de descanso.",
            Icons.Default.Info,
            Menstruacao
        )
        status.isFertile -> CycleTip(
            "Janela Fértil",
            dateStr,
            "Você está no seu período fértil. O estrogênio está subindo, o que melhora o humor e a pele. Ótimo momento para atividades sociais e autocuidado.",
            Icons.Default.Info,
            Ovulacao
        )
        else -> CycleTip(
            "Equilíbrio Hormonal",
            dateStr,
            "Você está em uma fase estável do ciclo. Aproveite para focar em projetos que exigem concentração e manter sua rotina de bem-estar constante.",
            Icons.Default.Info,
            iOSBlue
        )
    }
}

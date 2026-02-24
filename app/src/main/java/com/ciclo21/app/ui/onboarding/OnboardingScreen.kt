package com.ciclo21.app.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciclo21.app.ui.components.CicloLogo
import com.ciclo21.app.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class PillOption(val name: String, val description: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onFinish: (Int, Int, LocalDate, Boolean, Boolean, String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var cycleLength by remember { mutableFloatStateOf(28f) }
    var periodLength by remember { mutableFloatStateOf(5f) }
    var lastPeriodDate by remember { mutableStateOf(LocalDate.now()) }
    var waterReminder by remember { mutableStateOf(true) }
    var biometricLock by remember { mutableStateOf(false) }
    var selectedPill by remember { mutableStateOf("Ciclo 21") }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    var showDatePicker by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = step.toFloat() / 5f,
        animationSpec = tween(500),
        label = "progress"
    )

    val pills = listOf(
        PillOption("Ciclo 21", "Combinada clássica", iOSPurple),
        PillOption("Iumi", "Baixa dosagem", iOSBlue),
        PillOption("Selene", "Controle de acne", iOSGreen),
        PillOption("Yaz", "Sintomas de TPM", iOSPink),
        PillOption("Outro", "Outro método", Color.Gray)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        // Círculos de fundo decorativos
        Box(modifier = Modifier.size(300.dp).offset(x = (-100).dp, y = (-100).dp).clip(CircleShape).background(Lutea.copy(alpha = 0.05f)))
        Box(modifier = Modifier.size(200.dp).align(Alignment.BottomEnd).offset(x = 50.dp, y = 50.dp).clip(CircleShape).background(Folicular.copy(alpha = 0.05f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                CicloLogo(size = 32.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Ciclo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = iOSLabel)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Barra de Progresso
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = Lutea,
                    trackColor = Lutea.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Passo $step de 5",
                    style = MaterialTheme.typography.labelSmall,
                    color = iOSSecondaryLabel,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Conteúdo Animado
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                        } using SizeTransform(clip = false)
                    },
                    label = "stepContent"
                ) { targetStep ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        when (targetStep) {
                            1 -> StepCard(
                                title = "Duração do Ciclo",
                                description = "O tempo médio entre suas menstruações.",
                                icon = Icons.Default.DateRange,
                                color = Lutea
                            ) {
                                ValuePicker(cycleLength, 21f..45f, "dias", Lutea) { cycleLength = it }
                            }
                            2 -> StepCard(
                                title = "Duração do Fluxo",
                                description = "Quantos dias sua menstruação costuma durar?",
                                icon = Icons.Default.DateRange,
                                color = Menstruacao
                            ) {
                                ValuePicker(periodLength, 2f..12f, "dias", Menstruacao) { periodLength = it }
                            }
                            3 -> StepCard(
                                title = "Última Menstruação",
                                description = "Selecione o início do seu último período.",
                                icon = Icons.Default.DateRange,
                                color = iOSBlue
                            ) {
                                DateDisplay(lastPeriodDate) { showDatePicker = true }
                            }
                            4 -> StepCard(
                                title = "Seu Anticoncepcional",
                                description = "Selecione o método que você utiliza.",
                                icon = Icons.Default.Info,
                                color = iOSGreen
                            ) {
                                PillPicker(pills, selectedPill) { selectedPill = it }
                            }
                            5 -> StepCard(
                                title = "Preferências",
                                description = "Como o Ciclo deve cuidar de você.",
                                icon = Icons.Default.Notifications,
                                color = iOSPurple
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    PreferenceToggle("Lembretes de Água", Icons.Default.Notifications, waterReminder) { waterReminder = it }
                                    PreferenceToggle("Bloqueio Biométrico", Icons.Default.Lock, biometricLock) { biometricLock = it }
                                }
                            }
                        }
                    }
                }
            }

            // Botões de Navegação
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { step-- },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                    ) {
                        Text("Voltar", color = iOSLabel)
                    }
                }

                Button(
                    onClick = {
                        if (step < 5) step++ 
                        else onFinish(cycleLength.toInt(), periodLength.toInt(), lastPeriodDate, waterReminder, biometricLock, selectedPill)
                    },
                    modifier = Modifier.weight(2f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (step == 5) iOSBlue else Lutea)
                ) {
                    Text(if (step < 5) "Continuar" else "Começar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    if (step < 5) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        lastPeriodDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("Confirmar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun PillPicker(options: List<PillOption>, selected: String, onSelect: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().height(280.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { pill ->
            val isSelected = selected == pill.name
            Surface(
                onClick = { onSelect(pill.name) },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) pill.color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, pill.color) else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(pill.color))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(pill.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(pill.description, style = MaterialTheme.typography.bodySmall, color = iOSSecondaryLabel)
                    }
                }
            }
        }
    }
}

@Composable
fun StepCard(title: String, description: String, icon: ImageVector, color: Color, content: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = iOSLabel)
        Text(description, style = MaterialTheme.typography.bodySmall, color = iOSSecondaryLabel, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(24.dp))
        content()
    }
}

@Composable
fun ValuePicker(value: Float, range: ClosedFloatingPointRange<Float>, unit: String, color: Color, onValueChange: (Float) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "${value.toInt()}", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black, color = color)
        Text(unit, style = MaterialTheme.typography.titleMedium, color = iOSSecondaryLabel)
        Slider(value = value, onValueChange = onValueChange, valueRange = range, colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color))
    }
}

@Composable
fun DateDisplay(date: LocalDate, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DateRange, null, tint = iOSBlue)
            Spacer(modifier = Modifier.width(12.dp))
            Text(date.format(DateTimeFormatter.ofPattern("dd 'de' MMMM", Locale("pt", "BR"))), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text("Alterar", color = iOSBlue, fontSize = 12.sp)
        }
    }
}

@Composable
fun PreferenceToggle(title: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (checked) iOSBlue.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (checked) iOSBlue else Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = iOSBlue))
        }
    }
}

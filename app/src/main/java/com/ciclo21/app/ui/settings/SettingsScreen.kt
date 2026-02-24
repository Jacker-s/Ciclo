package com.ciclo21.app.ui.settings

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciclo21.app.ui.theme.*
import com.ciclo21.app.data.local.PreferenceManager
import com.ciclo21.app.data.util.AlarmScheduler
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class PillInfo(val name: String, val description: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val preferenceManager = remember { PreferenceManager(context) }
    val alarmScheduler = remember { AlarmScheduler(context) }
    
    var showPillSelector by remember { mutableStateOf(false) }
    
    var pillTime by remember { mutableStateOf(preferenceManager.getPillTime()) }
    var selectedPill by remember { mutableStateOf(preferenceManager.getPillBrand() ?: "Nenhum") }
    var isPregnancyMode by remember { mutableStateOf(preferenceManager.isPregnancyMode()) }
    var isWaterReminder by remember { mutableStateOf(preferenceManager.isWaterReminderEnabled()) }
    var isBiometricEnabled by remember { mutableStateOf(preferenceManager.isBiometricLockEnabled()) }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val pills = listOf(
        PillInfo("Ciclo 21", "Combinada clássica, alta eficácia e custo acessível.", iOSPurple),
        PillInfo("Iumi / Iumi ES", "Baixa dosagem, ajuda a reduzir o inchaço e a retenção.", iOSBlue),
        PillInfo("Selene / Diane 35", "Foco no controle de acne, oleosidade e SOP.", iOSGreen),
        PillInfo("Yaz / Yasmin", "Fórmula moderna que minimiza sintomas de TPM.", iOSPink),
        PillInfo("Adoless", "Esquema de 24+4 dias para menor variação hormonal.", Ovulacao),
        PillInfo("Cerazette / Kelly", "Apenas progesterona, ideal para quem amamenta.", Lutea),
        PillInfo("Mesigyna", "Injetável mensal, praticidade para o dia a dia.", Menstruacao)
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Text("Ajustes", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold) }
            
            item {
                Section("MÉTODO CONTRACEPTIVO") {
                    SettingItem(icon = Icons.Default.Info, title = "Anticoncepcional", subtitle = selectedPill) { 
                        showPillSelector = true 
                    }
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = iOSSeparator)
                    SettingItem(icon = Icons.Default.Notifications, title = "Horário do Lembrete", subtitle = pillTime.format(DateTimeFormatter.ofPattern("HH:mm"))) {
                        TimePickerDialog(context, { _, h, m ->
                            val newTime = LocalTime.of(h, m)
                            pillTime = newTime
                            preferenceManager.savePillSettings(selectedPill, h, m, 21)
                            alarmScheduler.schedulePillAlarm(newTime)
                        }, pillTime.hour, pillTime.minute, true).show()
                    }
                }
            }

            item {
                Section("PREFERÊNCIAS E SAÚDE") {
                    SettingSwitchItem(
                        icon = Icons.Default.Favorite, 
                        title = "Modo Gravidez", 
                        subtitle = "Acompanhe sua gestação", 
                        checked = isPregnancyMode,
                        onCheckedChange = { 
                            isPregnancyMode = it
                            preferenceManager.setPregnancyMode(it)
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = iOSSeparator)
                    SettingSwitchItem(
                        icon = Icons.Default.Star,
                        title = "Lembrete de Água", 
                        subtitle = "Notificações para se hidratar", 
                        checked = isWaterReminder,
                        onCheckedChange = { 
                            isWaterReminder = it
                            preferenceManager.setWaterReminder(it)
                        }
                    )
                }
            }

            item {
                Section("SEGURANÇA") {
                    SettingSwitchItem(
                        icon = Icons.Default.Lock, 
                        title = "Bloqueio Biométrico", 
                        subtitle = "Proteger acesso ao app", 
                        checked = isBiometricEnabled,
                        onCheckedChange = { 
                            isBiometricEnabled = it
                            preferenceManager.setBiometricLock(it)
                        }
                    )
                }
            }

            // ESPAÇAMENTO PARA O MENU FLUTUANTE + BARRA DO ANDROID
            item {
                Spacer(modifier = Modifier.height(140.dp))
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }

    if (showPillSelector) {
        ModalBottomSheet(
            onDismissRequest = { showPillSelector = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = null
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(iOSPink, iOSPurple)
                            )
                        )
                        .padding(vertical = 32.dp, horizontal = 24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(Icons.Default.Info, null, tint = Color.White, modifier = Modifier.padding(16.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Seu Anticoncepcional",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Selecione o método que você utiliza para que possamos personalizar suas notificações e insights.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pills) { pill ->
                        val isSelected = selectedPill == pill.name
                        Surface(
                            onClick = {
                                selectedPill = pill.name
                                preferenceManager.savePillSettings(pill.name, pillTime.hour, pillTime.minute, 21)
                                showPillSelector = false
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) pill.color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, pill.color) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(pill.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(pill.color)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        pill.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) pill.color else iOSLabel
                                    )
                                    Text(
                                        pill.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = iOSSecondaryLabel
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, null, tint = pill.color, modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(text = title, style = MaterialTheme.typography.labelSmall, color = iOSSecondaryLabel, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
        Column(modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface), content = content)
    }
}

@Composable
private fun SettingItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(iOSBlue.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iOSBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = iOSSecondaryLabel, maxLines = 1)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray)
    }
}

@Composable
private fun SettingSwitchItem(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(iOSBlue.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iOSBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = iOSSecondaryLabel, maxLines = 1)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = iOSBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}

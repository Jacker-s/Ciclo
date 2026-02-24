package com.ciclo21.app.ui.symptoms

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ciclo21.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SymptomsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val symptomsList = listOf("Cólica", "Dor de cabeça", "Inchaço", "Sensibilidade nos seios", "Acne", "Cansaço", "Náusea")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Log Diário", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Como você está hoje?", style = MaterialTheme.typography.labelSmall, color = iOSSecondaryLabel)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = iOSBlue)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.saveRecord(onNavigateBack) }) {
                        Text("Salvar", color = iOSBlue, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            // Humor
            item {
                iOSSectionHeader("HUMOR")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MoodEmojiItem("Radiante", "🤩", uiState.mood == "Radiante") { viewModel.updateMood(it) }
                    MoodEmojiItem("Feliz", "😊", uiState.mood == "Feliz") { viewModel.updateMood(it) }
                    MoodEmojiItem("Neutro", "😐", uiState.mood == "Neutro") { viewModel.updateMood(it) }
                    MoodEmojiItem("Triste", "😔", uiState.mood == "Triste") { viewModel.updateMood(it) }
                    MoodEmojiItem("Irritada", "😠", uiState.mood == "Irritada") { viewModel.updateMood(it) }
                }
            }

            // Fluxo
            item {
                iOSSectionHeader("MENSTRUAÇÃO")
                iOSGroupedContainer {
                    iOSSelectionRow("Fluxo", listOf("Nenhum", "Leve", "Médio", "Forte"), uiState.flowIntensity) { viewModel.updateFlow(it) }
                }
            }

            // Sintomas
            item {
                iOSSectionHeader("SINTOMAS")
                OptInFlowRow {
                    symptomsList.forEach { symptom ->
                        val isSelected = uiState.symptoms.contains(symptom)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.toggleSymptom(symptom) },
                            label = { Text(symptom) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = iOSBlue.copy(alpha = 0.1f),
                                selectedLabelColor = iOSBlue
                            )
                        )
                    }
                }
            }

            // Hábitos
            item {
                iOSSectionHeader("SAÚDE & HÁBITOS")
                iOSGroupedContainer {
                    iOSCounterRow("Hidratação", "${uiState.waterIntake * 250}ml") { viewModel.updateWater(uiState.waterIntake + 1) }
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = iOSSeparator)
                    iOSInputRow("Horas de Sono", "${uiState.sleepHours}h (Auto)", Icons.Default.Info)
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = iOSSeparator)
                    iOSInputRow("Peso", "${uiState.weight} kg", Icons.Default.AccountCircle)
                }
            }

            // Vida Sexual
            item {
                iOSSectionHeader("VIDA SEXUAL")
                iOSGroupedContainer {
                    iOSSelectionRow("Desejo", listOf("Baixo", "Normal", "Alto"), uiState.sexualDrive) { viewModel.updateLibido(it) }
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = iOSSeparator)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Relação Sexual", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = uiState.sexualActivity,
                            onCheckedChange = { viewModel.updateSex(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = iOSPink)
                        )
                    }
                }
            }

            // Notas
            item {
                iOSSectionHeader("NOTAS")
                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = { viewModel.updateNote(it) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    placeholder = { Text("Escreva algo sobre o seu dia...") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        focusedBorderColor = iOSBlue
                    )
                )
            }
        }
    }
}

@Composable
fun MoodEmojiItem(label: String, emoji: String, isSelected: Boolean, onClick: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick(label) }) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isSelected) iOSBlue.copy(alpha = 0.1f) else iOSSecondaryBackground)
                .border(if (isSelected) 2.dp else 0.dp, if (isSelected) iOSBlue else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (isSelected) iOSBlue else iOSSecondaryLabel)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OptInFlowRow(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}

@Composable
fun iOSSectionHeader(title: String) {
    Text(text = title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = iOSSecondaryLabel, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
}

@Composable
fun iOSGroupedContainer(content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
        Column(content = content)
    }
}

@Composable
fun iOSSelectionRow(title: String, options: List<String>, selected: String?, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = option == selected
                Surface(
                    modifier = Modifier.weight(1f).height(40.dp).clickable { onSelect(option) },
                    color = if (isSelected) iOSBlue else iOSSecondaryBackground,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = option, textAlign = TextAlign.Center, color = if (isSelected) Color.White else iOSLabel, style = MaterialTheme.typography.labelMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}

@Composable
fun iOSCounterRow(title: String, value: String, onAdd: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(iOSBlue.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Info, null, tint = iOSBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = iOSBlue)
        Spacer(modifier = Modifier.width(12.dp))
        Surface(onClick = onAdd, shape = CircleShape, color = iOSBlue.copy(alpha = 0.1f)) {
            Icon(Icons.Default.Add, null, tint = iOSBlue, modifier = Modifier.padding(4.dp))
        }
    }
}

@Composable
fun iOSInputRow(title: String, value: String, icon: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(iOSPurple.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iOSPurple, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = iOSSecondaryLabel)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = iOSTertiaryLabel, modifier = Modifier.size(18.dp))
    }
}

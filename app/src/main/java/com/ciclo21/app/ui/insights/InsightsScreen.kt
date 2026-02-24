package com.ciclo21.app.ui.insights

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciclo21.app.ui.theme.*
import com.ciclo21.app.ui.theme.PopUpData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen() {
    var showSheet by remember { mutableStateOf(false) }
    var sheetContent by remember { mutableStateOf<PopUpData?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Elementos decorativos de fundo
        Box(modifier = Modifier.size(300.dp).offset(x = (-150).dp, y = 100.dp).clip(CircleShape).background(iOSPink.copy(alpha = 0.03f)))
        Box(modifier = Modifier.size(200.dp).align(Alignment.BottomEnd).offset(x = 100.dp, y = (-50).dp).clip(CircleShape).background(iOSBlue.copy(alpha = 0.03f)))

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 100.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Tendências",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = iOSLabel
                )
            }

            // Gráfico Hormonal Dinâmico
            item {
                HormonalTimelineCard(onClick = {
                    sheetContent = PopUpData(
                        title = "Equilíbrio Hormonal",
                        description = "As variações de Estrogênio e Progesterona definem como você se sente física e emocionalmente.",
                        tips = listOf(
                            "O Estrogênio sobe na fase folicular, aumentando sua energia.",
                            "A Progesterona domina a fase lútea, podendo causar cansaço.",
                            "Entender essas ondas ajuda a planejar melhor seu mês."
                        ),
                        color = iOSPink
                    )
                    showSheet = true
                })
            }

            // Análise de Métricas de Saúde
            item {
                Text("RESUMO DE SAÚDE", style = MaterialTheme.typography.labelSmall, color = iOSSecondaryLabel, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricRingCard(
                        modifier = Modifier.weight(1f),
                        title = "Sono",
                        value = "7.5h",
                        progress = 0.8f,
                        icon = Icons.Default.Info,
                        color = iOSBlue,
                        onClick = {
                            sheetContent = PopUpData(
                                title = "Análise de Sono",
                                description = "Um sono de qualidade ajuda a regular o cortisol e reduz os sintomas da TPM.",
                                tips = listOf(
                                    "Evite telas 1 hora antes de dormir.",
                                    "Mantenha o quarto escuro e fresco.",
                                    "Chás de camomila ou melissa são ótimos na fase lútea."
                                ),
                                color = iOSBlue
                            )
                            showSheet = true
                        }
                    )
                    MetricRingCard(
                        modifier = Modifier.weight(1f),
                        title = "Água",
                        value = "1.8L",
                        progress = 0.6f,
                        icon = Icons.Default.Star,
                        color = iOSBlue,
                        onClick = {
                            sheetContent = PopUpData(
                                title = "Hidratação",
                                description = "Beber água reduz o inchaço e melhora a circulação durante a menstruação.",
                                tips = listOf(
                                    "Tente beber 2.5L de água por dia.",
                                    "Reduza o sódio para diminuir a retenção de líquidos.",
                                    "Água de coco é uma ótima aliada hoje."
                                ),
                                color = iOSBlue
                            )
                            showSheet = true
                        }
                    )
                }
            }

            // Seção de Insights de Sintomas
            item {
                Text("PADRÕES RECORRENTES", style = MaterialTheme.typography.labelSmall, color = iOSSecondaryLabel, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                iOSGroupedContainer {
                    SymptomInsightRow("Cólica", "Presente em 80% dos ciclos", Menstruacao) {
                        sheetContent = PopUpData(
                            title = "Alívio de Cólicas",
                            description = "A dismenorreia (cólica) ocorre devido à contração do útero.",
                            tips = listOf(
                                "Use bolsas de água morna no baixo ventre.",
                                "Remédios antiespasmódicos ajudam no alívio.",
                                "Pratique exercícios leves e alongamentos.",
                                "Consulte um médico antes de usar medicamentos."
                            ),
                            color = Menstruacao
                        )
                        showSheet = true
                    }
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = iOSSeparator)
                    SymptomInsightRow("Energia Alta", "Presente em 90% da fase folicular", iOSGreen) {
                        sheetContent = PopUpData(
                            title = "Pico de Bem-estar",
                            description = "Sua fase folicular é marcada pelo aumento do estrogênio.",
                            tips = listOf(
                                "Aproveite sua alta criatividade.",
                                "Sua sociabilidade está no auge.",
                                "Ótimo momento para novos projetos."
                            ),
                            color = iOSGreen
                        )
                        showSheet = true
                    }
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = iOSSeparator)
                    SymptomInsightRow("Sensibilidade", "Frequente antes do fluxo", iOSPink) {
                        sheetContent = PopUpData(
                            title = "Cuidados na TPM",
                            description = "A queda hormonal pode causar maior irritabilidade e sensibilidade.",
                            tips = listOf(
                                "Pratique o autocuidado e meditação.",
                                "Reduza cafeína e alimentos processados.",
                                "Priorize atividades que tragam relaxamento."
                            ),
                            color = iOSPink
                        )
                        showSheet = true
                    }
                }
            }

            // Card de Predição de Próximo Ciclo
            item {
                NextCycleCard()
            }

            // Dica de Saúde Randômica
            item { HealthTipCard() }
        }
    }

    if (showSheet && sheetContent != null) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(sheetContent!!.color.copy(alpha = 0.1f))
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Info, null, tint = sheetContent!!.color)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = sheetContent!!.title.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = sheetContent!!.color,
                    letterSpacing = 2.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = sheetContent!!.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text("Análise Detalhada:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                sheetContent!!.tips.forEach { tip ->
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        Icon(Icons.Default.CheckCircle, null, tint = sheetContent!!.color, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = tip, style = MaterialTheme.typography.bodyMedium, color = iOSSecondaryLabel)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { showSheet = false },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = iOSBlue),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HormonalTimelineCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(iOSPink.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Info, null, tint = iOSPink, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Ondas Hormonais", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Ciclo Atual", style = MaterialTheme.typography.labelSmall, color = iOSSecondaryLabel)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                val width = size.width
                val height = size.height
                val pathEstrogen = androidx.compose.ui.graphics.Path()
                val pathProgesterone = androidx.compose.ui.graphics.Path()
                
                // Desenho das curvas
                pathEstrogen.moveTo(0f, height * 0.8f)
                pathEstrogen.cubicTo(width * 0.3f, height * 0.9f, width * 0.45f, 0f, width * 0.55f, 0f)
                pathEstrogen.cubicTo(width * 0.65f, 0f, width * 0.8f, height * 0.6f, width, height * 0.7f)
                
                pathProgesterone.moveTo(0f, height * 0.9f)
                pathProgesterone.cubicTo(width * 0.5f, height * 0.9f, width * 0.75f, height * 0.1f, width, height * 0.2f)
                
                drawPath(pathEstrogen, color = iOSPink, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
                drawPath(pathProgesterone, color = iOSPurple, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendDot("Estrogênio", iOSPink)
                LegendDot("Progesterona", iOSPurple)
            }
        }
    }
}

@Composable
fun MetricRingCard(modifier: Modifier, title: String, value: String, progress: Float, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
                CircularProgressIndicator(progress = { progress }, color = color, strokeWidth = 8.dp, trackColor = color.copy(alpha = 0.1f), strokeCap = StrokeCap.Round)
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Text(title, style = MaterialTheme.typography.labelSmall, color = iOSSecondaryLabel, fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun SymptomInsightRow(title: String, desc: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = iOSSecondaryLabel)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = iOSTertiaryLabel, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun NextCycleCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Previsão do Próximo Ciclo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(iOSBlue.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DateRange, null, tint = iOSBlue)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Início Estimado", style = MaterialTheme.typography.labelSmall, color = iOSSecondaryLabel)
                    Text("14 de Março", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("daqui a 18 dias", style = MaterialTheme.typography.bodySmall, color = iOSBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = iOSSecondaryLabel)
    }
}

@Composable
fun iOSGroupedContainer(content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.surface), content = content)
}

@Composable
fun HealthTipCard() {
    Surface(modifier = Modifier.fillMaxWidth(), color = iOSBlue.copy(alpha = 0.05f), shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, iOSBlue.copy(alpha = 0.1f))) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(iOSBlue.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Info, null, tint = iOSBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Insight do Dia", fontWeight = FontWeight.Bold, color = iOSBlue)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Reduzir cafeína durante a fase lútea ajuda a diminuir a sensibilidade nos seios e irritabilidade.", style = MaterialTheme.typography.bodyMedium, color = iOSLabel, lineHeight = 20.sp)
            }
        }
    }
}

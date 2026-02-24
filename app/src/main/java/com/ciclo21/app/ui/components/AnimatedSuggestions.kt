package com.ciclo21.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciclo21.app.domain.model.CyclePhase
import com.ciclo21.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun AnimatedSuggestionsBanner(
    phase: CyclePhase,
    isPregnancyMode: Boolean = false,
    onSuggestionClick: (PopUpData) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestionData = if (isPregnancyMode) {
        listOf(
            PopUpData("Vínculo Maternal", "Seu bebê já pode sentir o toque e o calor do seu corpo.", listOf("Faça carinho na barriga.", "Ouça músicas suaves."), iOSPink),
            PopUpData("Saúde do Bebê", "A vitamina D e o Cálcio são fundamentais.", listOf("Tome sol matinal.", "Pré-natal em dia."), iOSBlue)
        )
    } else {
        when(phase) {
            CyclePhase.MENSTRUACAO -> listOf(PopUpData("Alívio Térmico", "O calor relaxa o útero.", listOf("Use bolsas de água morna."), Menstruacao))
            else -> listOf(PopUpData("Dica do Dia", "Mantenha o corpo em movimento.", listOf("Caminhada leve.", "Beba água."), phase.color))
        }
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(phase, isPregnancyMode) {
        while (true) {
            delay(6000)
            currentIndex = (currentIndex + 1) % suggestionData.size
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth().wrapContentHeight().clickable { onSuggestionClick(suggestionData[currentIndex]) },
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp)) {
                if (isPregnancyMode) RadiantPulseAnimation(color = iOSPink)
                else when(phase) {
                    CyclePhase.MENSTRUACAO -> WaterDrinkingAnimation(color = Menstruacao)
                    else -> EnergySparkleAnimation(color = phase.color)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            AnimatedContent(targetState = currentIndex, label = "suggestion") { index ->
                Column(modifier = Modifier.weight(1f)) {
                    Text(if (isPregnancyMode) "JORNADA MATERNA" else "SUGESTÃO DA FASE", style = MaterialTheme.typography.labelSmall, color = if (isPregnancyMode) iOSPink else phase.color, fontWeight = FontWeight.Bold)
                    Text(suggestionData[index].title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text(suggestionData[index].description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun WaterDrinkingAnimation(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "water")
    val waterLevel by infiniteTransition.animateFloat(0.75f, 0.25f, infiniteRepeatable(tween(4000), RepeatMode.Reverse), label = "level")
    val waveOffset by infiniteTransition.animateFloat(0f, 2f * Math.PI.toFloat(), infiniteRepeatable(tween(2500)), label = "offset")
    Canvas(Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp))) {
        val path = Path()
        val baseLevel = size.height * waterLevel
        path.moveTo(0f, size.height)
        path.lineTo(0f, baseLevel)
        for (x in 0..size.width.toInt()) {
            val y = baseLevel + sin(x.toFloat() / size.width * 2f * Math.PI.toFloat() + waveOffset) * 5.dp.toPx()
            path.lineTo(x.toFloat(), y)
        }
        path.lineTo(size.width, size.height)
        path.close()
        drawPath(path, Brush.verticalGradient(listOf(color.copy(alpha = 0.6f), color)))
    }
}

@Composable
fun RadiantPulseAnimation(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "radiant")
    val radiusScale by infiniteTransition.animateFloat(0.5f, 1.2f, infiniteRepeatable(tween(2000), RepeatMode.Reverse), label = "radius")
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(Brush.radialGradient(listOf(color, Color.Transparent)), (size.width / 2) * radiusScale, center)
        drawCircle(color, 8.dp.toPx(), center)
    }
}

@Composable
fun EnergySparkleAnimation(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "energy")
    val offsetY by infiniteTransition.animateFloat(1f, 0f, infiniteRepeatable(tween(2000)), label = "offsetY")
    Canvas(Modifier.fillMaxSize().clip(CircleShape)) {
        drawCircle(color.copy(alpha = 0.1f))
        for (i in 0..2) {
            drawCircle(color.copy(alpha = offsetY), 4.dp.toPx(), androidx.compose.ui.geometry.Offset(size.width * (0.3f + i * 0.2f), size.height * ((offsetY + i * 0.3f) % 1f)))
        }
    }
}

@Composable
fun BreathingCalmAnimation(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "breath")
    val scale by infiniteTransition.animateFloat(0.6f, 0.9f, infiniteRepeatable(tween(4000), RepeatMode.Reverse), label = "scale")
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(color, (size.width / 2) * scale, style = Stroke(4.dp.toPx()), center = center)
    }
}

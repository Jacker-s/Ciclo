package com.ciclo21.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ciclo21.app.ui.theme.*

@Composable
fun ContraceptiveFlower(
    selectedBrand: String,
    modifier: Modifier = Modifier
) {
    val brands = listOf("Adoless", "Ciclo 21", "Diane 35", "Iumi", "Microvlar", "Selene", "Yasmin", "Yaz")
    val angleStep = 360f / brands.size
    val bloomScale by animateFloatAsState(targetValue = 1f, animationSpec = spring(0.8f))

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(280.dp)) {
        Box(modifier = Modifier.size(180.dp).blur(40.dp).background(Folicular.copy(alpha = 0.2f), CircleShape))
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val petalWidth = 70.dp.toPx()
            val petalHeight = 110.dp.toPx()

            brands.forEachIndexed { index, brand ->
                val rotation = index * angleStep
                val isSelected = brand == selectedBrand
                rotate(rotation, pivot = androidx.compose.ui.geometry.Offset(centerX, centerY)) {
                    val path = Path().apply {
                        moveTo(centerX, centerY)
                        cubicTo(centerX - petalWidth * 0.5f, centerY - petalHeight * 0.4f, centerX - petalWidth * 0.2f, centerY - petalHeight, centerX, centerY - petalHeight)
                        cubicTo(centerX + petalWidth * 0.2f, centerY - petalHeight, centerX + petalWidth * 0.5f, centerY - petalHeight * 0.4f, centerX, centerY)
                        close()
                    }
                    drawPath(path = path, brush = Brush.verticalGradient(colors = if (isSelected) listOf(Folicular, Menstruacao.copy(alpha = 0.8f)) else listOf(Color.LightGray.copy(alpha = 0.2f), Color.LightGray.copy(alpha = 0.05f))))
                }
            }
            drawCircle(brush = Brush.radialGradient(listOf(Color.White, Folicular.copy(alpha = 0.5f))), radius = 30.dp.toPx() * bloomScale, center = androidx.compose.ui.geometry.Offset(centerX, centerY))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = if (selectedBrand != "Selecione") selectedBrand.uppercase() else "MARCA", style = MaterialTheme.typography.labelSmall, color = if (selectedBrand != "Selecione") Menstruacao else Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

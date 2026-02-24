package com.ciclo21.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CicloLogo(
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val organicShape = GenericShape { size, _ ->
        moveTo(size.width * 0.2f, size.height * 0.2f)
        quadraticBezierTo(size.width * 0.5f, 0f, size.width * 0.8f, size.height * 0.2f)
        quadraticBezierTo(size.width, size.height * 0.5f, size.width * 0.8f, size.height * 0.8f)
        quadraticBezierTo(size.width * 0.5f, size.height, size.width * 0.2f, size.height * 0.8f)
        quadraticBezierTo(0f, size.height * 0.5f, size.width * 0.2f, size.height * 0.2f)
    }

    Box(
        modifier = modifier
            .size(size)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(color, color.copy(alpha = 0.6f))
                ),
                shape = organicShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "21",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontSize = (size.value * 0.35).sp,
                fontWeight = FontWeight.ExtraLight,
                fontFamily = FontFamily.Serif
            )
        )
    }
}

package com.ciclo21.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ciclo21.app.Screen

@Composable
fun LiquidNavigationBar(
    screens: List<Screen>,
    selectedRoute: String?,
    onItemSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier,
    indicatorColor: Color = MaterialTheme.colorScheme.primary
) {
    var rowWidth by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val selectedIndex = screens.indexOfFirst { it.route == selectedRoute }.takeIf { it >= 0 } ?: 0

    val indicatorPosition by animateDpAsState(
        targetValue = with(density) { (rowWidth / screens.size * selectedIndex).toDp() },
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "indicatorPosition"
    )

    // A "Pílula" flutuante
    Surface(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .height(68.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(34.dp),
                ambientColor = Color.Black.copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(34.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { rowWidth = it.width }
        ) {
            // Indicador de seleção dinâmico (Liquid effect)
            Box(
                modifier = Modifier
                    .offset(x = indicatorPosition)
                    .width(with(density) { (rowWidth / screens.size).toDp() })
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(indicatorColor.copy(alpha = 0.12f))
                )
            }

            Row(modifier = Modifier.fillMaxSize()) {
                screens.forEachIndexed { _, screen ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onItemSelected(screen) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = if (selectedRoute == screen.route) indicatorColor else Color.Gray.copy(alpha = 0.6f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

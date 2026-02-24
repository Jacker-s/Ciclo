package com.ciclo21.app.domain.model

import androidx.compose.ui.graphics.Color
import com.ciclo21.app.ui.theme.*

enum class CyclePhase(val displayName: String, val color: Color, val description: String) {
    MENSTRUACAO("Menstruação", Menstruacao, "Seu corpo está se renovando."),
    FOLICULAR("Fase Folicular", Folicular, "Sua energia está começando a subir."),
    OVULACAO("Ovulação", Ovulacao, "Você está no seu pico de fertilidade e brilho."),
    LUTEA("Fase Lútea", Lutea, "Momento de introspecção e cuidado.")
}

package com.example.simpleDatePicker

import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

fun getDayOfMonth(monthNumber: Int, year: Int): List<WheelPickerModel> {

  Log.d("TAG666", "monthNumber: " + monthNumber)
  Log.d("TAG666", "year: " + year)

  val leapYears = listOf(
    1403, 1407, 1411, 1415, 1419, 1423, 1427, 1431, 1435, 1439
  )

  val day29 = (1..29).map { WheelPickerModel(number = it, value = it.toString()) }
  val day30 = (1..30).map { WheelPickerModel(number = it, value = it.toString()) }
  val day31 = (1..31).map { WheelPickerModel(number = it, value = it.toString()) }

  return when (monthNumber) {
    in 1..6 -> day31
    in 7..11 -> day30
    12 -> if (year in leapYears) day30 else day29
    else -> emptyList()
  }

}

fun Modifier.fadeEdges() =
  this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
      drawContent()
      drawRect(
        brush = Brush.verticalGradient(
          0f to Color.Transparent,
          0.5f to Color.Black,
          1f to Color.Transparent
        ),
        blendMode = BlendMode.DstIn,
      )
    }
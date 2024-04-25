package com.example.simpleDatePicker

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke

@Immutable
data class WheelPickerModel(
  val number: Int,
  val value: String,
)

data class CurrentDate(
  var year: String,
  var month: String,
  var monthNumber: Int,
  var day: String,
)

sealed interface CenterIndicator {
  data class TwoLine(
    val lineThickness: Float = 10f,
    val cap: StrokeCap = StrokeCap.Round,
    val color: Color = Color.Black,
  ) : CenterIndicator

  data class RecRound(
    val cornerRadius: CornerRadius = CornerRadius(x = 10f, y = 10f),
    val style: DrawStyle = Stroke(),
    val color: Color = Color.Black,
  ) : CenterIndicator

}
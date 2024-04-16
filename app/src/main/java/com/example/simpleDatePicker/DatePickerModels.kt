package com.example.simpleDatePicker

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke

@Immutable
data class WheelPickerModel(
  var number: Int,
  var value: String,
)

data class CurrentDate(
  var year: String,
  var month: String,
  var monthNumber: Int,
  var day: String,
)

sealed interface CenterIndicator {
  data class TwoLine(
    var lineThickness: Float = 10f,
    var cap: StrokeCap = StrokeCap.Round,
    var color: Color = Color.Black,
  ) : CenterIndicator

  data class RecRound(
    var cornerRadius: CornerRadius = CornerRadius(x = 10f, y = 10f),
    var style: DrawStyle = Stroke(),
    var color: Color = Color.Black,
  ) : CenterIndicator

}
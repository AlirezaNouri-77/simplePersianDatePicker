package com.example.simpleDatePicker

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs
import kotlin.math.round

@Composable
fun DatePicker(
  currentDate: CurrentDate,
  size: DpSize = DpSize(width = 200.dp, height = 120.dp),
  showCenterIndicator: Boolean = false,
  centerIndicator: CenterIndicator = CenterIndicator.RecRound(),
  onMonthChange: (number: Int, name: String) -> Unit,
  onDayChange: (value: String) -> Unit,
  onYearChange: (value: String) -> Unit,
) {

  val monthList = mapOf(
    1 to "فروردین",
    2 to "اردیبهشت",
    3 to "خرداد",
    4 to "تیر",
    5 to "مرداد",
    6 to "شهریور",
    7 to "مهر",
    8 to "ابان",
    9 to "اذر",
    10 to "دی",
    11 to "بهمن",
    12 to "اسفند",
  ).map {
    WheelPickerModel(
      number = it.key,
      value = it.value
    )
  }
  val yearList = (1350..1480).map { WheelPickerModel(number = it, value = it.toString()) }
  var dayList by remember {
    mutableStateOf(
      getDayOfMonth(
        monthNumber = currentDate.monthNumber,
        year = currentDate.year.toInt()
      )
    )
  }

  BoxWithConstraints(
    modifier = Modifier
      .width(size.width)
      .height(size.height)
      .padding(10.dp),
    contentAlignment = Alignment.Center,
  ) {

    if (showCenterIndicator) {
      Box(
        modifier = Modifier
          .width(size.width)
          .height(this.maxHeight / 3)
          .drawWithContent {
            when (centerIndicator) {
              is CenterIndicator.TwoLine -> {
                this.drawLine(
                  color = centerIndicator.color,
                  start = Offset.Zero,
                  end = Offset(x = this.size.width, y = 0f),
                  cap = centerIndicator.cap,
                  strokeWidth = centerIndicator.lineThickness,
                )
                this.drawLine(
                  color = centerIndicator.color,
                  start = Offset(x = 0f, y = this.size.height),
                  end = Offset(x = this.size.width, y = this.size.height),
                  cap = centerIndicator.cap,
                  strokeWidth = centerIndicator.lineThickness,
                )
              }

              is CenterIndicator.RecRound -> {
                drawRoundRect(
                  color = centerIndicator.color,
                  cornerRadius = centerIndicator.cornerRadius,
                  style = centerIndicator.style,
                )
              }
            }

          },
      ) {}
    }


    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.width(size.width),
    ) {

      // Day
      WheelPicker(
        modifier = Modifier
          .weight(0.2f)
          .height(size.height),
        startIndex = dayList.indexOfFirst { it.value == currentDate.day },
        dataSize = dayList.size,
        onScrollEnd = { int ->
          onDayChange(dayList[int].value)
        }
      ) {
        Text(
          modifier = Modifier.fillMaxWidth(),
          fontSize = 14.sp,
          text = dayList[it].value,
          textAlign = TextAlign.Center,
          fontWeight = FontWeight.SemiBold,
        )
      }

      // Month
      WheelPicker(
        modifier = Modifier
          .weight(0.6f)
          .height(size.height),
        startIndex = monthList.indexOfFirst { it.number == currentDate.monthNumber },
        dataSize = monthList.size,
        onScrollEnd = { int ->
          onMonthChange(monthList[int].number, monthList[int].value)
          dayList = getDayOfMonth(
            monthNumber = monthList[int].number,
            year = currentDate.year.toInt()
          )
        }
      ) {
        Text(
          modifier = Modifier.fillMaxWidth(),
          fontSize = 18.sp,
          text = monthList[it].value,
          textAlign = TextAlign.Center,
          fontWeight = FontWeight.SemiBold,
        )
      }

      // Year
      WheelPicker(
        modifier = Modifier
          .weight(0.2f)
          .height(size.height),
        dataSize = yearList.size,
        startIndex = yearList.indexOfFirst { it.value == currentDate.year },
        onScrollEnd = { int ->
          onYearChange(yearList[int].value)
          dayList = getDayOfMonth(
            monthNumber = currentDate.monthNumber,
            year = yearList[int].value.toInt(),
          )
        }
      ) {
        Text(
          modifier = Modifier.fillMaxWidth(),
          fontSize = 14.sp,
          text = yearList[it].value,
          textAlign = TextAlign.Center,
          fontWeight = FontWeight.SemiBold,
        )
      }

    }

  }
}


@OptIn(ExperimentalFoundationApi::class, FlowPreview::class)
@Composable
private fun WheelPicker(
  modifier: Modifier,
  startIndex: Int,
  dataSize: Int,
  onScrollEnd: (Int) -> Unit,
  content: @Composable (Int) -> Unit,
) {

  val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)

  val firstItem = remember {
    snapshotFlow {
      listState.firstVisibleItemIndex
    }.debounce(200).distinctUntilChanged()
  }.collectAsState(initial = 0).value

  LaunchedEffect(key1 = firstItem) {
    onScrollEnd(firstItem)
  }

  BoxWithConstraints(modifier = modifier) {

    val halfHeight = round(this.maxHeight.value / 2)
    val itemHeight = this.maxHeight / 3

    LazyColumn(
      Modifier
        .height(this.maxHeight)
        .width(this.maxWidth)
        .fadeEdges(),
      contentPadding = PaddingValues(vertical = this.maxHeight / 3),
      state = listState,
      flingBehavior = rememberSnapFlingBehavior(SnapLayoutInfoProvider(listState)),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {

      items(dataSize) { index ->

        val floatScale by remember {
          derivedStateOf {
            val currentItem =
              listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                ?: return@derivedStateOf 0.6f
            val itemHalf = currentItem.size / 2
            (1f - minOf(1f, abs(currentItem.offset + itemHalf - halfHeight) / halfHeight) * 0.5f)
          }
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .scale(floatScale),
          contentAlignment = Alignment.Center,
        ) {
          content(index)
        }

      }
    }
  }

}
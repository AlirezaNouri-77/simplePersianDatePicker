package com.example.simpleDatePicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simpleDatePicker.ui.theme.PickerDateTheme
import saman.zamani.persiandate.PersianDate

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val persianDate = PersianDate()

    setContent {
      PickerDateTheme {

        var currentDate by remember {
          mutableStateOf(
            CurrentDate(
              persianDate.shYear.toString(),
              persianDate.shMonth.toString(),
              persianDate.shMonth,
              persianDate.shDay.toString(),
            )
          )
        }

        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {

          DatePicker(
            currentDate,
            showCenterIndicator = true,
            centerIndicator = CenterIndicator.TwoLine(lineThickness = 5f),
            onDayChange = { day ->
              currentDate = currentDate.copy(day = day)
            },
            onMonthChange = { number, month ->
              currentDate = currentDate.copy(monthNumber = number, month = month)
            },
            onYearChange = { year ->
              currentDate = currentDate.copy(year = year)
            },
          )

          Spacer(modifier = Modifier.height(20.dp))

          Text(
            text = "${currentDate.year} ${currentDate.month} ${currentDate.day}",
            fontSize = 17.sp
          )

        }

      }
    }
  }
}




package com.raj.weatherapp

import android.content.Context
import android.media.Image
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.raj.weatherapp.Model.WeatherModel
import com.raj.weatherapp.api.NetworkResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Weather(viewModel: WeatherViewModel, context: Context) {
    LaunchedEffect(Unit) {
        viewModel.getData("Bhavnagar")
    }

    var city by remember {
        mutableStateOf("")
    }

    var previousWeatherData by remember {
        mutableStateOf<WeatherModel?>(null)
    }

    val weatherResult = viewModel.weatherResult.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var weatherImage = viewModel.weatherResult.observeAsState()
    val focusManager: FocusManager = LocalFocusManager.current
    val focusRequester = remember {
        FocusRequester()
    }


    var backgroundImageRes by remember {
        mutableStateOf<Int?>(null) // Set a default background
    }

    // LaunchedEffect to update the background image based on weather condition
    LaunchedEffect(weatherResult.value) {
        if (weatherResult.value is NetworkResponse.Success) {
            val weatherCondition = (weatherResult.value as NetworkResponse.Success).data.weather[0].main
            backgroundImageRes = when (weatherCondition) {
                "Rain","Mist","Drizzle","Thunderstorm" -> R.drawable.rain
                "Clear" -> R.drawable.sunny
                "Clouds","Fog","Haze","Smoke","Dust","Sand","Ash","Tornado" -> R.drawable.cloud
                "Snow" -> R.drawable.snow
                else -> R.drawable.cloud
            }
            previousWeatherData = (weatherResult.value as NetworkResponse.Success).data
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val backgroundImage = backgroundImageRes?.let { painterResource(id = it) }
        if (backgroundImage != null) {
            Image(
                painter = backgroundImage,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Adjust the scaling as needed
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .focusRequester(focusRequester),
                    value = city,
                    singleLine = true,
                    onValueChange = {
                        city = it
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    placeholder = {
                        Text(text = "Search for any location")
                    },
                    shape = RoundedCornerShape(25.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (city.isEmpty()) {
                                Toast.makeText(context, "Enter City...", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.getData(city)
                            }
                            keyboardController?.hide()
                            focusRequester.freeFocus()
                            focusManager.clearFocus()

                        }

                    )

                )

            }

            when (val result = weatherResult.value) {
                is NetworkResponse.Failure -> {
                    previousWeatherData?.let {
                        Toast.makeText(context, "Failed to Load Data", Toast.LENGTH_SHORT).show()
                        WeatherDetail(data = it)
                    }
                }

                NetworkResponse.Loading -> {
                    ShowCircularProgressIndicator()
                }

                is NetworkResponse.Success -> {
                    WeatherDetail(data = result.data)
                }

                null -> {}
            }
        }
    }
}

@Composable
fun ShowCircularProgressIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun WeatherDetail(data: WeatherModel) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Add padding at the bottom to ensure space for the Card
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Weather information at the top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location Icon",
                    modifier = Modifier.size(40.dp)
                )
                Column {
                    Text(text = data.name, fontSize = 25.sp)
                    Text(text = data.sys.country, fontSize = 18.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val temp = String.format("%.1f",data.main.temp - 273.15)
            val minTemp = String.format("%.2f" ,data.main.temp_min - 273.15)
            val maxTemp = String.format("%.2f" ,data.main.temp_max - 273.15)



            Box(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$temp° C",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                    )
                    AsyncImage(
                        modifier = Modifier.size(100.dp),
                        model = "https://openweathermap.org/img/wn/${data.weather[0].icon}@4x.png",
                        contentDescription = "Condition Icon"
                    )
                    Text(
                        text = data.weather[0].main,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Min : $minTemp° C",
                            fontSize = 25.sp
                        )
                        Text(
                            text = "Max : $maxTemp° C",
                            fontSize = 25.sp
                        )
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(0.3f)// Glass-like effect
                ),
                shape = MaterialTheme.shapes.medium,
                border = null // Ensure no border is applied
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly // Ensures equal spacing
                    ) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(8.dp)) {
                            WeatherKeyVal(
                                key = "Humidity",
                                value = data.main.humidity,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                        Box(modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)) {
                            WeatherKeyVal(
                                key = "UV",
                                value = data.main.sea_level.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                            )
                        }
                        Box(modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)) {
                            WeatherKeyVal(
                                key = "Pressure",
                                value = data.main.pressure.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                            )
                        }

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Box(modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)) {
                            WeatherKeyVal(
                                key = "Sea Level",
                                value = data.main.sea_level,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                            )
                        }
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(8.dp)
                        ) {
                            WeatherKeyVal(
                                key = "Wind Speed",
                                value = data.wind.speed,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                            )
                        }
                        Box(modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)) {
                            WeatherKeyVal(
                                key = "Cloud Cover",
                                value = data.main.grnd_level.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                            )
                        }
                    }
                }
            }
        }
    }




@Composable
fun WeatherKeyVal(key: String, value: String , modifier: Modifier = Modifier) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.Transparent)
            .fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.3f) // Glass-like effect
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Clip
            )
            Text(
                text = key,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}


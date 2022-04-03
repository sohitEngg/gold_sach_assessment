package com.nasa.apod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val vm = ApodViewModel()
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ApodView(vm)
            }
        }
    }
}

@Composable
fun ApodView(vm: ApodViewModel) {
    LaunchedEffect(Unit, block = {
        vm.getApod()
    })

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row {
                    Text("NASA: Picture of the day")
                }
            })
    }) {
        if (vm.errorMessage.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (vm.apod.value.url.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(0.dp, 0.dp, 16.dp, 0.dp)
                    ) {
                        SubcomposeAsyncImage(
                            model = vm.apod.value.url,
                            loading = {
                                CircularProgressIndicator()
                            },
                            contentDescription = "Picture of the day"
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 16.dp, 0.dp),
                    ) {
                        CircularProgressIndicator()
                        Text("fetching APOD...")
                    }
                }
                Divider(modifier = Modifier.padding(0.dp, 10.dp, 16.dp, 0.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 10.dp, 16.dp, 0.dp)
                ) {
                    Text(
                        vm.apod.value.title,
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 10.dp, 16.dp, 0.dp)
                ) {
                    Text(
                        vm.apod.value.explanation,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 100,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 15.sp
                    )
                }
            }
        } else {
            Text(vm.errorMessage)
        }
    }
}

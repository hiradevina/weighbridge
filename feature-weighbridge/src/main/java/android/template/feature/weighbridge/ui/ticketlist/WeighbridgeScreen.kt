/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.template.feature.weighbridge.ui.ticketlist

import android.template.core.data.InboundTicketModel
import android.template.feature.weighbridge.ui.filter.FilterBottomSheet
import android.template.feature.weighbridge.ui.filter.InboundTicketFilter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.repeatOnLifecycle
import android.template.feature.weighbridge.ui.ticketlist.MyModelUiState.Success
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.TimeZone

@Composable
fun MyModelScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MyModelViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val items by produceState<MyModelUiState>(
        initialValue = MyModelUiState.Loading,
        key1 = lifecycle,
        key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(state = STARTED) {
            viewModel.uiState.collect { value = it }
        }
    }
    if (items is Success) {
        MyModelScreen(
            navController = navController,
            items = (items as Success).data,
            onSave = { },
            modifier = modifier,
            onFilter = { viewModel.filter(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyModelScreen(
    navController: NavController,
    items: List<InboundTicketModel>,
    onSave: (name: String) -> Unit,
    modifier: Modifier = Modifier,
    onFilter: (InboundTicketFilter) -> Unit
) {
    var showFilter by remember {
        mutableStateOf(false)
    }

    if (showFilter) {
        FilterBottomSheet(onDismiss = { showFilter = false }) { filter ->
            onFilter(filter)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Weighbridge Ticket List") })
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FloatingActionButton(
                    onClick = { navController.navigate("add") },
                ) {
                    Icon(Icons.Filled.Add, "Add Ticket")
                }
                FloatingActionButton(
                    onClick = { showFilter = true },
                ) {
                    Icon(Icons.Outlined.FilterAlt, "Filter Ticket")
                }
            }
        }
    ) {
        LazyColumn(modifier.padding(it), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) {
                TicketItem(ticket = it) {id ->
                    navController.navigate("edit/$id")
                }
            }
        }
    }
}

@Composable
fun TicketItem(ticket: InboundTicketModel, onEdit: (id: Int) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = { }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                Text("Ticket ID: #${ticket.id}")
                Text("${ticket.dateTime.formatted()}")


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Driver Name \n${ticket.driverName}")
                Text("License Plate \n${ticket.licenseNumber}", textAlign = TextAlign.End)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Inbound Weight \n${ticket.inboundWeight} Ton")
                Text("Outbound Weight \n${ticket.outboundWeight} Ton", textAlign = TextAlign.End)
            }
            Text("Net Weight \n${ticket.netWeight} Ton")
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onEdit(ticket.id) }) {
                Text("EDIT")
            }
        }
    }
}

private fun Long.formatted(): String {
    return SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm").apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(this)
}

// Previews

//@Preview(showBackground = true)
//@Composable
//private fun DefaultPreview() {
//    MyApplicationTheme {
//        MyModelScreen(listOf("Compose", "Room", "Kotlin"), onSave = {})
//    }
//}
//
//@Preview(showBackground = true, widthDp = 480)
//@Composable
//private fun PortraitPreview() {
//    MyApplicationTheme {
//        MyModelScreen(listOf("Compose", "Room", "Kotlin"), onSave = {})
//    }
//}

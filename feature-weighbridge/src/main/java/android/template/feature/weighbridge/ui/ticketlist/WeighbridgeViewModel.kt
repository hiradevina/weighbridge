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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import android.template.core.data.MyModelRepository
import android.template.feature.weighbridge.ui.filter.InboundTicketFilter
import android.template.feature.weighbridge.ui.ticketlist.MyModelUiState.Error
import android.template.feature.weighbridge.ui.ticketlist.MyModelUiState.Loading
import android.template.feature.weighbridge.ui.ticketlist.MyModelUiState.Success
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class MyModelViewModel @Inject constructor(
    private val myModelRepository: MyModelRepository
) : ViewModel() {

    private val _filterState = MutableStateFlow(InboundTicketFilter())
    val filterState: StateFlow<InboundTicketFilter> = _filterState.asStateFlow()

    val uiState: StateFlow<MyModelUiState> = myModelRepository
        .myModels.combine(filterState) { ticket, filter -> Pair(filter.filter(ticket), filter) }
        .map<Pair<List<InboundTicketModel>, InboundTicketFilter>, MyModelUiState> { pair ->
            Success(
                data = pair.first,
                filter = pair.second
            )
        }
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun filter(filter: InboundTicketFilter) {
        Log.d("filter", filter.toString())
        _filterState.value = filter
    }


}

private fun InboundTicketFilter.filter(tickets: List<InboundTicketModel>): List<InboundTicketModel> {
    return tickets.filter { ticket ->
        val dateFilter = if (from != 0L || until != 0L) {
            (ticket.dateTime in from..until)
        } else true
        val licenseFilter = if (licenseNumber != "") {
            ticket.licenseNumber.contains(licenseNumber, ignoreCase = true)
        } else true
        val driverFilter = if (driverName != "") {
            ticket.driverName.contains(driverName, ignoreCase = true)
        } else true
        dateFilter && licenseFilter && driverFilter
    }
}

sealed interface MyModelUiState {
    object Loading : MyModelUiState
    data class Error(val throwable: Throwable) : MyModelUiState
    data class Success(val data: List<InboundTicketModel>, val filter: InboundTicketFilter) :
        MyModelUiState
}

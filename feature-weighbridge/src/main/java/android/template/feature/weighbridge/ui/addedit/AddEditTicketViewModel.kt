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

package android.template.feature.weighbridge.ui.addedit

import android.template.core.data.InboundTicketModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import android.template.core.data.MyModelRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddEditTicketViewModel @Inject constructor(
    private val myModelRepository: MyModelRepository
) : ViewModel() {

    var input by mutableStateOf(InboundTicketModel())
        private set

    private val _uiState = MutableStateFlow(AddEditTicketState())
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<AddEditTicketState> = _uiState

    fun onEvent(event: AddEditTicketEvent) {
        when(event) {
            is AddEditTicketEvent.EditDateTime -> editDate(event.value)
            is AddEditTicketEvent.EditDriverName -> editDriverName(event.name)
            is AddEditTicketEvent.EditInboundWeight -> editInboundWeight(event.weight)
            is AddEditTicketEvent.EditLicense -> editLicense(event.license)
            is AddEditTicketEvent.EditOutboundWeight -> editOutboundWeight(event.weight)
            AddEditTicketEvent.Submit -> submit()
            is AddEditTicketEvent.EditTicket -> fetchTicketData(event.ticketId)
        }
    }

    private fun fetchTicketData(ticketId: Int) {
        viewModelScope.launch {
            val ticketDetail = myModelRepository.getById(ticketId)
            input = ticketDetail
            _uiState.update { it.copy(operationType = AddEditTicketType.EDIT) }
        }
    }

    private fun submit() {
        viewModelScope.launch {
            if (_uiState.value.operationType == AddEditTicketType.ADD) {
                myModelRepository.add(input)
            } else {
                myModelRepository.edit(input)
            }
        }
    }

    private fun editDate(selectedDate: Long) {
        input = input.copy(dateTime = selectedDate)
        validateButton()
    }

    private fun editLicense(license: String) {
        input = input.copy(licenseNumber = license)
        validateButton()
    }

    private fun editDriverName(name: String) {
        input = input.copy(driverName = name)
        validateButton()
    }

    private fun editInboundWeight(weight: Long) {
        input = input.copy(inboundWeight = weight)
        validateButton()
    }

    private fun editOutboundWeight(weight: Long) {
        input = input.copy(outboundWeight = weight)
        validateButton()
    }

    private fun validateButton() {
        _uiState.update {
            val validate = input.driverName.isNotEmpty() && input.licenseNumber.isNotEmpty() && input.inboundWeight != 0L && input.outboundWeight != 0L
            it.copy(enableButton = validate)
        }
    }
}

sealed interface AddEditTicketEvent {
    data class EditTicket(val ticketId: Int) : AddEditTicketEvent
    data class EditDateTime(val value: Long) : AddEditTicketEvent
    data class EditLicense(val license: String) : AddEditTicketEvent
    data class EditDriverName(val name: String) : AddEditTicketEvent
    data class EditInboundWeight(val weight: Long) : AddEditTicketEvent
    data class EditOutboundWeight(val weight: Long) : AddEditTicketEvent
    object Submit : AddEditTicketEvent
}

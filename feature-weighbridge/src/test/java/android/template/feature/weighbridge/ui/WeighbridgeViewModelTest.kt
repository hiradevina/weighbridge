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

package android.template.feature.weighbridge.ui.mymodel


import android.template.core.data.InboundTicketModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import android.template.core.data.MyModelRepository
import android.template.feature.weighbridge.ui.MainDispatcherRule
import android.template.feature.weighbridge.ui.addedit.AddEditTicketEvent
import android.template.feature.weighbridge.ui.addedit.AddEditTicketViewModel
import android.template.feature.weighbridge.ui.ticketlist.MyModelUiState
import android.template.feature.weighbridge.ui.ticketlist.MyModelViewModel
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import org.junit.Rule
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class MyModelViewModelTest {


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


//    @Test
//    fun uiState_initiallyLoading() = runTest {
//        val viewModel = MyModelViewModel(FakeMyModelRepository())
//        assertEquals(viewModel.uiState.first(), MyModelUiState.Loading)
//    }

    @Test
    fun uiState_onItemSaved_isDisplayed() = runTest(timeout = 60.seconds) {
        val repo = FakeMyModelRepository()
        val addEditTicketViewModel = AddEditTicketViewModel(repo)

        val newTicket = InboundTicketModel(
            dateTime = 1718582087074,
            licenseNumber = "H 3058 KKR",
            driverName = "Mbing",
            inboundWeight = 4,
            outboundWeight = 9
        )
        addEditTicketViewModel.onEvent(AddEditTicketEvent.EditLicense(newTicket.licenseNumber))
        addEditTicketViewModel.onEvent(AddEditTicketEvent.EditDriverName(newTicket.driverName))
        addEditTicketViewModel.onEvent(AddEditTicketEvent.EditDateTime(newTicket.dateTime))
        addEditTicketViewModel.onEvent(AddEditTicketEvent.EditInboundWeight(newTicket.inboundWeight))
        addEditTicketViewModel.onEvent(AddEditTicketEvent.EditOutboundWeight(newTicket.outboundWeight))
        addEditTicketViewModel.onEvent(AddEditTicketEvent.Submit)



//        val viewModel = MyModelViewModel(repo)
//        val res = repo.myModels.toList()
//        assertEquals(res.size, 2)
    }
}

private class FakeMyModelRepository : MyModelRepository {

    private val data = mutableListOf<InboundTicketModel>(
        InboundTicketModel(
            id = 1,
            inboundWeight = 3,
            outboundWeight = 4,
            driverName = "sigit",
            licenseNumber =  "B 9485 FJR",
            dateTime = 1718582087071
        )
    )

    override val myModels: Flow<List<InboundTicketModel>>
        get() = flow { emit(data.toList()) }

    override suspend fun add(ticket: InboundTicketModel) {
        data.add(ticket.copy(id = data.last().id + 1))
        println(data)
    }

    override suspend fun getById(id: Int): InboundTicketModel {
        return data.first()
    }

    override suspend fun edit(ticket: InboundTicketModel) {
        //
    }


}

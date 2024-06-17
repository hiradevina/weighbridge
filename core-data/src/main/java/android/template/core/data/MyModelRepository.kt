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

package android.template.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.template.core.database.MyModel
import android.template.core.database.MyModelDao
import javax.inject.Inject

interface MyModelRepository {
    val myModels: Flow<List<InboundTicketModel>>

    suspend fun add(ticket: InboundTicketModel)

    suspend fun getById(id: Int): InboundTicketModel

    suspend fun edit(ticket: InboundTicketModel)
}

class DefaultMyModelRepository @Inject constructor(
    private val myModelDao: MyModelDao
) : MyModelRepository {

    override val myModels: Flow<List<InboundTicketModel>> =
        myModelDao.getMyModels().map { items -> items.map { it.toUiModel() } }

    override suspend fun add(ticket: InboundTicketModel) {
        myModelDao.insertMyModel(ticket.toDaoModel())
    }

    override suspend fun getById(id: Int): InboundTicketModel {
        return myModelDao.getById(id).toUiModel()
    }

    override suspend fun edit(ticket: InboundTicketModel) {
        myModelDao.updateModel(ticket.toModifiedDaoModel())
    }
}

private fun InboundTicketModel.toModifiedDaoModel(): MyModel {
    return MyModel(
        dateTime = dateTime,
        licenseNumber = licenseNumber,
        driverName = driverName,
        inboundWeight = inboundWeight,
        outboundWeight = outboundWeight
    ).apply {
        uid = id
    }
}

private fun MyModel.toUiModel(): InboundTicketModel {
    return InboundTicketModel(
        id = uid,
        dateTime = dateTime,
        licenseNumber = licenseNumber,
        driverName = driverName,
        inboundWeight = inboundWeight,
        outboundWeight = outboundWeight
    )
}

private fun InboundTicketModel.toDaoModel(): MyModel {
    return MyModel(
        dateTime = dateTime,
        licenseNumber = licenseNumber,
        driverName = driverName, 
        inboundWeight = inboundWeight,
        outboundWeight = outboundWeight
    )
}

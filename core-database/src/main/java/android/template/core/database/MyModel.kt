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

package android.template.core.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity
data class MyModel(
    val dateTime: Long = 0,
    val licenseNumber: String = "",
    val driverName: String = "",
    var inboundWeight: Long = 0,
    var outboundWeight: Long = 0
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}

@Dao
interface MyModelDao {
    @Query("SELECT * FROM mymodel ORDER BY uid DESC LIMIT 10")
    fun getMyModels(): Flow<List<MyModel>>

    @Query("SELECT * FROM mymodel WHERE uid=:id LIMIT 1")
    suspend fun getById(
        id: Int
    ): MyModel

    @Update
    suspend fun updateModel(vararg model: MyModel)

    @Insert
    suspend fun insertMyModel(item: MyModel)
}

package android.template.feature.weighbridge.ui.filter

import android.template.feature.weighbridge.ui.shared.InboundTimePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    initialTimestampFrom: Long = 0,
    initialTimestampUntil: Long = 0,
    driverName: String = "",
    license: String = "",
    onDismiss: () -> Unit,
    onSubmit: (InboundTicketFilter) -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var timestampFrom: Long by remember { mutableStateOf(initialTimestampFrom) }
    var timestampUntil: Long by remember { mutableStateOf(initialTimestampUntil) }
    var driverNameFilter: String by remember { mutableStateOf(driverName) }
    var licenseFilter: String by remember { mutableStateOf(license) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Filter by Date")
            InboundTimePicker(
                modifier = Modifier.fillMaxWidth(),
                timestamp = timestampFrom,
                onChanged = { timestampFrom = it },
                title = "Dari"
            )
            InboundTimePicker(
                modifier = Modifier.fillMaxWidth(),
                timestamp = timestampUntil,
                onChanged = { timestampUntil = it },
                title = "Sampai"
            )
            Text("Filter by Driver Name")
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nama Pengemudi") },
                value = driverNameFilter,
                onValueChange = { value -> driverNameFilter = value }
            )
            Text("Filter by Truck License Plate", modifier = Modifier.fillMaxWidth())
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("License Plate") },
                value = licenseFilter,
                onValueChange = { value -> licenseFilter = value }
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onSubmit(
                        InboundTicketFilter(
                            timestampFrom,
                            timestampUntil,
                            licenseFilter,
                            driverNameFilter
                        )
                    )
                    onDismiss()
                }) {
                Text("Apply Filter")
            }
        }
    }
}
package android.template.feature.weighbridge.ui.addedit

import android.template.feature.weighbridge.ui.shared.InboundTimePicker
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun AddEditTicketScreen(
    navController: NavController,
    ticketId: Int? = null,
    viewModel: AddEditTicketViewModel = hiltViewModel()
) {
    val onEvent: (AddEditTicketEvent) -> Unit = { viewModel.onEvent(it) }
    if (ticketId != null) {
        onEvent(AddEditTicketEvent.EditTicket(ticketId))
    }

    AddEditTicketScreen(
        viewModel, onEvent
    ) {
        navController.popBackStack()
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AddEditTicketScreen(
    viewModel: AddEditTicketViewModel,
    onEvent: (AddEditTicketEvent) -> Unit,
    onClose: () -> Unit,
) {
    val input = viewModel.input
    val state = viewModel.uiState.collectAsState().value
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(state.title) })
        }
    )
    {
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .padding(it)
                .padding(16.dp)
        ) {
            val (etDateTime, etDriverName, etLicenseNum, etInbound, etOutbound, btnSubmit) = createRefs()
            InboundTimePicker(Modifier.constrainAs(etDateTime) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
                timestamp = input.dateTime,
                onChanged = { value -> onEvent(AddEditTicketEvent.EditDateTime(value)) }
            )
            TextField(
                modifier = Modifier.constrainAs(etDriverName) {
                    top.linkTo(etDateTime.bottom, margin = 8.dp)
                    start.linkTo(etDateTime.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
                enabled = state.operationType == AddEditTicketType.ADD,
                readOnly = state.operationType != AddEditTicketType.ADD,
                label = { Text("Nama Pengemudi") },
                value = input.driverName,
                onValueChange = { value -> onEvent(AddEditTicketEvent.EditDriverName(value)) }
            )
            TextField(
                modifier = Modifier.constrainAs(etLicenseNum) {
                    top.linkTo(etDriverName.bottom, margin = 8.dp)
                    start.linkTo(etDateTime.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
                enabled = state.operationType == AddEditTicketType.ADD,
                readOnly = state.operationType != AddEditTicketType.ADD,
                label = { Text("No. Polisi") },
                value = input.licenseNumber,
                onValueChange = { value -> onEvent(AddEditTicketEvent.EditLicense(value)) })
            TextField(
                modifier = Modifier.constrainAs(etInbound) {
                    top.linkTo(etLicenseNum.bottom, margin = 8.dp)
                    start.linkTo(etDateTime.start)
                    end.linkTo(etOutbound.start)
                    width = Dimension.fillToConstraints
                },
                label = { Text("Inbound Weight") },
                suffix = { Text("Ton") },
                value = input.inboundWeight.toString(),
                onValueChange = { value ->
                    onEvent(
                        AddEditTicketEvent.EditInboundWeight(
                            value.toLongOrNull() ?: 0
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            TextField(
                modifier = Modifier.constrainAs(etOutbound) {
                    top.linkTo(etInbound.top)
                    start.linkTo(etInbound.end, margin = 8.dp)
                    end.linkTo(parent.end)
                    bottom.linkTo(etInbound.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
                value = input.outboundWeight.toString(),
                label = { Text("Outbound Weight") },
                suffix = { Text("Ton") },
                onValueChange = { value ->
                    onEvent(
                        AddEditTicketEvent.EditOutboundWeight(
                            value.toLongOrNull() ?: 0
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(modifier = Modifier.constrainAs(btnSubmit) {
                top.linkTo(etInbound.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }, enabled = state.enableButton,
                onClick = {
                    onEvent(AddEditTicketEvent.Submit)
                    onClose()
                }) {
                Text("Tambah Tiket")
            }
        }
    }
}

//@Preview
//@Composable
//fun AddEditTickerScreenPreview() {
//    MyApplicationTheme {
//        AddEditTicketScreen()
//    }
//}


package android.template.feature.weighbridge.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboundTimePicker(
    modifier: Modifier,
    timestamp: Long,
    onChanged: (Long) -> Unit,
    title: String = "Tanggal Masuk"
) {

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = timestamp)
    val timePickerState =
        rememberTimePickerState(
            initialMinute = timestamp.toMinute(),
            initialHour = timestamp.toHour()
        )

    var openDialog: InboundTimePickerDialogState by remember {
        mutableStateOf(
            InboundTimePickerDialogState.CLOSE
        )
    }

    val selectedTimeInMillis = (datePickerState.selectedDateMillis
        ?: 0) + timePickerState.hour.times(3600000) + timePickerState.minute.times(60000)

    val selectedDate = Calendar.getInstance().apply {
        timeInMillis = selectedTimeInMillis
    }
    val formattedDate = SimpleDateFormat("EEEE dd MMMM yyyy HH:mm").apply {
        timeZone = TimeZone.getTimeZone("ID")
    }

    val dateFieldValue =
        if (selectedDate.timeInMillis != 0L) formattedDate.format(selectedDate.time) else "Pilih tanggal"


    TextField(
        modifier = modifier.clickable {
            openDialog = InboundTimePickerDialogState.DATE
        },
        leadingIcon = {
            IconButton(onClick = {
                openDialog = InboundTimePickerDialogState.DATE
            }) {
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.surfaceTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        label = { Text(title) },
        value = dateFieldValue.toString(),
        onValueChange = {},
        readOnly = true
    )


    when (openDialog) {
        InboundTimePickerDialogState.DATE -> {
            DatePickerDialog(
                onDismissRequest = {
                    openDialog = InboundTimePickerDialogState.CLOSE
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openDialog = InboundTimePickerDialogState.TIME
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openDialog = InboundTimePickerDialogState.CLOSE
                        }
                    ) {
                        Text("CANCEL")
                    }
                }
            ) {
                DatePicker(
                    modifier = modifier,
                    state = datePickerState
                )
            }
        }

        InboundTimePickerDialogState.TIME -> {
            TimePickerDialog(
                onDismissRequest = { /*TODO*/ },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openDialog = InboundTimePickerDialogState.CLOSE
                            onChanged(
                                (datePickerState.selectedDateMillis
                                    ?: 0) + timePickerState.hour.times(3600000) + timePickerState.minute.times(
                                    60000
                                )
                            )
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openDialog = InboundTimePickerDialogState.CLOSE
                        }
                    ) { Text("Cancel") }
                },
            )
            {
                TimePicker(
                    state = timePickerState,
                )
            }
        }

        InboundTimePickerDialogState.CLOSE -> {
            // no op
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = containerColor
                ),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}

private fun Long.toMinute(): Int {
    // Convert milliseconds to total seconds
    val totalSeconds = this / 1000

    // Convert total seconds to total minutes
    val totalMinutes = totalSeconds / 60

    // Get the minute of the hour (0-59) by taking modulus 60 of total minutes
    val minuteOfHour = (totalMinutes % 60).toInt()

    return minuteOfHour
}

private fun Long.toHour(): Int {
    val totalSeconds = this / 1000

    // Convert total seconds to total minutes
    val totalMinutes = totalSeconds / 60

    // Convert total minutes to total hours
    val totalHours = totalMinutes / 60

    // Get the hour of the day (0-23) by taking modulus 24 of total hours
    val hourOfDay = (totalHours % 24).toInt()

    return hourOfDay
}

enum class InboundTimePickerDialogState {
    CLOSE, DATE, TIME
}
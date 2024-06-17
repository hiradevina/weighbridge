package android.template.core.data

import android.util.Log

data class InboundTicketModel(
    val id: Int = -1,
    val dateTime: Long = System.currentTimeMillis(),
    val licenseNumber: String = "",
    val driverName: String = "",
    var inboundWeight: Long = 0,
    var outboundWeight: Long = 0
) {
    val netWeight: Long
        get() = outboundWeight - inboundWeight
}

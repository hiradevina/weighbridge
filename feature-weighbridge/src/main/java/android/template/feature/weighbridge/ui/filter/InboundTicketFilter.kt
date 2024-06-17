package android.template.feature.weighbridge.ui.filter


data class InboundTicketFilter(
    val from: Long = 0,
    val until: Long = 0,
    val licenseNumber: String = "",
    val driverName: String = "",
) {

}
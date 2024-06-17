package android.template.feature.weighbridge.ui.addedit

data class AddEditTicketState(
    val operationType: AddEditTicketType = AddEditTicketType.ADD,
    val enableButton: Boolean = false,
    val dateError: String = "",
    val nameError: String = "",
    val licenseError: String = "",
    val inWeightError: String = "",
    val outWeightError: String = ""
) {
    val title: String
        get() {
            return when (operationType) {
                AddEditTicketType.ADD -> "Tambah Tiket"
                AddEditTicketType.EDIT -> "Edit Tiket"
            }
        }
}

enum class AddEditTicketType {
    ADD, EDIT
}

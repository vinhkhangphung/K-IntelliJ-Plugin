package pvk.vn.dialog

import com.intellij.openapi.ui.ValidationInfo
import javax.swing.JComponent
import javax.swing.JTextField

class MigrationDialogValidator(private val state: MigrationDialogState) {

    fun validate(): ValidationInfo? {
        return validateFolder()
            ?: validateTableName()
            ?: validateOperation()
            ?: validateDescription()
            ?: validateVersion(
                state.majorVersionField,
                state.majorVersionField.text.trim(),
                "Major"
            )
            ?: validateVersion(
                state.minorVersionField,
                state.minorVersionField.text.trim(),
                "Minor"
            )
    }

    private fun validateFolder(): ValidationInfo? {
        if (state.folderField.text.isBlank())
            return ValidationInfo("Output folder is required", state.folderField)
        if (state.selectedDirectory?.isDirectory != true)
            return ValidationInfo("Selected path is not a valid directory", state.folderField)
        return null
    }

    private fun validateTableName(): ValidationInfo? {
        if (state.tableName.isBlank()) {
            val component: JComponent =
                if (state.isManualEntry) state.manualTableField else state.tableComboBox
            return ValidationInfo("Table name is required", component)
        }
        return null
    }

    private fun validateOperation(): ValidationInfo? {
        if (state.operation.isBlank())
            return ValidationInfo("Operation is required", state.customOperationField)
        return null
    }

    private fun validateDescription(): ValidationInfo? {
        if (state.descriptionField.text.isBlank())
            return ValidationInfo("Description is required", state.descriptionField)
        return null
    }

    private fun validateVersion(
        field: JTextField,
        value: String,
        label: String
    ): ValidationInfo? {
        val number = value.toIntOrNull()
        if (value.isEmpty() || number == null || number < 1)
            return ValidationInfo("$label version must be a positive number", field)
        return null
    }
}

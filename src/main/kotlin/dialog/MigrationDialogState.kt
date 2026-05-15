package pvk.vn.dialog

import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.ui.ComboBox
import pvk.vn.DatabaseTableFetcher
import pvk.vn.constants.MigrationConstants
import javax.swing.JTextField

class MigrationDialogState(
    val folderField: TextFieldWithBrowseButton,
    val tableComboBox: ComboBox<Any>,
    val manualTableField: JTextField,
    val operationBox: javax.swing.JComboBox<String>,
    val customOperationField: JTextField,
    val descriptionField: JTextField,
    val majorVersionField: JTextField,
    val minorVersionField: JTextField
) {
    val isManualEntry: Boolean
        get() = tableComboBox.selectedItem == MigrationConstants.MANUAL_ENTRY

    val tableName: String
        get() {
            val selected = tableComboBox.selectedItem
            return when {
                isManualEntry || selected == null ->
                    manualTableField.text.trim().lowercase().replace(" ", "_")
                selected is DatabaseTableFetcher.TableEntry ->
                    selected.tableName.lowercase()
                else ->
                    selected.toString().trim().lowercase().replace(" ", "_")
            }
        }

    val operation: String
        get() {
            val raw = operationBox.selectedItem?.toString()
                ?.substringBefore(" ") ?: ""
            return if (raw == "custom") customOperationField.text.trim().lowercase()
            else raw
        }

    val description: String
        get() = descriptionField.text.trim().lowercase().replace(" ", "_")

    val majorVersion: String
        get() = majorVersionField.text.trim().ifEmpty { MigrationConstants.DEFAULT_MAJOR_VERSION }

    val minorVersion: String
        get() = minorVersionField.text.trim().ifEmpty { MigrationConstants.DEFAULT_MINOR_VERSION }

    val selectedDirectory
        get() = LocalFileSystem.getInstance()
            .findFileByPath(folderField.text.trim())
}

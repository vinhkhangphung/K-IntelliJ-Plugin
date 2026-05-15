package pvk.vn.dialog

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import pvk.vn.DatabaseTableFetcher
import pvk.vn.MigrationSettings
import pvk.vn.constants.MigrationConstants
import pvk.vn.util.onAnyChange
import javax.swing.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent


class CreateMigrationDialog(
    private val project: Project,
    defaultDirectory: String? = null
) : DialogWrapper(project) {

    // ── Lazy DB fetch ─────────────────────────────────────
    private val allTables: List<DatabaseTableFetcher.TableEntry> by lazy {
        DatabaseTableFetcher.fetchTables(project)
    }

    // ── Raw fields ────────────────────────────────────────
    private val folderField = buildFolderField(defaultDirectory)

    private val tableComboBox         = ComboBox<Any>()
    private val tableSearchField      = JTextField(30).apply {
        toolTipText = "Type to filter tables from DB..."
    }
    private val manualTableField      = JTextField(30).apply {
        toolTipText = "Enter table name manually"
    }
    private val dbStatusLabel         = JLabel()

    private val operationBox          = JComboBox(MigrationConstants.OPERATIONS)
    private val customOperationField  = JTextField(30).apply { isEnabled = false }
    private val descriptionField      = JTextField(30)

    private val majorVersionField     = JTextField(5).apply {
        text        = MigrationSettings.loadMajorVersion()
        toolTipText = "Major version number (e.g. 1 → V1_x_...)"
    }
    private val minorVersionField     = JTextField(5).apply {
        text        = MigrationSettings.loadMinorVersion()
        toolTipText = "Minor version number (e.g. 1 → Vx_1_...)"
    }

    private val previewLabel          = JLabel().apply {
        foreground = java.awt.Color(100, 149, 237)
    }

    // ── Advanced toggle ───────────────────────────────────
    private var advancedExpanded      = false

    private val advancedToggleButton  = buildAdvancedToggleButton()

    // ── Collaborators ─────────────────────────────────────
    private val state = MigrationDialogState(
        folderField          = folderField,
        tableComboBox        = tableComboBox,
        manualTableField     = manualTableField,
        operationBox         = operationBox,
        customOperationField = customOperationField,
        descriptionField     = descriptionField,
        majorVersionField    = majorVersionField,
        minorVersionField    = minorVersionField
    )

    private val advancedPanel = MigrationDialogLayout(
        state                = state,
        tableSearchField     = tableSearchField,
        dbStatusLabel        = dbStatusLabel,
        previewLabel         = previewLabel,
        advancedToggleButton = advancedToggleButton,
        advancedPanel        = JPanel() // placeholder, replaced below
    ).buildAdvancedPanel(majorVersionField, minorVersionField)

    private val layout = MigrationDialogLayout(
        state                = state,
        tableSearchField     = tableSearchField,
        dbStatusLabel        = dbStatusLabel,
        previewLabel         = previewLabel,
        advancedToggleButton = advancedToggleButton,
        advancedPanel        = advancedPanel
    )

    private val tableComboManager = TableComboManager(
        allTables          = allTables,
        comboBox           = tableComboBox,
        searchField        = tableSearchField,
        statusLabel        = dbStatusLabel,
        onSelectionChanged = {
            updateManualFieldVisibility()
            updatePreview()
        }
    )

    private val validator = MigrationDialogValidator(state)

    // ── Exposed values (used by the action after OK) ──────
    val tableName: String        get() = state.tableName
    val operation: String        get() = state.operation
    val description: String      get() = state.description
    val majorVersion: String     get() = state.majorVersion
    val minorVersion: String     get() = state.minorVersion
    val selectedDirectory: VirtualFile? get() = state.selectedDirectory

    // ── Init ──────────────────────────────────────────────
    init {
        title = MigrationConstants.DIALOG_TITLE
        init()
        attachListeners()
        updateManualFieldVisibility()
        updatePreview()
    }

    // ── DialogWrapper overrides ───────────────────────────
    override fun createCenterPanel(): JComponent =
        layout.buildCenterPanel()

    override fun doValidate(): ValidationInfo? =
        validator.validate()

    override fun doOKAction() {
        MigrationSettings.saveMajorVersion(majorVersion)
        MigrationSettings.saveMinorVersion(minorVersion)
        MigrationSettings.saveLastDirectory(folderField.text.trim())
        super.doOKAction()
    }

    // ── Private helpers ───────────────────────────────────
    private fun updatePreview() {
        previewLabel.text = MigrationPreviewBuilder.build(
            majorVersion = state.majorVersion,
            minorVersion = state.minorVersion,
            tableName    = state.tableName,
            operation    = state.operation,
            description  = state.description
        )
    }

    private fun updateManualFieldVisibility() {
        manualTableField.isVisible = state.isManualEntry
        SwingUtilities.invokeLater { pack() }
    }

    private fun attachListeners() {
        attachSearchKeyNavigation()
        attachAdvancedToggle()
        attachOperationListener()
        attachPreviewListeners()
    }

    private fun attachSearchKeyNavigation() {
        tableSearchField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_DOWN, KeyEvent.VK_UP -> {
                        val direction = if (e.keyCode == KeyEvent.VK_DOWN) 1 else -1
                        val newIndex  = (tableComboBox.selectedIndex + direction)
                            .coerceIn(0, tableComboBox.itemCount - 1)
                        tableComboBox.selectedIndex = newIndex
                        if (!tableComboBox.isPopupVisible) tableComboBox.showPopup()
                        e.consume()
                    }
                    KeyEvent.VK_ENTER -> {
                        tableComboBox.hidePopup()
                        updateManualFieldVisibility()
                        updatePreview()
                        e.consume()
                    }
                    KeyEvent.VK_ESCAPE -> {
                        tableComboBox.hidePopup()
                        e.consume()
                    }
                }
            }
        })
    }

    private fun attachAdvancedToggle() {
        advancedToggleButton.addActionListener {
            advancedExpanded          = !advancedExpanded
            advancedPanel.isVisible   = advancedExpanded
            advancedToggleButton.text = if (advancedExpanded) "▼  Advanced"
            else "▶  Advanced"
            SwingUtilities.invokeLater { pack() }
        }
    }

    private fun attachOperationListener() {
        operationBox.addActionListener {
            customOperationField.isEnabled =
                operationBox.selectedItem?.toString()?.startsWith("custom") == true
            updatePreview()
        }
    }

    private fun attachPreviewListeners() {
        listOf(
            manualTableField,
            customOperationField,
            descriptionField,
            majorVersionField,
            minorVersionField
        ).forEach { field ->
            field.document.onAnyChange { updatePreview() }
        }
    }

    // ── Folder field factory ──────────────────────────────
    private fun buildFolderField(defaultDirectory: String?) =
        TextFieldWithBrowseButton().apply {
            text = MigrationSettings.loadLastDirectory() ?: defaultDirectory ?: ""
            addActionListener {
                val descriptor = FileChooserDescriptorFactory
                    .createSingleFolderDescriptor().apply {
                        title = "Select Migration Output Folder"
                    }
                val preselected = LocalFileSystem.getInstance()
                    .findFileByPath(this.text.trim())
                FileChooser.chooseFile(descriptor, project, preselected) { chosen ->
                    this.text = chosen.path
                }
            }
        }

    // ── Advanced toggle button factory ────────────────────
    private fun buildAdvancedToggleButton() = JButton("▶  Advanced").apply {
        isContentAreaFilled = false
        isBorderPainted     = false
        isFocusPainted      = false
        horizontalAlignment = SwingConstants.LEFT
        font                = font.deriveFont(java.awt.Font.BOLD, 11f)
        foreground          = java.awt.Color(100, 149, 237)
        cursor              = java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)
    }
}

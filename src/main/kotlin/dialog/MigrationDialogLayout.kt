package pvk.vn.dialog

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

class MigrationDialogLayout(
    private val state: MigrationDialogState,
    val tableSearchField: JTextField,
    val dbStatusLabel: JLabel,
    val previewLabel: JLabel,
    val advancedToggleButton: JButton,
    val advancedPanel: JPanel
) {
    fun buildCenterPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        val gbc   = defaultGbc()
        var row   = 0

        fun addRow(label: String, component: JComponent) {
            gbc.gridx = 0; gbc.gridy = row
            gbc.weightx = 0.3; gbc.gridwidth = 1
            panel.add(JLabel(label), gbc)
            gbc.gridx = 1; gbc.weightx = 0.7
            panel.add(component, gbc)
            row++
        }

        fun addFullRow(component: JComponent) {
            gbc.gridx = 0; gbc.gridy = row
            gbc.gridwidth = 2; gbc.weightx = 1.0
            panel.add(component, gbc)
            gbc.gridwidth = 1
            row++
        }

        addRow("Output Folder:",     state.folderField)
        addFullRow(dbStatusLabel)
        addRow("Search Table:",      tableSearchField)
        addRow("Table:",             state.tableComboBox)
        addRow("Manual Table Name:", state.manualTableField)
        addRow("Operation:",         state.operationBox)
        addRow("Custom Operation:",  state.customOperationField)
        addRow("Description:",       state.descriptionField)

        addFullRow(JSeparator())
        addFullRow(advancedToggleButton)
        addFullRow(advancedPanel)

        addFullRow(JSeparator())
        addFullRow(JLabel("Preview:"))
        addFullRow(previewLabel)

        return panel
    }

    fun buildAdvancedPanel(
        majorVersionField: JTextField,
        minorVersionField: JTextField
    ): JPanel {
        val panel = JPanel(GridBagLayout()).apply {
            isVisible = false
            border    = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(0, 4, 4, 4)
            )
        }

        val gbc = defaultGbc(insets = Insets(4, 8, 4, 8))

        // Major / Minor on same row
        val versionRow = buildVersionRow(majorVersionField, minorVersionField)

        gbc.gridx = 0; gbc.gridy = 0
        gbc.weightx = 0.3; gbc.gridwidth = 1
        panel.add(JLabel("Version:"), gbc)
        gbc.gridx = 1; gbc.weightx = 0.7
        panel.add(versionRow, gbc)

        // Version hint label
        val hintLabel = JLabel("Affects prefix: V{major}_{minor}_{timestamp}__...").apply {
            foreground = java.awt.Color(130, 130, 130)
            font       = font.deriveFont(java.awt.Font.ITALIC, 10f)
        }
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 1
        panel.add(hintLabel, gbc)

        return panel
    }

    private fun buildVersionRow(
        majorVersionField: JTextField,
        minorVersionField: JTextField
    ): JPanel = JPanel(GridBagLayout()).apply {
        isOpaque = false
        val g = GridBagConstraints().apply {
            insets = Insets(0, 0, 0, 8)
            fill   = GridBagConstraints.HORIZONTAL
        }

        g.gridx = 0; g.weightx = 0.0; add(JLabel("Major:"), g)
        g.gridx = 1; g.weightx = 0.3; add(majorVersionField, g)

        g.gridx = 2; g.weightx = 0.0; g.insets = Insets(0, 16, 0, 8)
        add(JLabel("Minor:"), g)

        g.gridx = 3; g.weightx = 0.3; g.insets = Insets(0, 0, 0, 0)
        add(minorVersionField, g)
    }

    private fun defaultGbc(insets: Insets = Insets(6, 8, 6, 8)) =
        GridBagConstraints().apply {
            this.insets = insets
            fill        = GridBagConstraints.HORIZONTAL
        }
}

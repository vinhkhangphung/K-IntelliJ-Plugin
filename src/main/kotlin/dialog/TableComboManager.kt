package pvk.vn.dialog

import com.intellij.openapi.ui.ComboBox
import pvk.vn.DatabaseTableFetcher
import pvk.vn.constants.MigrationConstants
import pvk.vn.util.FuzzyMatcher
import pvk.vn.util.onAnyChange
import javax.swing.*
import java.awt.Component

class TableComboManager(
    private val allTables: List<DatabaseTableFetcher.TableEntry>,
    val comboBox: ComboBox<Any>,
    val searchField: JTextField,
    val statusLabel: JLabel,
    private val onSelectionChanged: () -> Unit
) {
    private val comboModel = DefaultComboBoxModel<Any>()

    init {
        comboBox.model    = comboModel
        comboBox.isEditable = false
        comboBox.renderer   = TableEntryRenderer()

        populateCombo(allTables)
        configureStatusLabel()
        attachSearchFilter()

        comboBox.addActionListener { onSelectionChanged() }
    }

    private fun populateCombo(tables: List<DatabaseTableFetcher.TableEntry>) {
        comboModel.removeAllElements()
        comboModel.addElement(MigrationConstants.MANUAL_ENTRY)
        tables.forEach { comboModel.addElement(it) }
    }

    private fun configureStatusLabel() {
        searchField.isEnabled = allTables.isNotEmpty()

        statusLabel.text = if (allTables.isEmpty()) {
            "⚠  No DB connections found — manual entry mode"
        } else {
            val connectionCount = allTables.map { it.dataSourceName }.distinct().size
            "✔  ${allTables.size} table(s) from $connectionCount connection(s)"
        }

        statusLabel.foreground = if (allTables.isEmpty()) {
            java.awt.Color(200, 80, 80)
        } else {
            java.awt.Color(80, 180, 80)
        }

        statusLabel.font = statusLabel.font.deriveFont(java.awt.Font.ITALIC, 11f)
    }

    private fun attachSearchFilter() {
        searchField.document.onAnyChange { applyFilter() }
    }

    private fun applyFilter() {
        val query = searchField.text.trim()

        val filtered = if (query.isEmpty()) {
            allTables
        } else {
            allTables
                .mapNotNull { entry ->
                    val score = FuzzyMatcher.score(query, entry.tableName)
                    if (score > 0) entry to score else null
                }
                .sortedByDescending { it.second }
                .map { it.first }
        }

        populateCombo(filtered)
        if (filtered.isNotEmpty()) comboBox.showPopup()
    }

    // ── Renderer ─────────────────────────────────────────
    private inner class TableEntryRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: JList<*>, value: Any?, index: Int,
            isSelected: Boolean, cellHasFocus: Boolean
        ): Component {
            val label = super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus
            ) as JLabel

            when (value) {
                MigrationConstants.MANUAL_ENTRY -> {
                    label.text       = MigrationConstants.MANUAL_ENTRY
                    label.foreground = if (isSelected) list.selectionForeground
                    else java.awt.Color(150, 150, 150)
                    label.font       = label.font.deriveFont(java.awt.Font.ITALIC)
                }
                is DatabaseTableFetcher.TableEntry ->
                    label.text = "  ${value.tableName}  —  ${value.schema}" +
                            "  [${value.dataSourceName}]"
                else -> label.text = value?.toString() ?: ""
            }

            return label
        }
    }
}

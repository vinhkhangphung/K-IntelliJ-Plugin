package pvk.vn.util

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.Document

/** Calls [onChange] on any document mutation. */
fun Document.onAnyChange(onChange: () -> Unit) {
    addDocumentListener(object : DocumentListener {
        override fun insertUpdate(e: DocumentEvent?) = onChange()
        override fun removeUpdate(e: DocumentEvent?) = onChange()
        override fun changedUpdate(e: DocumentEvent?) = onChange()
    })
}

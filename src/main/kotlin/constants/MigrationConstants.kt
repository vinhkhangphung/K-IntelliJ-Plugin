package pvk.vn.constants

object MigrationConstants {
    const val MANUAL_ENTRY = "Type table name manually..."

    val OPERATIONS = arrayOf(
        "create   → Creating a new table",
        "add      → Adding column(s) or constraint(s)",
        "drop     → Dropping column(s), index or table",
        "alter    → Modifying existing column(s)",
        "insert   → Inserting seed/reference data",
        "update   → Updating existing data",
        "delete   → Deleting data",
        "index    → Adding or removing index",
        "rls      → Row Level Security policy",
        "custom   → Type your own"
    )

    const val DEFAULT_MAJOR_VERSION = "1"
    const val DEFAULT_MINOR_VERSION = "1"
    const val TIMESTAMP_PATTERN = "yyyyMMdd_HHmm"
    const val DIALOG_TITLE = "🛠 New Flyway Migration"
}

package pvk.vn.dialog

import pvk.vn.constants.MigrationConstants
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object MigrationPreviewBuilder {

    fun build(
        majorVersion: String,
        minorVersion: String,
        tableName: String,
        operation: String,
        description: String
    ): String {
        val timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern(MigrationConstants.TIMESTAMP_PATTERN))

        val major = majorVersion.ifEmpty { MigrationConstants.DEFAULT_MAJOR_VERSION }
        val minor = minorVersion.ifEmpty { MigrationConstants.DEFAULT_MINOR_VERSION }
        val table = tableName.ifEmpty { "<table>" }
        val op    = operation.ifEmpty { "<operation>" }
        val desc  = description.ifEmpty { "<description>" }

        return "V${major}_${minor}_${timestamp}__${table}_${op}_${desc}.sql"
    }
}

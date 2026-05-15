package pvk.vn

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object MigrationFileGenerator {

    fun create(
        project:      Project,
        directory:    VirtualFile,
        tableName:    String,
        operation:    String,
        description:  String,
        majorVersion: String = "1",
        minorVersion: String = "1"
    ) {
        val timestamp  = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
        val fileName   = "V${majorVersion}_${minorVersion}_${timestamp}" +
                "__${tableName}_${operation}_${description}.sql"
        val author     = System.getProperty("user.name")
        val createdAt  = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

        val content = """
            |-- ============================================================
            |-- Migration  : $fileName
            |-- Table      : $tableName
            |-- Operation  : $operation
            |-- Description: $description
            |-- Author     : $author
            |-- Created at : $createdAt
            |-- ============================================================
            |
            |-- TODO: Write your migration SQL below
            |
        """.trimMargin()

        WriteCommandAction.runWriteCommandAction(project) {
            val file = directory.createChildData(this, fileName)
            file.setBinaryContent(content.toByteArray())
        }
    }
}

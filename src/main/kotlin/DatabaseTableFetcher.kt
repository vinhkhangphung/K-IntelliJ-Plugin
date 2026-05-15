package pvk.vn

import com.intellij.database.model.DasTable
import com.intellij.database.psi.DbPsiFacade
import com.intellij.database.util.DasUtil
import com.intellij.openapi.project.Project

object DatabaseTableFetcher {

    data class TableEntry(
        val schema: String,
        val tableName: String,
        val dataSourceName: String
    ) {
        override fun toString() = "[$dataSourceName] $schema.$tableName"
    }

    fun fetchTables(project: Project): List<TableEntry> {
        return try {
            val facade = DbPsiFacade.getInstance(project)
            val results = mutableListOf<TableEntry>()

            for (dbDataSource in facade.dataSources) {
                if (!dbDataSource.isValid) continue

                val dataSourceName = dbDataSource.name

                DasUtil.getTables(dbDataSource).forEach { dasTable: DasTable ->
                    val schemaName = dasTable.dasParent?.name ?: "unknown"
                    results += TableEntry(
                        schema         = schemaName,
                        tableName      = dasTable.name,
                        dataSourceName = dataSourceName
                    )
                }
            }

            results.sortedWith(
                compareBy({ it.dataSourceName }, { it.schema }, { it.tableName })
            )
        } catch (e: Throwable) {
            // Fallback: dialog still opens in manual mode
            emptyList()
        }
    }
}

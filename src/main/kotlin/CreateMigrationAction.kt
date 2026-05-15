package pvk.vn

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import pvk.vn.dialog.CreateMigrationDialog

class CreateMigrationAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        // Use the right-clicked file/folder as the default suggestion
        val clickedFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val defaultDir = when {
            clickedFile?.isDirectory == true -> clickedFile.path
            clickedFile != null              -> clickedFile.parent?.path
            else                             -> null
        }

        val dialog = CreateMigrationDialog(project, defaultDirectory = defaultDir)
        if (dialog.showAndGet()) {
            val targetDir = dialog.selectedDirectory ?: return

            MigrationSettings.saveLastDirectory(targetDir.path)

            MigrationFileGenerator.create(
                project    = project,
                directory  = targetDir,
                tableName  = dialog.tableName,
                operation  = dialog.operation,
                description = dialog.description,
                majorVersion = dialog.majorVersion,
                minorVersion = dialog.minorVersion
            )
        }
    }

    override fun update(e: AnActionEvent) {
        // Optionally restrict visibility
        // e.presentation.isEnabledAndVisible = true
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}

package pvk.vn

import com.intellij.ide.util.PropertiesComponent

object MigrationSettings {
    private const val KEY_LAST_DIR     = "pvk.vn.migration.lastDirectory"
    private const val KEY_MAJOR_VERSION = "pvk.vn.migration.majorVersion"
    private const val KEY_MINOR_VERSION = "pvk.vn.migration.minorVersion"

    fun saveLastDirectory(path: String) {
        PropertiesComponent.getInstance().setValue(KEY_LAST_DIR, path)
    }

    fun loadLastDirectory(): String? {
        return PropertiesComponent.getInstance().getValue(KEY_LAST_DIR)
    }

    fun saveMajorVersion(value: String) {
        PropertiesComponent.getInstance().setValue(KEY_MAJOR_VERSION, value)
    }

    fun loadMajorVersion(): String {
        return PropertiesComponent.getInstance().getValue(KEY_MAJOR_VERSION, "1")
    }

    fun saveMinorVersion(value: String) {
        PropertiesComponent.getInstance().setValue(KEY_MINOR_VERSION, value)
    }

    fun loadMinorVersion(): String {
        return PropertiesComponent.getInstance().getValue(KEY_MINOR_VERSION, "1")
    }
}

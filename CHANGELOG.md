<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Flyway Migration Generator — Changelog

## [Unreleased]

## [1.0.0] — 2026-05-15

### Added
- **Migration dialog** — Create Flyway migration files directly from the IDE via
  `Tools → Create Flyway Migration` or right-click on any folder in the Project panel
- **DB-aware table picker** — Automatically fetches tables from active IntelliJ
  Database connections; falls back to manual entry when no connection is available
- **Fuzzy table search** — Filter tables using flexible fuzzy matching
  (e.g. `mess_sear` matches `message_search_index`), with results ranked by relevance
- **Operation selector** — Predefined operations (`create`, `add`, `drop`, `alter`,
  `insert`, `update`, `delete`, `index`, `rls`) plus a `custom` free-text option
- **Live filename preview** — See the generated filename update in real time as
  fields are filled in
- **Versioning support** — Configurable major/minor version numbers with
  persistence between sessions
- **Advanced panel** — Collapsible section for version configuration, keeping the
  UI clean for common use
- **Output folder picker** — Browse and select target directory; last used folder
  is remembered across sessions
- **Keyboard navigation** — Arrow keys and Enter navigate the table dropdown
  while typing in the search field
- **Validation** — Inline error messages for missing or invalid fields before
  file generation

### Notes
- Requires an active IntelliJ Database connection for automatic table discovery;
  manual table name entry is always available as fallback
- Compatible with IntelliJ-based IDEs (IntelliJ IDEA, DataGrip, etc.)


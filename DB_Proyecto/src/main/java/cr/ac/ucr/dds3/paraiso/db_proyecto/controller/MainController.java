package cr.ac.ucr.dds3.paraiso.db_proyecto.controller;

import cr.ac.ucr.dds3.paraiso.db_proyecto.model.config.DatabaseConfig;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.Database;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.ForeignKeyOption;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.ExecutionResult;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.QueryData;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.QueryDefinition;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.QueryResult;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.TableRepository;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.ColumnDefinition;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.ForeignKeyDefinition;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.TableCatalog;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.TableDefinition;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.service.CascadeWarningService;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.service.SqlErrorTranslator;
import cr.ac.ucr.dds3.paraiso.db_proyecto.view.MainView;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainController {

    private final MainView view;
    private final TableRepository repository = new TableRepository();

    public MainController(MainView view) {
        this.view = view;
    }

    public void initialize() {
        view.initialize(TableCatalog.getAllTables(), QueryData.getQueries());
        if (!Database.testConnection()) {
            view.showConnectionDialog(Database.getConfig(), this::saveConnectionSettings);
        }
    }

    public List<Map<String, Object>> loadTableRows(String tableName) throws SQLException {
        return repository.findAll(TableCatalog.getTable(tableName));
    }

    public List<ForeignKeyOption> loadForeignKeyOptions(String tableName, String columnName) throws SQLException {
        TableDefinition table = TableCatalog.getTable(tableName);
        ForeignKeyDefinition foreignKey = table.findForeignKey(columnName);
        if (foreignKey == null) {
            throw new IllegalArgumentException("Column is not a foreign key: " + columnName);
        }
        ColumnDefinition column = table.findColumn(columnName);
        return repository.loadForeignKeyOptions(foreignKey, column.nullable());
    }

    public void insertRecord(String tableName, Map<String, String> rawValues) {
        executeWriteOperation(() -> {
            TableDefinition table = TableCatalog.getTable(tableName);
            Map<String, Object> values = parseValues(table, rawValues);
            repository.insert(table, values);
            view.showInfo("Registro insertado correctamente.");
            view.refreshCurrentTable();
        });
    }

    public void updateRecord(String tableName, Map<String, String> rawValues, Map<String, String> originalKeys) {
        executeWriteOperation(() -> {
            TableDefinition table = TableCatalog.getTable(tableName);
            String warning = CascadeWarningService.buildUpdateWarning(tableName);
            if (warning != null && !view.confirmAction("Confirmar actualización", warning)) {
                return;
            }

            Map<String, Object> values = parseValues(table, rawValues);
            Map<String, Object> primaryKeys = parsePrimaryKeys(table, originalKeys);
            repository.update(table, primaryKeys, values);
            view.showInfo("Registro actualizado correctamente.");
            view.refreshCurrentTable();
        });
    }

    public void deleteRecord(String tableName, Map<String, String> originalKeys) {
        executeWriteOperation(() -> {
            TableDefinition table = TableCatalog.getTable(tableName);
            String warning = CascadeWarningService.buildDeleteWarning(tableName);
            if (warning != null && !view.confirmAction("Confirmar eliminación", warning)) {
                return;
            }
            if (!view.confirmAction("Eliminar registro", "¿Desea eliminar el registro seleccionado?")) {
                return;
            }

            Map<String, Object> primaryKeys = parsePrimaryKeys(table, originalKeys);
            repository.delete(table, primaryKeys);
            view.showInfo("Registro eliminado correctamente.");
            view.refreshCurrentTable();
        });
    }

    public QueryResult runQuery(QueryDefinition queryDefinition) {
        try {
            return repository.executeQuery(queryDefinition.sql());
        } catch (SQLException exception) {
            view.showError(SqlErrorTranslator.translate(exception));
            return new QueryResult(List.of(), List.of());
        }
    }

    public QueryResult runFreeQuery(String sql) throws SQLException {
        return repository.executeQuery(sql);
    }

    public ExecutionResult runFreeUpdate(String sql) throws SQLException {
        return repository.executeUpdate(sql);
    }

    public boolean saveConnectionSettings(DatabaseConfig config) {
        Database.configure(config);
        if (Database.testConnection()) {
            view.showInfo("Conexión establecida correctamente.");
            view.refreshCurrentTable();
            return true;
        }
        view.showError("No se pudo conectar. Verifique URL, usuario y contraseña.");
        return false;
    }

    private Map<String, Object> parseValues(TableDefinition table, Map<String, String> rawValues) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (ColumnDefinition column : table.columns()) {
            values.put(column.name(), TableRepository.parseValue(column, rawValues.get(column.name())));
        }
        return values;
    }

    private Map<String, Object> parsePrimaryKeys(TableDefinition table, Map<String, String> originalKeys) {
        Map<String, Object> primaryKeys = new LinkedHashMap<>();
        for (String primaryKeyColumn : table.primaryKeyColumns()) {
            ColumnDefinition column = table.findColumn(primaryKeyColumn);
            primaryKeys.put(primaryKeyColumn, TableRepository.parseValue(column, originalKeys.get(primaryKeyColumn)));
        }
        return primaryKeys;
    }

    private void executeWriteOperation(WriteOperation operation) {
        try {
            operation.run();
        } catch (IllegalArgumentException exception) {
            view.showError(exception.getMessage());
        } catch (SQLException exception) {
            view.showError(SqlErrorTranslator.translate(exception));
        }
    }

    @FunctionalInterface
    private interface WriteOperation {
        void run() throws SQLException;
    }
}

package cr.ac.ucr.dds3.paraiso.db_proyecto.model.data;

import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.ColumnDefinition;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.ColumnType;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.ForeignKeyDefinition;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.TableDefinition;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TableRepository {

    public List<Map<String, Object>> findAll(TableDefinition table) throws SQLException {
        String columnList = table.columns().stream()
                .map(ColumnDefinition::name)
                .collect(Collectors.joining(", "));

        String sql = "SELECT " + columnList + " FROM " + table.tableName() + " ORDER BY 1";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return readRows(resultSet);
        }
    }

    public void insert(TableDefinition table, Map<String, Object> values) throws SQLException {
        List<ColumnDefinition> columns = table.columns();
        String columnNames = columns.stream().map(ColumnDefinition::name).collect(Collectors.joining(", "));
        String placeholders = columns.stream().map(column -> "?").collect(Collectors.joining(", "));
        String sql = "INSERT INTO " + table.tableName() + " (" + columnNames + ") VALUES (" + placeholders + ")";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindInsertOrUpdate(statement, columns, values);
            statement.executeUpdate();
        }
    }

    public void update(
            TableDefinition table,
            Map<String, Object> primaryKeyValues,
            Map<String, Object> values
    ) throws SQLException {
        List<ColumnDefinition> nonPrimaryColumns = table.columns().stream()
                .filter(column -> !column.primaryKey())
                .toList();
        List<ColumnDefinition> primaryColumns = table.columns().stream()
                .filter(ColumnDefinition::primaryKey)
                .toList();

        String setClause = nonPrimaryColumns.stream()
                .map(column -> column.name() + " = ?")
                .collect(Collectors.joining(", "));
        String whereClause = primaryColumns.stream()
                .map(column -> column.name() + " = ?")
                .collect(Collectors.joining(" AND "));
        String sql = "UPDATE " + table.tableName() + " SET " + setClause + " WHERE " + whereClause;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            for (ColumnDefinition column : nonPrimaryColumns) {
                bindValue(statement, index++, values.get(column.name()), column);
            }
            for (ColumnDefinition column : primaryColumns) {
                bindValue(statement, index++, primaryKeyValues.get(column.name()), column);
            }
            statement.executeUpdate();
        }
    }

    public void delete(TableDefinition table, Map<String, Object> primaryKeyValues) throws SQLException {
        List<ColumnDefinition> primaryColumns = table.columns().stream()
                .filter(ColumnDefinition::primaryKey)
                .toList();
        String whereClause = primaryColumns.stream()
                .map(column -> column.name() + " = ?")
                .collect(Collectors.joining(" AND "));
        String sql = "DELETE FROM " + table.tableName() + " WHERE " + whereClause;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            for (ColumnDefinition column : primaryColumns) {
                bindValue(statement, index++, primaryKeyValues.get(column.name()), column);
            }
            statement.executeUpdate();
        }
    }

    public List<ForeignKeyOption> loadForeignKeyOptions(
            ForeignKeyDefinition foreignKey,
            boolean includeNullOption
    ) throws SQLException {
        String sql = "SELECT " + foreignKey.referencedColumn() + " AS fk_value, "
                + foreignKey.displayExpression() + " AS fk_label FROM "
                + foreignKey.referencedTable() + " ORDER BY 2";

        List<ForeignKeyOption> options = new ArrayList<>();
        if (includeNullOption) {
            options.add(new ForeignKeyOption(null, "(Ninguno)"));
        }

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                options.add(new ForeignKeyOption(
                        resultSet.getObject("fk_value"),
                        resultSet.getString("fk_label")
                ));
            }
        }

        return options;
    }

    public QueryResult executeQuery(String sql) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (int index = 1; index <= columnCount; index++) {
                columnNames.add(metaData.getColumnLabel(index));
            }

            List<Object[]> rows = new ArrayList<>();
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int index = 1; index <= columnCount; index++) {
                    row[index - 1] = resultSet.getObject(index);
                }
                rows.add(row);
            }
            return new QueryResult(columnNames, rows);
        }
    }

    public ExecutionResult executeUpdate(String sql) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int affectedRows = statement.executeUpdate();
            return new ExecutionResult(affectedRows);
        }
    }

    public static Object parseValue(ColumnDefinition column, String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            if (column.nullable()) {
                return null;
            }
            throw new IllegalArgumentException("El campo '" + column.label() + "' es obligatorio.");
        }

        return switch (column.type()) {
            case STRING -> rawValue.trim();
            case INTEGER -> Integer.valueOf(rawValue.trim());
            case DECIMAL -> new BigDecimal(rawValue.trim());
            case DATE -> Date.valueOf(LocalDate.parse(rawValue.trim()));
            case DATETIME -> Timestamp.valueOf(LocalDateTime.parse(rawValue.trim().replace(' ', 'T')));
            case TIME -> Time.valueOf(LocalTime.parse(rawValue.trim()));
        };
    }

    public static String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().toString().replace('T', ' ');
        }
        if (value instanceof Date date) {
            return date.toLocalDate().toString();
        }
        if (value instanceof Time time) {
            return time.toLocalTime().toString();
        }
        return String.valueOf(value);
    }

    private void bindInsertOrUpdate(
            PreparedStatement statement,
            List<ColumnDefinition> columns,
            Map<String, Object> values
    ) throws SQLException {
        int index = 1;
        for (ColumnDefinition column : columns) {
            bindValue(statement, index++, values.get(column.name()), column);
        }
    }

    private void bindValue(
            PreparedStatement statement,
            int index,
            Object value,
            ColumnDefinition column
    ) throws SQLException {
        if (value == null) {
            statement.setNull(index, sqlType(column.type()));
            return;
        }

        switch (column.type()) {
            case STRING -> statement.setString(index, value.toString());
            case INTEGER -> statement.setInt(index, ((Number) value).intValue());
            case DECIMAL -> statement.setBigDecimal(index, (BigDecimal) value);
            case DATE -> {
                if (value instanceof Date date) {
                    statement.setDate(index, date);
                } else {
                    statement.setDate(index, Date.valueOf(LocalDate.parse(value.toString())));
                }
            }
            case DATETIME -> {
                if (value instanceof Timestamp timestamp) {
                    statement.setTimestamp(index, timestamp);
                } else {
                    statement.setTimestamp(index, Timestamp.valueOf(
                            LocalDateTime.parse(value.toString().replace(' ', 'T'))));
                }
            }
            case TIME -> {
                if (value instanceof Time time) {
                    statement.setTime(index, time);
                } else {
                    statement.setTime(index, Time.valueOf(LocalTime.parse(value.toString())));
                }
            }
            default -> statement.setObject(index, value);
        }
    }

    private int sqlType(ColumnType type) {
        return switch (type) {
            case STRING -> Types.VARCHAR;
            case INTEGER -> Types.INTEGER;
            case DECIMAL -> Types.DECIMAL;
            case DATE -> Types.DATE;
            case DATETIME -> Types.TIMESTAMP;
            case TIME -> Types.TIME;
        };
    }

    private List<Map<String, Object>> readRows(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<String> columnNames = new ArrayList<>();
        for (int index = 1; index <= columnCount; index++) {
            columnNames.add(metaData.getColumnLabel(index));
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (String columnName : columnNames) {
                row.put(columnName, resultSet.getObject(columnName));
            }
            rows.add(row);
        }
        return rows;
    }
}

package cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata;

import java.util.List;

public record TableDefinition(
        String tableName,
        String displayName,
        List<ColumnDefinition> columns,
        List<ForeignKeyDefinition> foreignKeys
) {
    public List<String> primaryKeyColumns() {
        return columns.stream()
                .filter(ColumnDefinition::primaryKey)
                .map(ColumnDefinition::name)
                .toList();
    }

    public ColumnDefinition findColumn(String columnName) {
        return columns.stream()
                .filter(column -> column.name().equals(columnName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown column: " + columnName));
    }

    public ForeignKeyDefinition findForeignKey(String columnName) {
        return foreignKeys.stream()
                .filter(fk -> fk.columnName().equals(columnName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return displayName;
    }
}

package cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata;

import java.util.List;

public record ColumnDefinition(
        String name,
        String label,
        ColumnType type,
        boolean primaryKey,
        boolean nullable,
        List<String> allowedValues
) {
    public ColumnDefinition(String name, String label, ColumnType type, boolean primaryKey, boolean nullable) {
        this(name, label, type, primaryKey, nullable, List.of());
    }

    public boolean hasAllowedValues() {
        return allowedValues != null && !allowedValues.isEmpty();
    }
}

package cr.ac.ucr.dds3.paraiso.db_proyecto.model.service;

import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.ForeignKeyDefinition;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.ReferentialAction;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.TableCatalog;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.TableDefinition;

import java.util.ArrayList;
import java.util.List;

public final class CascadeWarningService {

    private CascadeWarningService() {
    }

    public static String buildDeleteWarning(String tableName) {
        return buildWarning(tableName, "eliminar", ReferentialAction.CASCADE, ReferentialAction.SET_NULL);
    }

    public static String buildUpdateWarning(String tableName) {
        return buildWarning(tableName, "actualizar", ReferentialAction.CASCADE, ReferentialAction.CASCADE);
    }

    private static String buildWarning(
            String tableName,
            String actionLabel,
            ReferentialAction... actions
    ) {
        List<String> warnings = new ArrayList<>();

        for (TableDefinition childTable : TableCatalog.getAllTables()) {
            for (ForeignKeyDefinition foreignKey : childTable.foreignKeys()) {
                if (!foreignKey.referencedTable().equals(tableName)) {
                    continue;
                }

                ReferentialAction onDelete = foreignKey.onDelete();
                ReferentialAction onUpdate = foreignKey.onUpdate();
                ReferentialAction relevant = actionLabel.equals("eliminar") ? onDelete : onUpdate;

                for (ReferentialAction action : actions) {
                    if (relevant == action) {
                        warnings.add(describeEffect(childTable.displayName(), foreignKey.columnName(), relevant));
                        break;
                    }
                }
            }
        }

        if (warnings.isEmpty()) {
            return null;
        }

        return "Si " + actionLabel + " este registro en '" + TableCatalog.getTable(tableName).displayName() + "':\n- "
                + String.join("\n- ", warnings);
    }

    private static String describeEffect(String childTable, String columnName, ReferentialAction action) {
        return switch (action) {
            case CASCADE -> "Se " + (columnName.contains("Cod") ? "eliminarán/actualizarán" : "afectarán")
                    + " registros relacionados en '" + childTable + "' (ON DELETE/UPDATE CASCADE).";
            case SET_NULL -> "El campo '" + columnName + "' en '" + childTable
                    + "' quedará en NULL (ON DELETE/UPDATE SET NULL).";
            case RESTRICT -> "La operación puede ser bloqueada por registros en '" + childTable + "'.";
        };
    }
}

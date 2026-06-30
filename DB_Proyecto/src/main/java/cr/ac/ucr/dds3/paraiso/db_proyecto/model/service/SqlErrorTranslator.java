package cr.ac.ucr.dds3.paraiso.db_proyecto.model.service;

import java.sql.SQLException;

public final class SqlErrorTranslator {

    private SqlErrorTranslator() {
    }

    public static String translate(SQLException exception) {
        int errorCode = exception.getErrorCode();
        String message = exception.getMessage() == null ? "" : exception.getMessage();

        return switch (errorCode) {
            case 515 -> "Un campo obligatorio no puede quedar vacío (NOT NULL). Revise los datos ingresados.";
            case 2627, 2601 -> "Ya existe un registro con esa clave o valor único. " + extractDetail(message);
            case 547 -> "El registro no cumple con una restricción de integridad (Llave Foránea o CHECK). "
                    + extractDetail(message);
            case 245, 8114 -> "El tipo de dato ingresado no es válido para uno de los campos. "
                    + extractDetail(message);
            case 208 -> "La tabla o vista no existe. Ejecute sql/sqlserver/database_extensions_sqlserver.sql si falta la vista o auditoría.";
            default -> "Error de base de datos (" + errorCode + "): " + message;
        };
    }

    private static String extractDetail(String message) {
        if (message.isBlank()) {
            return "";
        }
        return " Detalle: " + message;
    }
}

package cr.ac.ucr.dds3.paraiso.db_proyecto.model.service;

import java.sql.SQLException;

public final class SqlErrorTranslator {

    private SqlErrorTranslator() {
    }

    public static String translate(SQLException exception) {
        int errorCode = exception.getErrorCode();
        String message = exception.getMessage() == null ? "" : exception.getMessage();

        return switch (errorCode) {
            case 1048 -> "Un campo obligatorio no puede quedar vacío (NOT NULL). Revise los datos ingresados.";
            case 1062 -> "Ya existe un registro con esa clave o valor único. " + extractDetail(message);
            case 1451 -> "No se puede eliminar o modificar este registro porque otras tablas dependen de él (borrado restringido). "
                    + extractDetail(message);
            case 1452 -> "La referencia seleccionada no existe en la tabla relacionada. Verifique las listas desplegables."
                    + extractDetail(message);
            case 3819, 4025 -> "Los datos incumplen una restricción CHECK de la base de datos. "
                    + extractDetail(message);
            case 1264, 1366, 1292 -> "El tipo de dato ingresado no es válido para uno de los campos. "
                    + extractDetail(message);
            case 1146 -> "La tabla o vista no existe. Ejecute sql/database_extensions.sql si falta la vista o auditoría.";
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

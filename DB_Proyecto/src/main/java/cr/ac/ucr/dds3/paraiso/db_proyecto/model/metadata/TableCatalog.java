package cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TableCatalog {

    private static final Map<String, TableDefinition> TABLES = new LinkedHashMap<>();

    static {
        register(new TableDefinition("provincia", "Provincia", List.of(
                col("CodProvincia", "Código", ColumnType.INTEGER, true, false),
                col("NombreProvincia", "Nombre", ColumnType.STRING, false, false)
        ), List.of()));

        register(new TableDefinition("ambito", "Ámbito", List.of(
                col("CodAmbito", "Código", ColumnType.INTEGER, true, false),
                col("Nombre", "Nombre", ColumnType.STRING, false, false),
                col("Descripcion", "Descripción", ColumnType.STRING, false, true)
        ), List.of()));

        register(new TableDefinition("canton", "Cantón", List.of(
                col("CodCanton", "Código", ColumnType.INTEGER, true, false),
                col("NombreCanton", "Nombre", ColumnType.STRING, false, false),
                col("CodProvincia", "Provincia", ColumnType.INTEGER, false, false)
        ), List.of(
                fk("CodProvincia", "provincia", "CodProvincia", "NombreProvincia",
                        ReferentialAction.RESTRICT, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("distrito", "Distrito", List.of(
                col("CodDistrito", "Código", ColumnType.INTEGER, true, false),
                col("NombreDistrito", "Nombre", ColumnType.STRING, false, false),
                col("CodCanton", "Cantón", ColumnType.INTEGER, false, false)
        ), List.of(
                fk("CodCanton", "canton", "CodCanton", "NombreCanton",
                        ReferentialAction.RESTRICT, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("cliente", "Cliente", List.of(
                col("Cedula", "Cédula", ColumnType.STRING, true, false),
                col("Nombre", "Nombre", ColumnType.STRING, false, false),
                col("Apellidos", "Apellidos", ColumnType.STRING, false, false),
                col("Correo_Electronico", "Correo", ColumnType.STRING, false, true),
                col("Fecha_Ingreso", "Fecha ingreso", ColumnType.DATE, false, false),
                colEnum("Tipo_Cliente", "Tipo cliente", ColumnType.STRING, false, false,
                        "Físico", "Jurídico"),
                col("CodDistrito", "Distrito", ColumnType.INTEGER, false, false),
                col("Barrio", "Barrio", ColumnType.STRING, false, false),
                col("Calle", "Calle", ColumnType.STRING, false, false),
                col("Avenida", "Avenida", ColumnType.STRING, false, false)
        ), List.of(
                fk("CodDistrito", "distrito", "CodDistrito", "NombreDistrito",
                        ReferentialAction.RESTRICT, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("tipo_promocion", "Tipo promoción", List.of(
                col("CodTipoPromo", "Código", ColumnType.INTEGER, true, false),
                col("Nombre", "Nombre", ColumnType.STRING, false, false),
                col("Descripcion", "Descripción", ColumnType.STRING, false, true),
                col("PorcentajeMaximo", "Porcentaje máximo", ColumnType.DECIMAL, false, false)
        ), List.of()));

        register(new TableDefinition("plan_tarifario", "Plan tarifario", List.of(
                col("CodPlan", "Código", ColumnType.INTEGER, true, false),
                col("Nombre", "Nombre", ColumnType.STRING, false, false),
                col("Descripcion", "Descripción", ColumnType.STRING, false, true),
                col("Cuota_Mensual", "Cuota mensual", ColumnType.DECIMAL, false, false),
                col("GB_Incluidos", "GB incluidos", ColumnType.INTEGER, false, false),
                col("Minutos_Incluidos", "Minutos incluidos", ColumnType.INTEGER, false, false),
                col("Mensajes_Incluidos", "Mensajes incluidos", ColumnType.INTEGER, false, false),
                col("Costo_Exceso_Consumo", "Costo exceso", ColumnType.DECIMAL, false, false),
                col("CategoriaDescripcion", "Categoría", ColumnType.STRING, false, false),
                col("VelocidadMaxima", "Velocidad máxima", ColumnType.STRING, false, false)
        ), List.of()));

        register(new TableDefinition("paquete", "Paquete", List.of(
                col("CodPaquete", "Código", ColumnType.INTEGER, true, false),
                col("Nombre", "Nombre", ColumnType.STRING, false, false),
                col("Precio", "Precio", ColumnType.DECIMAL, false, false),
                col("CapacidadGB", "Capacidad GB", ColumnType.INTEGER, false, false),
                col("Vigencia", "Vigencia (días)", ColumnType.INTEGER, false, false)
        ), List.of()));

        register(new TableDefinition("servicio", "Servicio", List.of(
                col("CodServicio", "Código", ColumnType.INTEGER, true, false),
                col("Nombre", "Nombre", ColumnType.STRING, false, false),
                col("Descripcion", "Descripción", ColumnType.STRING, false, true),
                col("CostoMensual", "Costo mensual", ColumnType.DECIMAL, false, false),
                col("Categoria", "Categoría", ColumnType.STRING, false, false)
        ), List.of()));

        register(new TableDefinition("franja_horaria", "Franja horaria", List.of(
                col("Id_Franja", "ID franja", ColumnType.INTEGER, true, false),
                col("Descripcion", "Descripción", ColumnType.STRING, false, false),
                col("HoraInicio", "Hora inicio", ColumnType.TIME, false, false),
                col("HoraFin", "Hora fin", ColumnType.TIME, false, false),
                col("DiasAplicacion", "Días aplicación", ColumnType.STRING, false, false),
                col("IdFranjaIncompatible", "Franja incompatible", ColumnType.INTEGER, false, true)
        ), List.of(
                fk("IdFranjaIncompatible", "franja_horaria", "Id_Franja", "Descripcion",
                        ReferentialAction.SET_NULL, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("promocion", "Promoción", List.of(
                col("CodPromocion", "Código", ColumnType.INTEGER, true, false),
                col("Nombre", "Nombre", ColumnType.STRING, false, false),
                col("Descripcion", "Descripción", ColumnType.STRING, false, true),
                col("Fecha_Inicio", "Fecha inicio", ColumnType.DATE, false, false),
                col("Fecha_Finalizacion", "Fecha fin", ColumnType.DATE, false, false),
                col("Porcentaje_Descuento", "Porcentaje descuento", ColumnType.DECIMAL, false, false),
                col("CodTipoPromocion", "Tipo promoción", ColumnType.INTEGER, false, false)
        ), List.of(
                fk("CodTipoPromocion", "tipo_promocion", "CodTipoPromo", "Nombre",
                        ReferentialAction.RESTRICT, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("linea_movil", "Línea móvil", List.of(
                col("Numero_Telefono", "Número", ColumnType.STRING, true, false),
                colEnum("Tipo_Linea", "Tipo línea", ColumnType.STRING, false, false, "Prepago", "Postpago"),
                colEnum("Tecnologia", "Tecnología", ColumnType.STRING, false, false, "3G", "4G", "5G"),
                col("Fecha_Activacion", "Fecha activación", ColumnType.DATE, false, false),
                colEnum("Estado_Linea", "Estado", ColumnType.STRING, false, false, "Activa", "Suspendida", "Inactiva"),
                colEnum("Tipo_SIM", "Tipo SIM", ColumnType.STRING, false, false, "Física", "eSIM"),
                col("Cedula_Cliente", "Cliente", ColumnType.STRING, false, false)
        ), List.of(
                fk("Cedula_Cliente", "cliente", "Cedula", "CONCAT(Nombre, ' ', Apellidos)",
                        ReferentialAction.RESTRICT, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("factura", "Factura", List.of(
                col("Numero_Factura", "Número factura", ColumnType.INTEGER, true, false),
                col("Fecha_Emision", "Fecha emisión", ColumnType.DATE, false, false),
                col("Fecha_Vencimiento", "Fecha vencimiento", ColumnType.DATE, false, false),
                col("Monto_Base", "Monto base", ColumnType.DECIMAL, false, false),
                col("Impuestos", "Impuestos", ColumnType.DECIMAL, false, false),
                col("Descuentos_Aplicados", "Descuentos", ColumnType.DECIMAL, false, false),
                col("Puntos_Redimidos", "Puntos redimidos", ColumnType.INTEGER, false, false),
                col("Monto_Final", "Monto final", ColumnType.DECIMAL, false, false),
                col("Fecha_Pago", "Fecha pago", ColumnType.DATE, false, true),
                colEnum("Estado_Pago", "Estado pago", ColumnType.STRING, false, false,
                        "Cancelada", "Pendiente", "Vencida"),
                col("Cedula_Cliente", "Cliente", ColumnType.STRING, false, false)
        ), List.of(
                fk("Cedula_Cliente", "cliente", "Cedula", "CONCAT(Nombre, ' ', Apellidos)",
                        ReferentialAction.RESTRICT, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("concepto_cobro", "Concepto de cobro", List.of(
                col("Numero_Factura", "Factura", ColumnType.INTEGER, true, false),
                col("IdConcepto", "ID concepto", ColumnType.INTEGER, true, false),
                col("Descripcion", "Descripción", ColumnType.STRING, false, false),
                col("Monto", "Monto", ColumnType.DECIMAL, false, false)
        ), List.of(
                fk("Numero_Factura", "factura", "Numero_Factura", "CAST(Numero_Factura AS CHAR)",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("consumo", "Consumo", List.of(
                col("IdConsumo", "ID consumo", ColumnType.INTEGER, true, false),
                col("Numero_Telefono", "Línea", ColumnType.STRING, false, false),
                col("Fecha_Hora_Inicio", "Inicio", ColumnType.DATETIME, false, false),
                col("Fecha_Hora_Fin", "Fin", ColumnType.DATETIME, false, false),
                colEnum("Tipo_Consumo", "Tipo consumo", ColumnType.STRING, false, false, "Datos", "Mensajes", "Voz"),
                col("Cantidad_Consumida", "Cantidad", ColumnType.DECIMAL, false, false),
                col("Costo_Calculado", "Costo", ColumnType.DECIMAL, false, false),
                col("CodAmbito", "Ámbito", ColumnType.INTEGER, false, false),
                col("IdDescuento_Franja", "Franja descuento", ColumnType.INTEGER, false, true)
        ), List.of(
                fk("Numero_Telefono", "linea_movil", "Numero_Telefono", "Numero_Telefono",
                        ReferentialAction.RESTRICT, ReferentialAction.CASCADE),
                fk("CodAmbito", "ambito", "CodAmbito", "Nombre",
                        ReferentialAction.RESTRICT, ReferentialAction.CASCADE),
                fk("IdDescuento_Franja", "franja_horaria", "Id_Franja", "Descripcion",
                        ReferentialAction.SET_NULL, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("llamada_voz", "Llamada de voz", List.of(
                col("IdConsumo", "Consumo", ColumnType.INTEGER, true, false),
                col("Telefono_Destino", "Teléfono destino", ColumnType.STRING, false, false),
                col("Duracion_Segundos", "Duración (seg)", ColumnType.INTEGER, false, false),
                col("Pais_Destino", "País destino", ColumnType.STRING, false, false)
        ), List.of(
                fk("IdConsumo", "consumo", "IdConsumo", "CAST(IdConsumo AS CHAR)",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("historico_planes", "Histórico planes", List.of(
                col("IdHistorico", "ID histórico", ColumnType.INTEGER, true, false),
                col("Numero_Telefono", "Línea", ColumnType.STRING, false, false),
                col("CodPlan", "Plan", ColumnType.INTEGER, false, false),
                col("Fecha_Inicio", "Fecha inicio", ColumnType.DATE, false, false),
                col("Fecha_Fin", "Fecha fin", ColumnType.DATE, false, true)
        ), List.of(
                fk("Numero_Telefono", "linea_movil", "Numero_Telefono", "Numero_Telefono",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE),
                fk("CodPlan", "plan_tarifario", "CodPlan", "Nombre",
                        ReferentialAction.RESTRICT, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("historico_paquete", "Histórico paquetes", List.of(
                col("Numero_Telefono", "Línea", ColumnType.STRING, true, false),
                col("CodPaquete", "Paquete", ColumnType.INTEGER, true, false),
                col("Fecha_Compra", "Fecha compra", ColumnType.DATETIME, true, false),
                col("Fecha_Vencimiento", "Fecha vencimiento", ColumnType.DATETIME, false, false)
        ), List.of(
                fk("Numero_Telefono", "linea_movil", "Numero_Telefono", "Numero_Telefono",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE),
                fk("CodPaquete", "paquete", "CodPaquete", "Nombre",
                        ReferentialAction.RESTRICT, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("linea_service", "Servicios por línea", List.of(
                col("Numero_Telefono", "Línea", ColumnType.STRING, true, false),
                col("CodServicio", "Servicio", ColumnType.INTEGER, true, false)
        ), List.of(
                fk("Numero_Telefono", "linea_movil", "Numero_Telefono", "Numero_Telefono",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE),
                fk("CodServicio", "servicio", "CodServicio", "Nombre",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("telefono_cliente", "Teléfonos de contacto", List.of(
                col("Cedula_Cliente", "Cliente", ColumnType.STRING, true, false),
                col("Telefono_Contacto", "Teléfono contacto", ColumnType.STRING, true, false)
        ), List.of(
                fk("Cedula_Cliente", "cliente", "Cedula", "CONCAT(Nombre, ' ', Apellidos)",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("puntos_fidelizacion", "Puntos fidelización", List.of(
                col("IdRegistro_Puntos", "ID registro", ColumnType.INTEGER, true, false),
                col("Cedula_Cliente", "Cliente", ColumnType.STRING, false, false),
                col("Fecha_Transaccion", "Fecha transacción", ColumnType.DATE, false, false),
                col("Puntos_Redimidos_Monto", "Monto redimido", ColumnType.DECIMAL, false, false),
                col("Saldo_Disponible", "Saldo disponible", ColumnType.INTEGER, false, false)
        ), List.of(
                fk("Cedula_Cliente", "cliente", "Cedula", "CONCAT(Nombre, ' ', Apellidos)",
                        ReferentialAction.RESTRICT, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("promo_incompatible", "Promos incompatibles", List.of(
                col("CodPromocion1", "Promoción 1", ColumnType.INTEGER, true, false),
                col("CodPromocion2", "Promoción 2", ColumnType.INTEGER, true, false)
        ), List.of(
                fk("CodPromocion1", "promocion", "CodPromocion", "Nombre",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE),
                fk("CodPromocion2", "promocion", "CodPromocion", "Nombre",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("promo_paquete", "Promo - paquete", List.of(
                col("CodPromocion", "Promoción", ColumnType.INTEGER, true, false),
                col("CodPaquete", "Paquete", ColumnType.INTEGER, true, false)
        ), List.of(
                fk("CodPromocion", "promocion", "CodPromocion", "Nombre",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE),
                fk("CodPaquete", "paquete", "CodPaquete", "Nombre",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("promo_plan", "Promo - plan", List.of(
                col("CodPromocion", "Promoción", ColumnType.INTEGER, true, false),
                col("CodPlan", "Plan", ColumnType.INTEGER, true, false)
        ), List.of(
                fk("CodPromocion", "promocion", "CodPromocion", "Nombre",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE),
                fk("CodPlan", "plan_tarifario", "CodPlan", "Nombre",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE)
        )));

        register(new TableDefinition("promo_servicio", "Promo - servicio", List.of(
                col("CodPromocion", "Promoción", ColumnType.INTEGER, true, false),
                col("CodServicio", "Servicio", ColumnType.INTEGER, true, false)
        ), List.of(
                fk("CodPromocion", "promocion", "CodPromocion", "Nombre",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE),
                fk("CodServicio", "servicio", "CodServicio", "Nombre",
                        ReferentialAction.CASCADE, ReferentialAction.CASCADE)
        )));
    }

    private TableCatalog() {
    }

    public static List<TableDefinition> getAllTables() {
        return new ArrayList<>(TABLES.values());
    }

    public static TableDefinition getTable(String tableName) {
        TableDefinition table = TABLES.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Unknown table: " + tableName);
        }
        return table;
    }

    private static void register(TableDefinition table) {
        TABLES.put(table.tableName(), table);
    }

    private static ColumnDefinition col(
            String name,
            String label,
            ColumnType type,
            boolean primaryKey,
            boolean nullable
    ) {
        return new ColumnDefinition(name, label, type, primaryKey, nullable);
    }

    private static ColumnDefinition colEnum(
            String name,
            String label,
            ColumnType type,
            boolean primaryKey,
            boolean nullable,
            String... values
    ) {
        return new ColumnDefinition(name, label, type, primaryKey, nullable, List.of(values));
    }

    private static ForeignKeyDefinition fk(
            String columnName,
            String referencedTable,
            String referencedColumn,
            String displayExpression,
            ReferentialAction onDelete,
            ReferentialAction onUpdate
    ) {
        return new ForeignKeyDefinition(columnName, referencedTable, referencedColumn,
                displayExpression, onDelete, onUpdate);
    }
}

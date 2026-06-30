package cr.ac.ucr.dds3.paraiso.db_proyecto.model.data;

import java.util.List;

public final class QueryData {

    private QueryData() {
    }

    public static List<QueryDefinition> getQueries() {
        return List.of(
                new QueryDefinition(
                        "Vista: resumen cliente-facturación",
                        "Consulta la vista vw_resumen_cliente_facturacion (punto 3.iii).",
                        """
                                SELECT Cedula, NombreCompleto, TipoCliente, NombreDistrito,
                                       NombreCanton, NombreProvincia, TotalLineas, TotalFacturas,
                                       MontoTotalFacturado
                                FROM vw_resumen_cliente_facturacion
                                ORDER BY MontoTotalFacturado DESC
                                """
                ),
                new QueryDefinition(
                        "Pistas de auditoría",
                        "Registros generados por los triggers de auditoría.",
                        """
                                SELECT IdAuditoria, NombreTabla, TipoOperacion, LlaveRegistro,
                                       DetalleCambio, FechaHora
                                FROM pista_auditoria
                                ORDER BY FechaHora DESC
                                """
                ),
                new QueryDefinition(
                        "Consumos con cliente y ámbito",
                        "JOIN entre consumo, línea móvil, cliente y ámbito.",
                        """
                                SELECT c.IdConsumo, lm.Numero_Telefono, CONCAT(cl.Nombre, ' ', cl.Apellidos) AS Cliente,
                                       c.Tipo_Consumo, c.Cantidad_Consumida, c.Costo_Calculado, a.Nombre AS Ambito
                                FROM consumo c
                                INNER JOIN linea_movil lm ON c.Numero_Telefono = lm.Numero_Telefono
                                INNER JOIN cliente cl ON lm.Cedula_Cliente = cl.Cedula
                                INNER JOIN ambito a ON c.CodAmbito = a.CodAmbito
                                ORDER BY c.Fecha_Hora_Inicio DESC
                                """
                ),
                new QueryDefinition(
                        "Facturas pendientes por cliente",
                        "JOIN factura-cliente con filtro de estado pendiente o vencida.",
                        """
                                SELECT f.Numero_Factura, f.Fecha_Emision, f.Fecha_Vencimiento, f.Monto_Final,
                                       f.Estado_Pago, cl.Cedula, CONCAT(cl.Nombre, ' ', cl.Apellidos) AS Cliente
                                FROM factura f
                                INNER JOIN cliente cl ON f.Cedula_Cliente = cl.Cedula
                                WHERE f.Estado_Pago IN ('Pendiente', 'Vencida')
                                ORDER BY f.Fecha_Vencimiento
                                """
                ),
                new QueryDefinition(
                        "Promociones activas con tipo y planes",
                        "LEFT JOIN entre promoción, tipo y planes asociados.",
                        """
                                SELECT p.CodPromocion, p.Nombre AS Promocion, tp.Nombre AS TipoPromocion,
                                       pl.Nombre AS PlanAsociado, p.Porcentaje_Descuento
                                FROM promocion p
                                INNER JOIN tipo_promocion tp ON p.CodTipoPromocion = tp.CodTipoPromo
                                LEFT JOIN promo_plan pp ON p.CodPromocion = pp.CodPromocion
                                LEFT JOIN plan_tarifario pl ON pp.CodPlan = pl.CodPlan
                                WHERE CONVERT(date, GETDATE()) BETWEEN p.Fecha_Inicio AND p.Fecha_Finalizacion
                                ORDER BY p.Nombre, pl.Nombre
                                """
                ),
                new QueryDefinition(
                        "Total facturado y promedio por cliente",
                        "Agregación con SUM y AVG (operadores vistos en clase).",
                        """
                                SELECT cl.Cedula,
                                       CONCAT(cl.Nombre, ' ', cl.Apellidos) AS Cliente,
                                       COUNT(f.Numero_Factura) AS CantidadFacturas,
                                       SUM(f.Monto_Final) AS TotalFacturado,
                                       AVG(f.Monto_Final) AS PromedioFactura
                                FROM cliente cl
                                LEFT JOIN factura f ON cl.Cedula = f.Cedula_Cliente
                                GROUP BY cl.Cedula, cl.Nombre, cl.Apellidos
                                ORDER BY TotalFacturado DESC
                                """
                ),
                new QueryDefinition(
                        "Clientes con ubicación completa",
                        "JOIN geográfico provincia-cantón-distrito con UCASE (operador no visto en clase).",
                        """
                                SELECT cl.Cedula,
                                       UPPER(CONCAT(cl.Nombre, ' ', cl.Apellidos)) AS ClienteMayusculas,
                                       p.NombreProvincia, ca.NombreCanton, d.NombreDistrito
                                FROM cliente cl
                                INNER JOIN distrito d ON cl.CodDistrito = d.CodDistrito
                                INNER JOIN canton ca ON d.CodCanton = ca.CodCanton
                                INNER JOIN provincia p ON ca.CodProvincia = p.CodProvincia
                                ORDER BY p.NombreProvincia, ca.NombreCanton, d.NombreDistrito
                                """
                )
        );
    }
}

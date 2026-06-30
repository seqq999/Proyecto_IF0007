USE bd_proyecto;
GO

-- ---------------------------------------------------------------------------
-- Audit trail table
-- ---------------------------------------------------------------------------
CREATE TABLE pista_auditoria (
    IdAuditoria BIGINT IDENTITY(1,1) NOT NULL,
    NombreTabla VARCHAR(64) NOT NULL,
    TipoOperacion VARCHAR(20) NOT NULL CHECK (TipoOperacion IN ('INSERT', 'UPDATE', 'DELETE')),
    LlaveRegistro VARCHAR(255) NOT NULL,
    DetalleCambio VARCHAR(MAX),
    FechaHora DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    PRIMARY KEY (IdAuditoria)
);
GO

-- ---------------------------------------------------------------------------
-- View: client billing and line summary (punto 3.iii)
-- ---------------------------------------------------------------------------
DROP VIEW IF EXISTS vw_resumen_cliente_facturacion;
GO
CREATE VIEW vw_resumen_cliente_facturacion AS
SELECT
    c.Cedula,
    CONCAT(c.Nombre, ' ', c.Apellidos) AS NombreCompleto,
    UPPER(c.Tipo_Cliente) AS TipoCliente,
    d.NombreDistrito,
    ca.NombreCanton,
    p.NombreProvincia,
    COUNT(DISTINCT lm.Numero_Telefono) AS TotalLineas,
    COUNT(DISTINCT f.Numero_Factura) AS TotalFacturas,
    ISNULL(SUM(f.Monto_Final), 0) AS MontoTotalFacturado
FROM cliente c
INNER JOIN distrito d ON c.CodDistrito = d.CodDistrito
INNER JOIN canton ca ON d.CodCanton = ca.CodCanton
INNER JOIN provincia p ON ca.CodProvincia = p.CodProvincia
LEFT JOIN linea_movil lm ON c.Cedula = lm.Cedula_Cliente
LEFT JOIN factura f ON c.Cedula = f.Cedula_Cliente
GROUP BY c.Cedula, c.Nombre, c.Apellidos, c.Tipo_Cliente,
         d.NombreDistrito, ca.NombreCanton, p.NombreProvincia;
GO

-- ---------------------------------------------------------------------------
-- Audit triggers (sample on main transactional tables)
-- ---------------------------------------------------------------------------

CREATE TRIGGER trg_cliente_ai ON cliente AFTER INSERT AS
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    SELECT 'cliente', 'INSERT', i.Cedula, CONCAT('Cliente creado: ', i.Nombre, ' ', i.Apellidos)
    FROM inserted i;
END;
GO

CREATE TRIGGER trg_cliente_au ON cliente AFTER UPDATE AS
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    SELECT 'cliente', 'UPDATE', i.Cedula, CONCAT('Cliente actualizado: ', d.Nombre, ' -> ', i.Nombre)
    FROM inserted i
    INNER JOIN deleted d ON i.Cedula = d.Cedula;
END;
GO

CREATE TRIGGER trg_cliente_ad ON cliente AFTER DELETE AS
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    SELECT 'cliente', 'DELETE', d.Cedula, CONCAT('Cliente eliminado: ', d.Nombre, ' ', d.Apellidos)
    FROM deleted d;
END;
GO

CREATE TRIGGER trg_factura_ai ON factura AFTER INSERT AS
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    SELECT 'factura', 'INSERT', CAST(i.Numero_Factura AS VARCHAR(255)), CONCAT('Factura emitida por ', i.Monto_Final)
    FROM inserted i;
END;
GO

CREATE TRIGGER trg_factura_au ON factura AFTER UPDATE AS
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    SELECT 'factura', 'UPDATE', CAST(i.Numero_Factura AS VARCHAR(255)), CONCAT('Estado: ', d.Estado_Pago, ' -> ', i.Estado_Pago)
    FROM inserted i
    INNER JOIN deleted d ON i.Numero_Factura = d.Numero_Factura;
END;
GO

CREATE TRIGGER trg_factura_ad ON factura AFTER DELETE AS
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    SELECT 'factura', 'DELETE', CAST(d.Numero_Factura AS VARCHAR(255)), CONCAT('Factura eliminada, monto ', d.Monto_Final)
    FROM deleted d;
END;
GO

CREATE TRIGGER trg_consumo_ai ON consumo AFTER INSERT AS
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    SELECT 'consumo', 'INSERT', CAST(i.IdConsumo AS VARCHAR(255)), CONCAT('Consumo ', i.Tipo_Consumo, ' linea ', i.Numero_Telefono)
    FROM inserted i;
END;
GO

CREATE TRIGGER trg_consumo_au ON consumo AFTER UPDATE AS
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    SELECT 'consumo', 'UPDATE', CAST(i.IdConsumo AS VARCHAR(255)), CONCAT('Costo: ', d.Costo_Calculado, ' -> ', i.Costo_Calculado)
    FROM inserted i
    INNER JOIN deleted d ON i.IdConsumo = d.IdConsumo;
END;
GO

CREATE TRIGGER trg_consumo_ad ON consumo AFTER DELETE AS
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    SELECT 'consumo', 'DELETE', CAST(d.IdConsumo AS VARCHAR(255)), CONCAT('Consumo eliminado linea ', d.Numero_Telefono)
    FROM deleted d;
END;
GO

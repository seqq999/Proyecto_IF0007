-- Extensions for DB_Proyecto application (view, audit trail, triggers)
-- Run after importing bd_proyecto.sql

USE bd_proyecto;

-- ---------------------------------------------------------------------------
-- Audit trail table
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pista_auditoria (
    IdAuditoria BIGINT NOT NULL AUTO_INCREMENT,
    NombreTabla VARCHAR(64) NOT NULL,
    TipoOperacion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    LlaveRegistro VARCHAR(255) NOT NULL,
    DetalleCambio TEXT,
    FechaHora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (IdAuditoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ---------------------------------------------------------------------------
-- View: client billing and line summary (punto 3.iii)
-- ---------------------------------------------------------------------------
DROP VIEW IF EXISTS vw_resumen_cliente_facturacion;
CREATE VIEW vw_resumen_cliente_facturacion AS
SELECT
    c.Cedula,
    CONCAT(c.Nombre, ' ', c.Apellidos) AS NombreCompleto,
    UCASE(c.Tipo_Cliente) AS TipoCliente,
    d.NombreDistrito,
    ca.NombreCanton,
    p.NombreProvincia,
    COUNT(DISTINCT lm.Numero_Telefono) AS TotalLineas,
    COUNT(DISTINCT f.Numero_Factura) AS TotalFacturas,
    COALESCE(SUM(f.Monto_Final), 0) AS MontoTotalFacturado
FROM cliente c
INNER JOIN distrito d ON c.CodDistrito = d.CodDistrito
INNER JOIN canton ca ON d.CodCanton = ca.CodCanton
INNER JOIN provincia p ON ca.CodProvincia = p.CodProvincia
LEFT JOIN linea_movil lm ON c.Cedula = lm.Cedula_Cliente
LEFT JOIN factura f ON c.Cedula = f.Cedula_Cliente
GROUP BY c.Cedula, c.Nombre, c.Apellidos, c.Tipo_Cliente,
         d.NombreDistrito, ca.NombreCanton, p.NombreProvincia;

-- ---------------------------------------------------------------------------
-- Audit triggers (sample on main transactional tables)
-- ---------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_cliente_ai;
DROP TRIGGER IF EXISTS trg_cliente_au;
DROP TRIGGER IF EXISTS trg_cliente_ad;
DROP TRIGGER IF EXISTS trg_factura_ai;
DROP TRIGGER IF EXISTS trg_factura_au;
DROP TRIGGER IF EXISTS trg_factura_ad;
DROP TRIGGER IF EXISTS trg_consumo_ai;
DROP TRIGGER IF EXISTS trg_consumo_au;
DROP TRIGGER IF EXISTS trg_consumo_ad;

DELIMITER $$

CREATE TRIGGER trg_cliente_ai AFTER INSERT ON cliente FOR EACH ROW
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    VALUES ('cliente', 'INSERT', NEW.Cedula, CONCAT('Cliente creado: ', NEW.Nombre, ' ', NEW.Apellidos));
END$$

CREATE TRIGGER trg_cliente_au AFTER UPDATE ON cliente FOR EACH ROW
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    VALUES ('cliente', 'UPDATE', NEW.Cedula, CONCAT('Cliente actualizado: ', OLD.Nombre, ' -> ', NEW.Nombre));
END$$

CREATE TRIGGER trg_cliente_ad AFTER DELETE ON cliente FOR EACH ROW
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    VALUES ('cliente', 'DELETE', OLD.Cedula, CONCAT('Cliente eliminado: ', OLD.Nombre, ' ', OLD.Apellidos));
END$$

CREATE TRIGGER trg_factura_ai AFTER INSERT ON factura FOR EACH ROW
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    VALUES ('factura', 'INSERT', CAST(NEW.Numero_Factura AS CHAR), CONCAT('Factura emitida por ', NEW.Monto_Final));
END$$

CREATE TRIGGER trg_factura_au AFTER UPDATE ON factura FOR EACH ROW
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    VALUES ('factura', 'UPDATE', CAST(NEW.Numero_Factura AS CHAR), CONCAT('Estado: ', OLD.Estado_Pago, ' -> ', NEW.Estado_Pago));
END$$

CREATE TRIGGER trg_factura_ad AFTER DELETE ON factura FOR EACH ROW
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    VALUES ('factura', 'DELETE', CAST(OLD.Numero_Factura AS CHAR), CONCAT('Factura eliminada, monto ', OLD.Monto_Final));
END$$

CREATE TRIGGER trg_consumo_ai AFTER INSERT ON consumo FOR EACH ROW
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    VALUES ('consumo', 'INSERT', CAST(NEW.IdConsumo AS CHAR), CONCAT('Consumo ', NEW.Tipo_Consumo, ' linea ', NEW.Numero_Telefono));
END$$

CREATE TRIGGER trg_consumo_au AFTER UPDATE ON consumo FOR EACH ROW
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    VALUES ('consumo', 'UPDATE', CAST(NEW.IdConsumo AS CHAR), CONCAT('Costo: ', OLD.Costo_Calculado, ' -> ', NEW.Costo_Calculado));
END$$

CREATE TRIGGER trg_consumo_ad AFTER DELETE ON consumo FOR EACH ROW
BEGIN
    INSERT INTO pista_auditoria (NombreTabla, TipoOperacion, LlaveRegistro, DetalleCambio)
    VALUES ('consumo', 'DELETE', CAST(OLD.IdConsumo AS CHAR), CONCAT('Consumo eliminado linea ', OLD.Numero_Telefono));
END$$

DELIMITER ;

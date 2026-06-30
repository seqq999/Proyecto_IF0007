CREATE DATABASE bd_proyecto;
GO
USE bd_proyecto;
GO

-- --------------------------------------------------------
-- Tabla `ambito`
-- --------------------------------------------------------
CREATE TABLE ambito (
  CodAmbito INT NOT NULL,
  Nombre VARCHAR(50) NOT NULL,
  Descripcion VARCHAR(150) DEFAULT NULL,
  PRIMARY KEY (CodAmbito)
);

INSERT INTO ambito (CodAmbito, Nombre, Descripcion) VALUES
(1, 'Nacional', 'Consumos dentro de Costa Rica'),
(2, 'Internacional', 'Consumos hacia el exterior del país'),
(3, 'Roaming', 'Consumos de datos fuera del país'),
(4, 'Comunidad', 'Llamadas a números de la misma red'),
(5, 'Especial', 'Números de emergencia o servicios del Estado');

-- --------------------------------------------------------
-- Tabla `provincia`
-- --------------------------------------------------------
CREATE TABLE provincia (
  CodProvincia INT NOT NULL,
  NombreProvincia VARCHAR(50) NOT NULL,
  PRIMARY KEY (CodProvincia)
);

INSERT INTO provincia (CodProvincia, NombreProvincia) VALUES
(1, 'San José'),
(2, 'Alajuela'),
(3, 'Cartago'),
(4, 'Heredia'),
(5, 'Guanacaste');

-- --------------------------------------------------------
-- Tabla `canton`
-- --------------------------------------------------------
CREATE TABLE canton (
  CodCanton INT NOT NULL,
  NombreCanton VARCHAR(50) NOT NULL,
  CodProvincia INT NOT NULL,
  PRIMARY KEY (CodCanton),
  CONSTRAINT canton_ibfk_1 FOREIGN KEY (CodProvincia) REFERENCES provincia (CodProvincia) ON UPDATE CASCADE
);

INSERT INTO canton (CodCanton, NombreCanton, CodProvincia) VALUES
(101, 'Central San José', 1),
(201, 'Central Alajuela', 2),
(301, 'Central Cartago', 3),
(306, 'Paraíso', 3),
(401, 'Central Heredia', 4);

-- --------------------------------------------------------
-- Tabla `distrito`
-- --------------------------------------------------------
CREATE TABLE distrito (
  CodDistrito INT NOT NULL,
  NombreDistrito VARCHAR(50) NOT NULL,
  CodCanton INT NOT NULL,
  PRIMARY KEY (CodDistrito),
  CONSTRAINT distrito_ibfk_1 FOREIGN KEY (CodCanton) REFERENCES canton (CodCanton) ON UPDATE CASCADE
);

INSERT INTO distrito (CodDistrito, NombreDistrito, CodCanton) VALUES
(10101, 'Carmen', 101),
(20101, 'Central Alajuela', 201),
(30101, 'Oriental', 301),
(30601, 'Paraíso Centro', 306),
(40101, 'Mercedes', 401);

-- --------------------------------------------------------
-- Tabla `cliente`
-- --------------------------------------------------------
CREATE TABLE cliente (
  Cedula VARCHAR(20) NOT NULL,
  Nombre VARCHAR(50) NOT NULL,
  Apellidos VARCHAR(50) NOT NULL,
  Correo_Electronico VARCHAR(100) DEFAULT NULL,
  Fecha_Ingreso DATE NOT NULL,
  Tipo_Cliente VARCHAR(30) NOT NULL CHECK (Tipo_Cliente in ('Físico','Jurídico')),
  CodDistrito INT NOT NULL,
  Barrio VARCHAR(100) NOT NULL,
  Calle VARCHAR(100) NOT NULL,
  Avenida VARCHAR(100) NOT NULL,
  PRIMARY KEY (Cedula),
  UNIQUE (Correo_Electronico),
  CONSTRAINT cliente_ibfk_1 FOREIGN KEY (CodDistrito) REFERENCES distrito (CodDistrito) ON UPDATE CASCADE
);

INSERT INTO cliente (Cedula, Nombre, Apellidos, Correo_Electronico, Fecha_Ingreso, Tipo_Cliente, CodDistrito, Barrio, Calle, Avenida) VALUES
('1-1111-1111', 'Luis Arnaldo', 'Gómez Quirós', 'luis.gomez@ucr.ac.cr', '2025-01-15', 'Físico', 30601, 'La Granja', 'Calle de la Estación', 'Avenida Central'),
('2-2222-2222', 'Sebastián', 'Araya Mora', 'sebast.araya@gmail.com', '2025-03-20', 'Físico', 30101, 'Los Ángeles', 'Calle 4', 'Avenida 2'),
('3-3333-3333', 'Konny', 'Agüero Rojas', 'konny.aguero@hotmail.com', '2024-06-10', 'Físico', 10101, 'Amón', 'Calle 9', 'Avenida 11'),
('4-4444-4444', 'Corporación Tica', 'S.A.', 'contacto@cortica.cr', '2023-11-01', 'Jurídico', 20101, 'El Centro', 'Calle Principal', 'Avenida 5'),
('5-5555-5555', 'María Elena', 'Solano Vargas', 'maria.solano@outlook.com', '2026-02-14', 'Físico', 40101, 'Santa Inés', 'Calle Los Almendros', 'Avenida 8');

-- --------------------------------------------------------
-- Tabla `telefono_cliente`
-- --------------------------------------------------------
CREATE TABLE telefono_cliente (
  Cedula_Cliente VARCHAR(20) NOT NULL,
  Telefono_Contacto VARCHAR(15) NOT NULL,
  PRIMARY KEY (Cedula_Cliente, Telefono_Contacto),
  CONSTRAINT telefono_cliente_ibfk_1 FOREIGN KEY (Cedula_Cliente) REFERENCES cliente (Cedula) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO telefono_cliente (Cedula_Cliente, Telefono_Contacto) VALUES
('1-1111-1111', '2574-1234'),
('1-1111-1111', '8888-0101'),
('2-2222-2222', '7777-0202'),
('3-3333-3333', '6666-0303'),
('4-4444-4444', '2222-4040');

-- --------------------------------------------------------
-- Tabla `linea_movil`
-- --------------------------------------------------------
CREATE TABLE linea_movil (
  Numero_Telefono VARCHAR(15) NOT NULL,
  Tipo_Linea VARCHAR(20) NOT NULL CHECK (Tipo_Linea in ('Prepago','Postpago')),
  Tecnologia VARCHAR(10) NOT NULL CHECK (Tecnologia in ('3G','4G','5G')),
  Fecha_Activacion DATE NOT NULL,
  Estado_Linea VARCHAR(20) NOT NULL CHECK (Estado_Linea in ('Activa','Suspendida','Inactiva')),
  Tipo_SIM VARCHAR(10) NOT NULL CHECK (Tipo_SIM in ('Física','eSIM')),
  Cedula_Cliente VARCHAR(20) NOT NULL,
  PRIMARY KEY (Numero_Telefono),
  CONSTRAINT linea_movil_ibfk_1 FOREIGN KEY (Cedula_Cliente) REFERENCES cliente (Cedula) ON UPDATE CASCADE
);

INSERT INTO linea_movil (Numero_Telefono, Tipo_Linea, Tecnologia, Fecha_Activacion, Estado_Linea, Tipo_SIM, Cedula_Cliente) VALUES
('6666-6663', 'Prepago', '4G', '2024-06-11', 'Activa', 'Física', '3-3333-3333'),
('7222-2225', 'Prepago', '5G', '2026-02-15', 'Inactiva', 'eSIM', '5-5555-5555'),
('7777-7772', 'Postpago', '5G', '2025-03-21', 'Activa', 'eSIM', '2-2222-2222'),
('8888-8881', 'Postpago', '4G', '2025-01-16', 'Activa', 'Física', '1-1111-1111'),
('8888-9994', 'Postpago', '4G', '2023-11-02', 'Suspendida', 'Física', '4-4444-4444');

-- --------------------------------------------------------
-- Tabla `servicio`
-- --------------------------------------------------------
CREATE TABLE servicio (
  CodServicio INT NOT NULL,
  Nombre VARCHAR(50) NOT NULL,
  Descripcion VARCHAR(150) DEFAULT NULL,
  CostoMensual DECIMAL(10,2) NOT NULL CHECK (CostoMensual >= 0),
  Categoria VARCHAR(50) NOT NULL,
  PRIMARY KEY (CodServicio)
);

INSERT INTO servicio (CodServicio, Nombre, Descripcion, CostoMensual, Categoria) VALUES
(601, 'Respaldo en la Nube', 'Sincronización de fotos y contactos', 1200.00, 'Valor Agregado'),
(602, 'Seguro contra Robo', 'Cobertura parcial por pérdida del dispositivo', 2500.00, 'Protección'),
(603, 'Streaming Música', 'Acceso premium a plataforma de música', 3000.00, 'Entretenimiento'),
(604, 'Llamadas Larga Distancia', 'Tarifa preferencial internacional', 1500.00, 'Voz'),
(605, 'ID de Llamada Oculto', 'Privacidad al realizar llamadas', 500.00, 'Privacidad');

-- --------------------------------------------------------
-- Tabla `linea_service`
-- --------------------------------------------------------
CREATE TABLE linea_service (
  Numero_Telefono VARCHAR(15) NOT NULL,
  CodServicio INT NOT NULL,
  PRIMARY KEY (Numero_Telefono, CodServicio),
  CONSTRAINT linea_service_ibfk_1 FOREIGN KEY (Numero_Telefono) REFERENCES linea_movil (Numero_Telefono) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT linea_service_ibfk_2 FOREIGN KEY (CodServicio) REFERENCES servicio (CodServicio) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO linea_service (Numero_Telefono, CodServicio) VALUES
('6666-6663', 605),
('7777-7772', 602),
('8888-8881', 601),
('8888-8881', 603),
('8888-9994', 604);

-- --------------------------------------------------------
-- Tabla `paquete`
-- --------------------------------------------------------
CREATE TABLE paquete (
  CodPaquete INT NOT NULL,
  Nombre VARCHAR(50) NOT NULL,
  Precio DECIMAL(10,2) NOT NULL CHECK (Precio >= 0),
  CapacidadGB INT NOT NULL CHECK (CapacidadGB >= 0),
  Vigencia INT NOT NULL CHECK (Vigencia > 0),
  PRIMARY KEY (CodPaquete)
);

INSERT INTO paquete (CodPaquete, Nombre, Precio, CapacidadGB, Vigencia) VALUES
(501, 'Paquete WhatsApp Ilimitado', 1500.00, 5, 7),
(502, 'Paquete Fin de Semana', 2500.00, 10, 2),
(503, 'Paquete Giga Nocturno', 1000.00, 20, 1),
(504, 'Paquete Teletrabajo', 5000.00, 30, 15),
(505, 'Paquete Viajero América', 12000.00, 50, 30);

-- --------------------------------------------------------
-- Tabla `historico_paquete`
-- --------------------------------------------------------
CREATE TABLE historico_paquete (
  Numero_Telefono VARCHAR(15) NOT NULL,
  CodPaquete INT NOT NULL,
  Fecha_Compra DATETIME NOT NULL,
  Fecha_Vencimiento DATETIME NOT NULL,
  PRIMARY KEY (Numero_Telefono, CodPaquete, Fecha_Compra),
  CONSTRAINT historico_paquete_ibfk_1 FOREIGN KEY (Numero_Telefono) REFERENCES linea_movil (Numero_Telefono) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT historico_paquete_ibfk_2 FOREIGN KEY (CodPaquete) REFERENCES paquete (CodPaquete) ON UPDATE CASCADE
);

INSERT INTO historico_paquete (Numero_Telefono, CodPaquete, Fecha_Compra, Fecha_Vencimiento) VALUES
('6666-6663', 503, '2026-06-10 23:00:00', '2026-06-11 23:00:00'),
('7222-2225', 505, '2026-06-01 12:00:00', '2026-07-01 12:00:00'),
('7777-7772', 502, '2026-06-05 17:00:00', '2026-06-07 17:00:00'),
('8888-8881', 501, '2026-06-01 08:00:00', '2026-06-08 08:00:00'),
('8888-9994', 504, '2026-05-01 09:30:00', '2026-05-16 09:30:00');

-- --------------------------------------------------------
-- Tabla `plan_tarifario`
-- --------------------------------------------------------
CREATE TABLE plan_tarifario (
  CodPlan INT NOT NULL,
  Nombre VARCHAR(50) NOT NULL,
  Descripcion VARCHAR(150) DEFAULT NULL,
  Cuota_Mensual DECIMAL(10,2) NOT NULL CHECK (Cuota_Mensual >= 0),
  GB_Incluidos INT NOT NULL CHECK (GB_Incluidos >= 0),
  Minutos_Incluidos INT NOT NULL CHECK (Minutos_Incluidos >= 0),
  Mensajes_Incluidos INT NOT NULL CHECK (Mensajes_Incluidos >= 0),
  Costo_Exceso_Consumo DECIMAL(10,2) NOT NULL CHECK (Costo_Exceso_Consumo >= 0),
  CategoriaDescripcion VARCHAR(100) NOT NULL,
  VelocidadMaxima VARCHAR(50) NOT NULL,
  PRIMARY KEY (CodPlan)
);

INSERT INTO plan_tarifario (CodPlan, Nombre, Descripcion, Cuota_Mensual, GB_Incluidos, Minutos_Incluidos, Mensajes_Incluidos, Costo_Exceso_Consumo, CategoriaDescripcion, VelocidadMaxima) VALUES
(801, 'Plan Universitario', 'Plan especial con redes sociales gratis', 10500.00, 15, 200, 500, 15.00, 'Postpago Estudiantil', '40 Mbps'),
(802, 'Plan Conectado Plus', 'Ideal para navegación moderada', 15000.00, 25, 300, 1000, 12.00, 'Postpago Estándar', '50 Mbps'),
(803, 'Plan Empresarial Pro', 'Minutos ilimitados y alta capacidad', 35000.00, 100, 9999, 9999, 10.00, 'Postpago Corporativo', '100 Mbps'),
(804, 'Plan Básico Control', 'Para consumo medido controlado', 7500.00, 5, 100, 200, 20.00, 'Postpago Controlado', '20 Mbps'),
(805, 'Plan Ultra Navegación 5G', 'Máxima velocidad disponible', 28000.00, 80, 500, 2000, 11.00, 'Postpago Premium 5G', '250 Mbps');

-- --------------------------------------------------------
-- Tabla `historico_planes`
-- --------------------------------------------------------
CREATE TABLE historico_planes (
  IdHistorico INT NOT NULL,
  Numero_Telefono VARCHAR(15) NOT NULL,
  CodPlan INT NOT NULL,
  Fecha_Inicio DATE NOT NULL,
  Fecha_Fin DATE DEFAULT NULL,
  PRIMARY KEY (IdHistorico),
  CONSTRAINT historico_planes_ibfk_1 FOREIGN KEY (Numero_Telefono) REFERENCES linea_movil (Numero_Telefono) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT historico_planes_ibfk_2 FOREIGN KEY (CodPlan) REFERENCES plan_tarifario (CodPlan) ON UPDATE CASCADE
);

INSERT INTO historico_planes (IdHistorico, Numero_Telefono, CodPlan, Fecha_Inicio, Fecha_Fin) VALUES
(1, '8888-8881', 802, '2025-01-16', NULL),
(2, '7777-7772', 801, '2025-03-21', '2025-12-31'),
(3, '7777-7772', 805, '2026-01-01', NULL),
(4, '6666-6663', 804, '2024-06-11', NULL),
(5, '8888-9994', 803, '2023-11-02', NULL);

-- --------------------------------------------------------
-- Tabla `factura`
-- --------------------------------------------------------
CREATE TABLE factura (
  Numero_Factura INT NOT NULL,
  Fecha_Emision DATE NOT NULL,
  Fecha_Vencimiento DATE NOT NULL,
  Monto_Base DECIMAL(10,2) NOT NULL CHECK (Monto_Base >= 0),
  Impuestos DECIMAL(10,2) NOT NULL CHECK (Impuestos >= 0),
  Descuentos_Aplicados DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (Descuentos_Aplicados >= 0),
  Puntos_Redimidos INT NOT NULL DEFAULT 0 CHECK (Puntos_Redimidos >= 0),
  Monto_Final DECIMAL(10,2) NOT NULL CHECK (Monto_Final >= 0),
  Fecha_Pago DATE DEFAULT NULL,
  Estado_Pago VARCHAR(20) NOT NULL CHECK (Estado_Pago in ('Cancelada','Pendiente','Vencida')),
  Cedula_Cliente VARCHAR(20) NOT NULL,
  PRIMARY KEY (Numero_Factura),
  CONSTRAINT factura_ibfk_1 FOREIGN KEY (Cedula_Cliente) REFERENCES cliente (Cedula) ON UPDATE CASCADE
);

INSERT INTO factura (Numero_Factura, Fecha_Emision, Fecha_Vencimiento, Monto_Base, Impuestos, Descuentos_Aplicados, Puntos_Redimidos, Monto_Final, Fecha_Pago, Estado_Pago, Cedula_Cliente) VALUES
(1001, '2026-05-01', '2026-05-15', 15000.00, 1950.00, 0.00, 0, 16950.00, '2026-05-10', 'Cancelada', '1-1111-1111'),
(1002, '2026-05-01', '2026-05-15', 10500.00, 1365.00, 2100.00, 100, 9765.00, '2026-05-14', 'Cancelada', '2-2222-2222'),
(1003, '2026-06-01', '2026-06-15', 35000.00, 4550.00, 0.00, 0, 39550.00, NULL, 'Pendiente', '4-4444-4444'),
(1004, '2026-06-01', '2026-06-15', 7500.00, 975.00, 500.00, 0, 7975.00, NULL, 'Vencida', '3-3333-3333'),
(1005, '2026-06-05', '2026-06-20', 28000.00, 3640.00, 1000.00, 0, 30640.00, '2026-06-18', 'Cancelada', '5-5555-5555');

-- --------------------------------------------------------
-- Tabla `concepto_cobro`
-- --------------------------------------------------------
CREATE TABLE concepto_cobro (
  Numero_Factura INT NOT NULL,
  IdConcepto INT NOT NULL,
  Descripcion VARCHAR(150) NOT NULL,
  Monto DECIMAL(10,2) NOT NULL CHECK (Monto >= 0),
  PRIMARY KEY (Numero_Factura, IdConcepto),
  CONSTRAINT concepto_cobro_ibfk_1 FOREIGN KEY (Numero_Factura) REFERENCES factura (Numero_Factura) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO concepto_cobro (Numero_Factura, IdConcepto, Descripcion, Monto) VALUES
(1001, 1, 'Mensualidad Plan Conectado Plus', 15000.00),
(1002, 1, 'Mensualidad Plan Universitario', 10500.00),
(1003, 1, 'Mensualidad Plan Empresarial Pro', 35000.00),
(1004, 1, 'Mensualidad Plan Básico Control', 7500.00),
(1005, 1, 'Mensualidad Plan Ultra 5G', 28000.00);

-- --------------------------------------------------------
-- Tabla `franja_horaria`
-- --------------------------------------------------------
CREATE TABLE franja_horaria (
  Id_Franja INT NOT NULL,
  Descripcion VARCHAR(100) NOT NULL,
  HoraInicio TIME NOT NULL,
  HoraFin TIME NOT NULL,
  DiasAplicacion VARCHAR(50) NOT NULL,
  IdFranjaIncompatible INT DEFAULT NULL,
  PRIMARY KEY (Id_Franja),
  CONSTRAINT franja_horaria_ibfk_1 FOREIGN KEY (IdFranjaIncompatible) REFERENCES franja_horaria (Id_Franja)
);

INSERT INTO franja_horaria (Id_Franja, Descripcion, HoraInicio, HoraFin, DiasAplicacion, IdFranjaIncompatible) VALUES
(10, 'Franja Diurna', '06:00:00', '18:00:00', 'Lunes a Viernes', NULL),
(20, 'Franja Nocturna', '18:00:01', '05:59:59', 'Lunes a Viernes', 10),
(30, 'Franja Fin de Semana', '00:00:00', '23:59:59', 'Sábado y Domingo', NULL),
(40, 'Súper Nocturna', '00:00:00', '04:00:00', 'Todos los días', NULL),
(50, 'Franja Feriados', '00:00:00', '23:59:59', 'Días Festivos', NULL);

-- UPDATE self referencing constraint
UPDATE franja_horaria SET IdFranjaIncompatible = 20 WHERE Id_Franja = 10;

-- --------------------------------------------------------
-- Tabla `consumo`
-- --------------------------------------------------------
CREATE TABLE consumo (
  IdConsumo INT NOT NULL,
  Numero_Telefono VARCHAR(15) NOT NULL,
  Fecha_Hora_Inicio DATETIME NOT NULL,
  Fecha_Hora_Fin DATETIME NOT NULL,
  Tipo_Consumo VARCHAR(30) NOT NULL CHECK (Tipo_Consumo in ('Datos','Mensajes','Voz')),
  Cantidad_Consumida DECIMAL(10,2) NOT NULL CHECK (Cantidad_Consumida >= 0),
  Costo_Calculado DECIMAL(10,2) NOT NULL CHECK (Costo_Calculado >= 0),
  CodAmbito INT NOT NULL,
  IdDescuento_Franja INT DEFAULT NULL,
  PRIMARY KEY (IdConsumo),
  CONSTRAINT consumo_ibfk_1 FOREIGN KEY (Numero_Telefono) REFERENCES linea_movil (Numero_Telefono) ON UPDATE CASCADE,
  CONSTRAINT consumo_ibfk_2 FOREIGN KEY (CodAmbito) REFERENCES ambito (CodAmbito) ON UPDATE CASCADE,
  CONSTRAINT consumo_ibfk_3 FOREIGN KEY (IdDescuento_Franja) REFERENCES franja_horaria (Id_Franja) ON UPDATE CASCADE
);

INSERT INTO consumo (IdConsumo, Numero_Telefono, Fecha_Hora_Inicio, Fecha_Hora_Fin, Tipo_Consumo, Cantidad_Consumida, Costo_Calculado, CodAmbito, IdDescuento_Franja) VALUES
(50001, '8888-8881', '2026-06-10 10:15:00', '2026-06-10 10:18:30', 'Voz', 3.50, 45.00, 1, 10),
(50002, '7777-7772', '2026-06-11 22:00:00', '2026-06-11 23:45:00', 'Datos', 450.25, 0.00, 1, 20),
(50003, '6666-6663', '2026-06-12 14:05:00', '2026-06-12 14:06:15', 'Voz', 1.25, 150.00, 2, 10),
(50004, '8888-9994', '2026-06-13 08:00:00', '2026-06-13 08:01:00', 'Mensajes', 1.00, 0.00, 1, 10),
(50005, '7222-2225', '2026-06-14 19:30:00', '2026-06-14 19:38:20', 'Voz', 8.33, 110.00, 4, 20);

-- --------------------------------------------------------
-- Tabla `llamada_voz`
-- --------------------------------------------------------
CREATE TABLE llamada_voz (
  IdConsumo INT NOT NULL,
  Telefono_Destino VARCHAR(15) NOT NULL,
  Duracion_Segundos INT NOT NULL CHECK (Duracion_Segundos >= 0),
  Pais_Destino VARCHAR(50) NOT NULL,
  PRIMARY KEY (IdConsumo),
  CONSTRAINT llamada_voz_ibfk_1 FOREIGN KEY (IdConsumo) REFERENCES consumo (IdConsumo) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO llamada_voz (IdConsumo, Telefono_Destino, Duracion_Segundos, Pais_Destino) VALUES
(50001, '2574-0000', 210, 'Costa Rica'),
(50003, '001-305-555', 75, 'Estados Unidos'),
(50005, '8333-3333', 500, 'Costa Rica');

-- --------------------------------------------------------
-- Tabla `tipo_promocion`
-- --------------------------------------------------------
CREATE TABLE tipo_promocion (
  CodTipoPromo INT NOT NULL,
  Nombre VARCHAR(50) NOT NULL,
  Descripcion VARCHAR(150) DEFAULT NULL,
  PorcentajeMaximo DECIMAL(5,2) NOT NULL CHECK (PorcentajeMaximo >= 0),
  PRIMARY KEY (CodTipoPromo)
);

INSERT INTO tipo_promocion (CodTipoPromo, Nombre, Descripcion, PorcentajeMaximo) VALUES
(701, 'Descuento Estudiantil', 'Beneficios para estudiantes universitarios', 25.00),
(702, 'Viernes Negro', 'Promociones exclusivas de fin de año', 50.00),
(703, 'Aniversario', 'Premios por años de antigüedad en la red', 15.00),
(704, 'Cliente Nuevo', 'Descuento de bienvenida en el primer mes', 30.00),
(705, 'Combo Familiar', 'Rebajas por asociar varias líneas a una cuenta', 20.00);

-- --------------------------------------------------------
-- Tabla `promocion`
-- --------------------------------------------------------
CREATE TABLE promocion (
  CodPromocion INT NOT NULL,
  Nombre VARCHAR(50) NOT NULL,
  Descripcion VARCHAR(150) DEFAULT NULL,
  Fecha_Inicio DATE NOT NULL,
  Fecha_Finalizacion DATE NOT NULL,
  Porcentaje_Descuento DECIMAL(5,2) NOT NULL CHECK (Porcentaje_Descuento >= 0),
  CodTipoPromocion INT NOT NULL,
  PRIMARY KEY (CodPromocion),
  CONSTRAINT promocion_ibfk_1 FOREIGN KEY (CodTipoPromocion) REFERENCES tipo_promocion (CodTipoPromo) ON UPDATE CASCADE
);

INSERT INTO promocion (CodPromocion, Nombre, Descripcion, Fecha_Inicio, Fecha_Finalizacion, Porcentaje_Descuento, CodTipoPromocion) VALUES
(901, 'Promo UCR Segundo Semestre', 'Descuento en la mensualidad del plan', '2026-03-01', '2026-07-31', 20.00, 701),
(902, 'Black Friday Móvil', 'Gigas dobles por tres meses', '2026-11-20', '2026-11-30', 50.00, 702),
(903, 'Bono Fidelidad Oro', 'Rebaja para clientes antiguos', '2026-01-01', '2026-12-31', 15.00, 703),
(904, 'Bienvenida Postpago', 'Primer mes a mitad de precio', '2026-05-01', '2026-06-30', 50.00, 704),
(905, 'Descuento Familias Conectadas', 'Aplicado a líneas asociadas', '2026-01-01', '2026-06-30', 10.00, 705);

-- --------------------------------------------------------
-- Tabla `promo_incompatible`
-- --------------------------------------------------------
CREATE TABLE promo_incompatible (
  CodPromocion1 INT NOT NULL,
  CodPromocion2 INT NOT NULL,
  PRIMARY KEY (CodPromocion1, CodPromocion2),
  CONSTRAINT promo_incompatible_ibfk_1 FOREIGN KEY (CodPromocion1) REFERENCES promocion (CodPromocion) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT promo_incompatible_ibfk_2 FOREIGN KEY (CodPromocion2) REFERENCES promocion (CodPromocion) -- ON DELETE NO ACTION
);

INSERT INTO promo_incompatible (CodPromocion1, CodPromocion2) VALUES
(901, 904),
(902, 903);

-- --------------------------------------------------------
-- Tabla `promo_paquete`
-- --------------------------------------------------------
CREATE TABLE promo_paquete (
  CodPromocion INT NOT NULL,
  CodPaquete INT NOT NULL,
  PRIMARY KEY (CodPromocion, CodPaquete),
  CONSTRAINT promo_paquete_ibfk_1 FOREIGN KEY (CodPromocion) REFERENCES promocion (CodPromocion) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT promo_paquete_ibfk_2 FOREIGN KEY (CodPaquete) REFERENCES paquete (CodPaquete) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO promo_paquete (CodPromocion, CodPaquete) VALUES
(902, 502),
(905, 504);

-- --------------------------------------------------------
-- Tabla `promo_plan`
-- --------------------------------------------------------
CREATE TABLE promo_plan (
  CodPromocion INT NOT NULL,
  CodPlan INT NOT NULL,
  PRIMARY KEY (CodPromocion, CodPlan),
  CONSTRAINT promo_plan_ibfk_1 FOREIGN KEY (CodPromocion) REFERENCES promocion (CodPromocion) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT promo_plan_ibfk_2 FOREIGN KEY (CodPlan) REFERENCES plan_tarifario (CodPlan) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO promo_plan (CodPromocion, CodPlan) VALUES
(901, 801),
(903, 803),
(903, 805),
(904, 802),
(905, 804);

-- --------------------------------------------------------
-- Tabla `promo_servicio`
-- --------------------------------------------------------
CREATE TABLE promo_servicio (
  CodPromocion INT NOT NULL,
  CodServicio INT NOT NULL,
  PRIMARY KEY (CodPromocion, CodServicio),
  CONSTRAINT promo_servicio_ibfk_1 FOREIGN KEY (CodPromocion) REFERENCES promocion (CodPromocion) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT promo_servicio_ibfk_2 FOREIGN KEY (CodServicio) REFERENCES servicio (CodServicio) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO promo_servicio (CodPromocion, CodServicio) VALUES
(902, 603),
(905, 601);

-- --------------------------------------------------------
-- Tabla `puntos_fidelizacion`
-- --------------------------------------------------------
CREATE TABLE puntos_fidelizacion (
  IdRegistro_Puntos INT NOT NULL,
  Cedula_Cliente VARCHAR(20) NOT NULL,
  Fecha_Transaccion DATE NOT NULL,
  Puntos_Redimidos_Monto DECIMAL(10,2) NOT NULL CHECK (Puntos_Redimidos_Monto >= 0),
  Saldo_Disponible INT NOT NULL CHECK (Saldo_Disponible >= 0),
  PRIMARY KEY (IdRegistro_Puntos),
  CONSTRAINT puntos_fidelizacion_ibfk_1 FOREIGN KEY (Cedula_Cliente) REFERENCES cliente (Cedula) ON UPDATE CASCADE
);

INSERT INTO puntos_fidelizacion (IdRegistro_Puntos, Cedula_Cliente, Fecha_Transaccion, Puntos_Redimidos_Monto, Saldo_Disponible) VALUES
(1, '1-1111-1111', '2026-05-10', 0.00, 150),
(2, '2-2222-2222', '2026-05-14', 500.00, 50),
(3, '3-3333-3333', '2026-06-01', 0.00, 320),
(4, '4-4444-4444', '2026-06-01', 0.00, 1200),
(5, '5-5555-5555', '2026-06-18', 0.00, 80);

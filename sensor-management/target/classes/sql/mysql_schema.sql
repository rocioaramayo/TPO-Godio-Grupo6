-- Esquema de MySQL para Sistema de Gestión de Sensores
-- Ingeniería de Datos II - TP Persistencia Poliglota

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS sensor_management;
USE sensor_management;

-- Tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(50) NOT NULL UNIQUE,
    permisos TEXT,
    estado ENUM('activo', 'inactivo') DEFAULT 'activo',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    estado ENUM('activo', 'inactivo') DEFAULT 'activo',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso TIMESTAMP NULL
);

-- Tabla de relación usuario-roles
CREATE TABLE IF NOT EXISTS usuario_roles (
    id_usuario INT,
    id_rol INT,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_rol),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol) ON DELETE CASCADE
);

-- Tabla de grupos
CREATE TABLE IF NOT EXISTS grupos (
    id_grupo VARCHAR(50) PRIMARY KEY,
    nombre_grupo VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo_grupo ENUM('equipo_trabajo', 'departamento', 'proyecto') DEFAULT 'equipo_trabajo',
    estado ENUM('activo', 'inactivo') DEFAULT 'activo',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de relación grupo-usuarios
CREATE TABLE IF NOT EXISTS grupo_usuarios (
    id_grupo VARCHAR(50),
    id_usuario INT,
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_grupo, id_usuario),
    FOREIGN KEY (id_grupo) REFERENCES grupos(id_grupo) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- Tabla de procesos
CREATE TABLE IF NOT EXISTS procesos (
    id_proceso INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo_proceso ENUM('informe_temp_max_min', 'informe_temp_promedio', 'alertas_climaticas', 'consultas_online', 'procesos_periodicos') NOT NULL,
    costo DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    parametros_requeridos TEXT,
    estado ENUM('activo', 'inactivo') DEFAULT 'activo',
    tiempo_estimado_minutos INT DEFAULT 5,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de solicitudes de proceso
CREATE TABLE IF NOT EXISTS solicitudes_proceso (
    id_solicitud VARCHAR(50) PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_proceso INT NOT NULL,
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('pendiente', 'completado', 'error', 'cancelado') DEFAULT 'pendiente',
    parametros TEXT,
    resultado TEXT,
    fecha_completado TIMESTAMP NULL,
    observaciones TEXT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_proceso) REFERENCES procesos(id_proceso) ON DELETE CASCADE
);

-- Tabla de historial de ejecución
CREATE TABLE IF NOT EXISTS historial_ejecucion (
    id_ejecucion VARCHAR(50) PRIMARY KEY,
    id_solicitud VARCHAR(50) NOT NULL,
    fecha_ejecucion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resultado TEXT,
    estado ENUM('iniciado', 'completado', 'error', 'cancelado') DEFAULT 'iniciado',
    duracion_segundos INT,
    observaciones TEXT,
    recursos_utilizados TEXT,
    FOREIGN KEY (id_solicitud) REFERENCES solicitudes_proceso(id_solicitud) ON DELETE CASCADE
);

-- Tabla de facturas
CREATE TABLE IF NOT EXISTS facturas (
    id_factura VARCHAR(50) PRIMARY KEY,
    id_usuario INT NOT NULL,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento TIMESTAMP NOT NULL,
    monto_total DECIMAL(10,2) NOT NULL,
    estado ENUM('pendiente', 'pagada', 'vencida', 'cancelada') DEFAULT 'pendiente',
    observaciones TEXT,
    metodo_pago ENUM('efectivo', 'transferencia', 'tarjeta') NULL,
    fecha_pago TIMESTAMP NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- Tabla de procesos facturados
CREATE TABLE IF NOT EXISTS factura_procesos (
    id_factura VARCHAR(50),
    id_proceso VARCHAR(50),
    monto DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_factura, id_proceso),
    FOREIGN KEY (id_factura) REFERENCES facturas(id_factura) ON DELETE CASCADE
);

-- Tabla de pagos
CREATE TABLE IF NOT EXISTS pagos (
    id_pago VARCHAR(50) PRIMARY KEY,
    id_factura VARCHAR(50) NOT NULL,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    monto_pagado DECIMAL(10,2) NOT NULL,
    metodo_pago ENUM('efectivo', 'transferencia', 'tarjeta', 'cheque') NOT NULL,
    estado ENUM('procesado', 'pendiente', 'fallido', 'reembolsado') DEFAULT 'procesado',
    numero_transaccion VARCHAR(100),
    observaciones TEXT,
    datos_adicionales TEXT,
    FOREIGN KEY (id_factura) REFERENCES facturas(id_factura) ON DELETE CASCADE
);

-- Tabla de cuentas corrientes
CREATE TABLE IF NOT EXISTS cuentas_corrientes (
    id_cuenta VARCHAR(50) PRIMARY KEY,
    id_usuario INT NOT NULL UNIQUE,
    saldo_actual DECIMAL(10,2) DEFAULT 0.00,
    limite_credito DECIMAL(10,2) DEFAULT 1000.00,
    estado ENUM('activa', 'suspendida', 'cancelada') DEFAULT 'activa',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_movimiento TIMESTAMP NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- Tabla de movimientos de cuenta
CREATE TABLE IF NOT EXISTS movimientos_cuenta (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_cuenta VARCHAR(50) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo ENUM('cargo', 'abono', 'debito', 'credito', 'sistema') NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    descripcion TEXT NOT NULL,
    saldo_anterior DECIMAL(10,2),
    saldo_posterior DECIMAL(10,2),
    FOREIGN KEY (id_cuenta) REFERENCES cuentas_corrientes(id_cuenta) ON DELETE CASCADE
);

-- Tabla de control de funcionamiento
CREATE TABLE IF NOT EXISTS control_funcionamiento (
    id_control VARCHAR(50) PRIMARY KEY,
    id_sensor INT NOT NULL,
    fecha_revision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado_sensor ENUM('funcionando', 'fallando', 'mantenimiento', 'desconectado') NOT NULL,
    observaciones TEXT,
    tecnico_responsable VARCHAR(100),
    tipo_control ENUM('preventivo', 'correctivo', 'rutina', 'emergencia') DEFAULT 'rutina',
    acciones_realizadas TEXT,
    proxima_revision TIMESTAMP NULL,
    estado_control ENUM('completado', 'pendiente', 'cancelado') DEFAULT 'completado'
);

-- Insertar roles por defecto
INSERT IGNORE INTO roles (descripcion, permisos) VALUES 
('administrador', '{"gestion_usuarios":true,"gestion_sensores":true,"gestion_procesos":true,"facturacion":true,"reportes":true}'),
('técnico', '{"gestion_sensores":true,"gestion_procesos":true,"reportes":true}'),
('usuario', '{"consultas":true,"solicitar_procesos":true}');

-- Insertar procesos por defecto
INSERT IGNORE INTO procesos (nombre, descripcion, tipo_proceso, costo, tiempo_estimado_minutos) VALUES 
('Informe Temperaturas Máximas y Mínimas', 'Genera informe de temperaturas máximas y mínimas por ciudad/país en un rango de fechas', 'informe_temp_max_min', 50.00, 10),
('Informe Temperaturas Promedio', 'Genera informe de temperaturas promedio por ciudad/país en un rango de fechas', 'informe_temp_promedio', 45.00, 8),
('Alertas Climáticas', 'Genera alertas automáticas por temperaturas y humedad fuera de rangos normales', 'alertas_climaticas', 30.00, 5),
('Consultas en Línea', 'Servicio de consultas en tiempo real de información de sensores', 'consultas_online', 20.00, 2),
('Procesos Periódicos', 'Ejecuta procesos periódicos de consultas sobre humedad y temperaturas', 'procesos_periodicos', 100.00, 15);

-- Crear índices para optimizar consultas
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_estado ON usuarios(estado);
CREATE INDEX idx_solicitudes_usuario ON solicitudes_proceso(id_usuario);
CREATE INDEX idx_solicitudes_estado ON solicitudes_proceso(estado);
CREATE INDEX idx_solicitudes_fecha ON solicitudes_proceso(fecha_solicitud);
CREATE INDEX idx_facturas_usuario ON facturas(id_usuario);
CREATE INDEX idx_facturas_estado ON facturas(estado);
CREATE INDEX idx_facturas_fecha ON facturas(fecha_emision);
CREATE INDEX idx_pagos_factura ON pagos(id_factura);
CREATE INDEX idx_movimientos_cuenta ON movimientos_cuenta(id_cuenta);
CREATE INDEX idx_movimientos_fecha ON movimientos_cuenta(fecha);
CREATE INDEX idx_control_sensor ON control_funcionamiento(id_sensor);
CREATE INDEX idx_control_fecha ON control_funcionamiento(fecha_revision);

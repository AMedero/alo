-- Creo tabla Categorías
CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(256)
);

-- Creo tabla Productos
CREATE TABLE IF NOT EXISTS productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(256),
    precio DECIMAL(10, 2) NOT NULL, -- Precio en USD
    imagen_url VARCHAR(256),        -- Ruta relativa (ej: ./images/...)
    stock INT DEFAULT 100,
    categoria_id BIGINT,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- Creo tabla Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    dni VARCHAR(15),       -- String para evitar problemas con puntos
    fecha_nacimiento DATE,
    direccion VARCHAR(256),
    rol VARCHAR(20) DEFAULT 'USER'
);

-- Creo tabla Órdenes
CREATE TABLE IF NOT EXISTS ordenes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2),
    estado VARCHAR(50) DEFAULT 'PENDIENTE',
    metodo_pago VARCHAR(50),
    id_transaccion_mp VARCHAR(100),
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Creo tabla Detalles
CREATE TABLE IF NOT EXISTS detalles_orden (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    orden_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    FOREIGN KEY (orden_id) REFERENCES ordenes(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);
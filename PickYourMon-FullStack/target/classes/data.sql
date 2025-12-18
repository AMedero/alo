-- Inserto Categorías
INSERT INTO categorias (nombre, descripcion) VALUES
('Eeveelutions', 'Todas las evoluciones de Eevee'),
('Clásicos', 'Los favoritos de siempre');

-- Inserto Productos (SOLO LOS 6 VISIBLES - Glaceon y Leafeon quedan para la demo)
INSERT INTO productos (nombre, precio, imagen_url, descripcion, stock, categoria_id) VALUES
('Peluche Eevee', 30.00, './images/productos/eevee_1.jpg', 'El peluche clásico de Eevee.', 50, 1),
('Peluche Flareon', 27.94, './images/productos/flareon_1.jpg', 'La evolución tipo fuego.', 30, 1),
('Peluche Vaporeon', 32.99, './images/productos/vaporeon_1.jpg', 'La evolución tipo agua.', 25, 1),
('Peluche Jolteon', 24.99, './images/productos/jolteon_1.jpg', 'La evolución eléctrica.', 40, 1),
('Peluche Sylveon', 33.54, './images/productos/sylveon_1.jpg', 'La evolución tipo hada.', 20, 1),
('Peluche Umbreon', 30.00, './images/productos/umbreon_1.jpg', 'La evolución siniestra.', 15, 1);

-- Inserto Usuarios (Ana y Jorge)
INSERT INTO usuarios (email, password, nombre, apellido, dni, fecha_nacimiento, direccion, rol) VALUES
('ana@gmail.com', '12345678', 'Ana', 'Martinez', '30123456', '2000-02-14', 'Av. Rivadavia 2812', 'USER'),
('jorge@gmail.com', 'qwerty123', 'Jorge', 'Sánchez', '25987654', '1990-08-20', 'Av Corrientes 1108', 'ADMIN');

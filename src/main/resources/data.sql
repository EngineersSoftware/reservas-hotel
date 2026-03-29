INSERT INTO usuario (id, nombre, email, telefono, dtype, membresia) VALUES
(1, 'Juan Perez', 'juan.perez@gmail.com', '1234567890', 'CLIENTE', 'BRONCE'),
(2, 'Maria Lopez', 'maria.lopez@gmail.com', '0987654321', 'CLIENTE', 'PLATA'),
(3, 'Pedro Gomez', 'pedro.gomez@gmail.com', '1122334455', 'CLIENTE', 'ORO'),
(4, 'Ana Martinez', 'ana.martinez@gmail.com', '5566778899', 'CLIENTE', 'DIAMANTE'),
(5, 'Luis Rodriguez', 'luis.rodriguez@gmail.com', '9988776655', 'CLIENTE', 'PLATINO'),
(6, 'Carlos Sanchez', 'carlos.sanchez@gmail.com', '4433221100', 'CLIENTE', 'BRONCE'),
(7, 'Laura Garcia', 'laura.garcia@gmail.com', '7788990011', 'CLIENTE', 'PLATA'),
(8, 'Jorge Martinez', 'jorge.martinez@gmail.com', '2233445566', 'CLIENTE', 'ORO'),
(9, 'Ana Lopez', 'ana.lopez@gmail.com', '8899001122', 'CLIENTE', 'DIAMANTE'),
(10, 'Pedro Rodriguez', 'pedro.rodriguez@gmail.com', '3344556677', 'CLIENTE', 'PLATINO');

INSERT INTO usuario (id, nombre, email, telefono, dtype, cargo) VALUES 
(11, 'Juan Perez', 'juan.perez@gmail.com', '1234567890', 'EMPLEADO', 'Recepcionista'),
(12, 'Maria Lopez', 'maria.lopez@gmail.com', '0987654321', 'EMPLEADO', 'Gerente'),
(13, 'Pedro Gomez', 'pedro.gomez@gmail.com', '1122334455', 'EMPLEADO', 'Administrador');

INSERT INTO habitacion (id, numero, tipo, precio_por_noche, disponible, capacidad) VALUES
(1, '101', 'SENCILLA', 100000.0, true, 1),
(2, '102', 'DOBLE', 200000.0, true, 2),
(3, '103', 'SUITE', 300000.0, true, 3),
(4, '104', 'FAMILIAR', 400000.0, true, 4),
(5, '105', 'FAMILIAR', 500000.0, true, 5),
(6, '106', 'SUITE', 600000.0, true, 3),
(7, '107', 'PRESIDENCIAL', 700000.0, true, 2);

INSERT INTO reserva (id, cliente_id, habitacion_id, fecha_entrada, fecha_salida, estado, total) VALUES
(1, 1, 1, '2026-01-01', '2026-01-03', 'CONFIRMADA', 300000.0),
(2, 2, 2, '2026-01-04', '2026-01-06', 'PENDIENTE', 600000.0),
(3, 3, 3, '2026-01-07', '2026-01-09', 'CONFIRMADA', 900000.0);

INSERT INTO pago (id, reserva_id, monto, metodo_pago, fecha_pago, estado_pago) VALUES
(1, 1, 300000.0, 'TARJETA', '2026-01-01', 'COMPLETADO'),
(2, 2, 600000.0, 'EFECTIVO', '2026-01-04', 'COMPLETADO'),
(3, 3, 900000.0, 'TRANSFERENCIA', '2026-01-07', 'COMPLETADO');
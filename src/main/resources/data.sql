-- =========================================================================
-- Datos semilla iniciales: Alojamientos y Reseñas genéricas
-- =========================================================================

-- Inmueble 1: Ático de lujo en Madrid
INSERT INTO properties (id, title, address, description, city, destination, type, property_type, price_per_night, capacity, available, direct_booking, image_url, rating, review_count)
VALUES (1, 'Ático de lujo con terraza privada en Gran Vía', 'Gran Vía 42, 7ºA', 'Exclusivo ático con espectaculares vistas panorámicas de la ciudad. Equipado con terraza solárium, cocina de diseño, salón espacioso y acabados de primera calidad a pasos de teatros y restaurantes.', 'Madrid', 'Madrid', 'Ático', 'Ático', 180.0, 4, true, true, 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=1200&q=80', 4.9, 4);

-- Inmueble 2: Apartamento modernista en Barcelona
INSERT INTO properties (id, title, address, description, city, destination, type, property_type, price_per_night, capacity, available, direct_booking, image_url, rating, review_count)
VALUES (2, 'Elegante apartamento modernista en Eixample', 'Passeig de Gràcia 88, 3ºB', 'Luminoso apartamento con altos techos artesonados y suelos hidráulicos tradicionales. Ubicado en una zona inmejorable a pocos minutos de Casa Batlló y transporte público.', 'Barcelona', 'Barcelona', 'Apartamento', 'Apartamento', 135.0, 3, true, false, 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=1200&q=80', 4.6, 3);

-- Inmueble 3: Villa mediterránea en Málaga
INSERT INTO properties (id, title, address, description, city, destination, type, property_type, price_per_night, capacity, available, direct_booking, image_url, rating, review_count)
VALUES (3, 'Villa mediterránea con piscina privada y jardín', 'Camino del Faro 12', 'Maravillosa villa independiente con piscina climatizada, porche exterior con barbacoa y amplias estancias climatizadas para disfrutar en familia de la Costa del Sol.', 'Málaga', 'Málaga', 'Villa', 'Villa', 290.0, 8, true, true, 'https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=1200&q=80', 5.0, 3);

-- Inmueble 4: Casa histórica en Toledo
INSERT INTO properties (id, title, address, description, city, destination, type, property_type, price_per_night, capacity, available, direct_booking, image_url, rating, review_count)
VALUES (4, 'Casa toledana señorial en el casco histórico', 'Calle Santo Tomé 15', 'Encantadora casa restaurada manteniendo elementos mudéjares y patio central toledano. Silenciosa, acogedora y ubicada en pleno corazón monumental de Toledo.', 'Toledo', 'Toledo', 'Casa', 'Casa', 95.0, 5, true, true, 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=1200&q=80', 4.7, 3);

-- Inmueble 5: Chalet con jardín en Valencia
INSERT INTO properties (id, title, address, description, city, destination, type, property_type, price_per_night, capacity, available, direct_booking, image_url, rating, review_count)
VALUES (5, 'Chalet contemporáneo cerca de la playa', 'Avenida del Puerto 104', 'Espacioso chalet con jardín privado, salón diáfano y parking privado. Ideal para disfrutar del clima levantino y de la Ciudad de las Artes y las Ciencias.', 'Valencia', 'Valencia', 'Chalet', 'Chalet', 210.0, 6, true, false, 'https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&w=1200&q=80', 4.3, 3);

-- Inmueble 6: Cabaña de madera en Granada
INSERT INTO properties (id, title, address, description, city, destination, type, property_type, price_per_night, capacity, available, direct_booking, image_url, rating, review_count)
VALUES (6, 'Cabaña alpina de madera en Sierra Nevada', 'Carretera de la Sierra km 22', 'Auténtico refugio de montaña con chimenea de leña, vistas panorámicas a las cumbres y acceso directo a rutas de senderismo y pistas de esquí.', 'Granada', 'Granada', 'Cabaña', 'Cabaña', 85.0, 4, true, true, 'https://images.unsplash.com/photo-1449158743715-0a90ebb6d2d8?auto=format&fit=crop&w=1200&q=80', 4.8, 4);

-- Inmueble 7: Apartamento en Sevilla
INSERT INTO properties (id, title, address, description, city, destination, type, property_type, price_per_night, capacity, available, direct_booking, image_url, rating, review_count)
VALUES (7, 'Apartamento con encanto andaluz en Triana', 'Calle Betis 29, 2ºA', 'Ubicado frente al río Guadalquivir, este apartamento destaca por su balcón típico sevillano, luz natural, decoración cálida y cercanía a la Plaza de España.', 'Sevilla', 'Sevilla', 'Apartamento', 'Apartamento', 75.0, 3, true, true, 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=1200&q=80', 4.5, 2);

-- Inmueble 8: Estudio costero en Bilbao
INSERT INTO properties (id, title, address, description, city, destination, type, property_type, price_per_night, capacity, available, direct_booking, image_url, rating, review_count)
VALUES (8, 'Estudio vanguardista junto a la Ría', 'Muelle de Uribitarte 8', 'Estudio funcional con diseño industrial moderno, a escasos metros del Museo Guggenheim. Conexión rápida a tranvía y zona gastronómica insuperable.', 'Bilbao', 'Bilbao', 'Apartamento', 'Apartamento', 65.0, 2, true, false, 'https://images.unsplash.com/photo-1493809842364-78817add7ffb?auto=format&fit=crop&w=1200&q=80', 4.0, 2);

-- =========================================================================
-- Reseñas asociadas a los alojamientos
-- =========================================================================

-- Reseñas para Inmueble 1 (Madrid)
INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (1, 1, null, 'Lucía Gómez', 5.0, 'La terraza es simplemente espectacular al atardecer. Todo limpio, ordenado y con todo lo necesario para sentirse como en casa.', TIMESTAMP '2026-10-15 18:30:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (2, 1, null, 'Marcos Serrano', 5.0, 'Inmejorable ubicación en plena Gran Vía. Muy silencioso gracias al buen aislamiento de las ventanas.', TIMESTAMP '2026-10-16 12:15:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (3, 1, null, 'Elena Rivas', 4.5, 'Apartamento impecable. El anfitrión respondió en minutos a todas nuestras dudas. Sin duda repetiremos.', TIMESTAMP '2026-10-17 09:40:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (4, 1, null, 'Álvaro Martín', 5.0, 'Experiencia de diez. Comodidad absoluta en camas y baño de lujo.', TIMESTAMP '2026-10-18 21:00:00');

-- Reseñas para Inmueble 2 (Barcelona)
INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (5, 2, null, 'Carla Bosch', 5.0, 'Los techos altos y la luz de la mañana le dan una magia única. Zona tranquila pero con mucha vida.', TIMESTAMP '2026-10-14 11:20:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (6, 2, null, 'David Valls', 4.5, 'Muy bien decorado y camas comodísimas. Todo perfecto durante nuestra estancia.', TIMESTAMP '2026-10-16 15:45:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (7, 2, null, 'Mireia Puig', 4.5, 'Excelente trato del anfitrión y muy buena limpieza.', TIMESTAMP '2026-10-19 10:10:00');

-- Reseñas para Inmueble 3 (Málaga)
INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (8, 3, null, 'Fernando Cano', 5.0, 'La piscina y la zona de barbacoa son un sueño para desconectar un fin de semana con amigos.', TIMESTAMP '2026-10-12 17:00:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (9, 3, null, 'Patricia Soler', 5.0, 'Instalaciones de primera calidad, aire acondicionado en todas las estancias y mucha privacidad.', TIMESTAMP '2026-10-15 20:30:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (10, 3, null, 'Javier Gil', 5.0, 'Un paraíso en Málaga. Cumplió y superó todas nuestras expectativas.', TIMESTAMP '2026-10-18 14:00:00');

-- Reseñas para Inmueble 4 (Toledo)
INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (11, 4, null, 'Beatriz Romero', 5.0, 'Dormir en una casa con tanta historia es una maravilla. El patio interior es encantador.', TIMESTAMP '2026-10-10 13:15:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (12, 4, null, 'Raúl Navarro', 4.5, 'Muy céntrico, se puede ir caminando a todas partes. Muy recomendable.', TIMESTAMP '2026-10-13 18:40:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (13, 4, null, 'Silvia Morales', 4.5, 'Todo excelente, anfitriones muy amables y atentos.', TIMESTAMP '2026-10-17 16:50:00');

-- Reseñas para Inmueble 5 (Valencia)
INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (14, 5, null, 'Andrés Garrido', 4.5, 'Chalet muy amplio, con todo lo necesario y muy cerca de la playa.', TIMESTAMP '2026-10-11 19:20:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (15, 5, null, 'Nuria Benítez', 4.0, 'Buena experiencia, jardín muy aprovechable con buen tiempo.', TIMESTAMP '2026-10-14 12:00:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (16, 5, null, 'Tomás Herrero', 4.5, 'Estancia cómoda y comunicación rápida.', TIMESTAMP '2026-10-17 11:30:00');

-- Reseñas para Inmueble 6 (Granada)
INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (17, 6, null, 'Daniel Ortiz', 5.0, 'La chimenea encendida con la nieve de fondo fue inigualable. Cabaña impecable.', TIMESTAMP '2026-10-09 22:15:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (18, 6, null, 'Cristina Peña', 5.0, 'Acogedora, cálida y rodeada de naturaleza pura. Repetiremos seguro.', TIMESTAMP '2026-10-12 16:30:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (19, 6, null, 'Iker Mendoza', 4.5, 'Excelente para esquiar y relajarse por la noche.', TIMESTAMP '2026-10-15 14:10:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (20, 6, null, 'Sara Lozano', 4.5, 'Entorno mágico y anfitrión muy hospitalario.', TIMESTAMP '2026-10-18 19:00:00');

-- Reseñas para Inmueble 7 (Sevilla)
INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (21, 7, null, 'Manuel Flores', 4.5, 'Las vistas a la orilla del Guadalquivir son preciosas. Muy bien situado.', TIMESTAMP '2026-10-13 20:00:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (22, 7, null, 'Rocío Marín', 4.5, 'Un apartamento con mucho duende en pleno barrio de Triana.', TIMESTAMP '2026-10-16 17:45:00');

-- Reseñas para Inmueble 8 (Bilbao)
INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (23, 8, null, 'Aitor Echeverría', 4.0, 'Ideal para un viaje de trabajo o turismo cultural junto a la Ría.', TIMESTAMP '2026-10-14 14:20:00');

INSERT INTO reviews (id, property_id, author_id, author_name, rating, comment, created_at)
VALUES (24, 8, null, 'Leire Aguirre', 4.0, 'Muy céntrico, limpio y cómodo para dos personas.', TIMESTAMP '2026-10-17 18:30:00');

-- Ajustar secuencias de identidad H2 para futuras inserciones
ALTER TABLE properties ALTER COLUMN id RESTART WITH 100;
ALTER TABLE reviews ALTER COLUMN id RESTART WITH 100;

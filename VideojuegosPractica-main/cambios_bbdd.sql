-- Cambios de esquema que necesitan las modificaciones del ejercicio.
-- Ejecutar sobre la base de datos `biblioteca` (cada bloque corresponde a un commit).

-- [commit: teléfono de los socios]
ALTER TABLE socios ADD COLUMN telefono VARCHAR(9) NULL;

-- [commit: máximo de renovaciones por préstamo]
ALTER TABLE prestamos ADD COLUMN renovaciones TINYINT NOT NULL DEFAULT 0;

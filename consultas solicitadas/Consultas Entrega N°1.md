## Punto 1: Conocer los viajes que reservó un determinado usuario en el corriente año.(View)

```sql
CREATE OR REPLACE VIEW ver_viajes_reservados_por_usuario AS
SELECT 
    uc.nombre AS nombre_chofer,
    uc.apellido AS apellido_chofer,
    up.nombre AS nombre_pasajero,
    up.apellido AS apellido_pasajero,
    p.id AS id_pasajero,
    v.fecha_inicio,
    v.fecha_finalizacion
FROM viaje v       
         JOIN chofer c ON v.id_chofer = c.id
         JOIN pasajero p ON v.id_pasajero = p.id
		 JOIN user_data uc ON uc.id =  c.id_user_data
		 JOIN user_data up ON up.id =  p.id_user_data
WHERE EXTRACT(YEAR FROM v.fecha_inicio) = EXTRACT(YEAR FROM CURRENT_DATE)
AND  v.fecha_inicio >= CURRENT_TIMESTAMP;

SELECT * FROM ver_viajes_reservados_por_usuario WHERE id_pasajero=1
```

## Punto 2 : Llevar un control de las veces que un usuario modifica su saldo, de manera de saber: a) la fecha en la que se modificó, b) el nuevo saldo y el anterior saldo.(Trigger)

### Paso 1: Creación de la tabla log_pasajero

```sql
CREATE TABLE log_pasajero(
  id_log SERIAL PRIMARY KEY,
  fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  id_pasajero INTEGER,
  saldo_anterior DOUBLE PRECISION,
  saldo_nuevo DOUBLE PRECISION
);
```

### Paso 2: Creación de la funcion fn_log_pasajero_cambio()

```sql
CREATE FUNCTION fn_log_pasajero_cambio()
  RETURNS TRIGGER AS $$ 
  BEGIN 
    IF NEW.saldo >= OLD.saldo THEN
      INSERT INTO log_pasajero(id_pasajero, saldo_anterior, saldo_nuevo) VALUES (OLD.id, OLD.saldo, NEW.saldo);
    END IF;
    RETURN NEW;
  END;
$$LANGUAGE plpgsql;
```

### Paso 3: Creación del trigger trg_cambios_pasajero

```sql
CREATE TRIGGER trg_cambios_pasajero
  AFTER UPDATE ON pasajero
  FOR EACH ROW
  EXECUTE FUNCTION fn_log_pasajero_cambio();
```

### Comprobación de funcionamiento del Trigger:

```sql
  --Se actualiza el saldo de un pasajero
UPDATE pasajero
SET saldo=1500000.0
WHERE id=1;
--Se consulta si se realizó el cambio de saldo
select * from log_pasajero;
```

## Punto 3: Saber qué usuarios tienen más de N reservas.(Function)

```sql
CREATE OR REPLACE FUNCTION pasajeros_con_n_reservas(cantidad_minima INT)
RETURNS TABLE(nombre VARCHAR(50), apellido VARCHAR(50), total_viajes BIGINT)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT u.nombre, u.apellido, COUNT(*) AS total_viajes
    FROM viaje v
    JOIN pasajero p ON v.id_pasajero = p.id
    JOIN user_data u ON u.id = p.id
	WHERE v.fecha_inicio >= CURRENT_TIMESTAMP
    GROUP BY p.id, u.nombre, u.apellido
    HAVING COUNT(*) >= cantidad_minima;
END;
$$;

SELECT * FROM pasajeros_con_n_reservas(1)
```

## Punto 4: Evitar que el precio base de un chofer tome un valor nulo en la base (por fuera de la interfaz de usuario).(Constrains)

```sql
ALTER TABLE chofer
ALTER COLUMN precio_base SET NOT NULL;
```

## Punto 5: Listar los choferes que tengan más de 2 viajes realizados.(View)

```sql
CREATE OR REPLACE VIEW choferes_con_mas_de_dos_viajes AS 
  SELECT 
      u.nombre,
      u.apellido,
      COUNT(*) AS cantidad_viajes
  FROM viaje v 
  JOIN chofer c ON v.id_chofer = c.id
  JOIN user_data u ON u.id = c.id_user_data
  WHERE v.fecha_finalizacion <= CURRENT_TIMESTAMP
  GROUP BY c.id, u.nombre, u.apellido
  HAVING COUNT(*) > 2
  ORDER BY cantidad_viajes DESC; 

SELECT * FROM choferes_con_mas_de_dos_viajes;
```
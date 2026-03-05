## Punto 1: Saber qué usuarios hicieron para un determinado chofer
```cypher
MATCH (p:PasajeroAmigo)-[v:VIAJO_CON]->(c:ChoferDeRelacionDeViaje {apellido: 'Cejas'})
RETURN  DISTINCT
p.nombre AS NombrePasajero,
p.apellido AS apellidoDePasajero
```



## Punto 2: Saber qué usuarios tienen más de 4 viajes.

```cypher
MATCH (p:PasajeroAmigo)-[v:VIAJO_CON]->(c:ChoferDeRelacionDeViaje)
WITH p, COUNT(v) AS totalViajes         
WHERE totalViajes > 4                   
RETURN p.apellido AS NombrePasajero,
p.nombre AS ApellidoPasajero,
p.idPasajero AS IDPasajero,
totalViajes  AS CantidadDeViajes
```


## Punto 3: Saber una sugerencia de choferes que podrían interesar. Son los choferes con los que viajaron amigos del usuario.

```cypher
MATCH (p:PasajeroAmigo {apellido: 'Rodriguez'})-[:AMIGO_DE]->(p2:PasajeroAmigo)
MATCH (p2)-[:VIAJO_CON]->(choferSugerido:ChoferDeRelacionDeViaje)
WHERE NOT (p)-[:VIAJO_CON]->(choferSugerido)
RETURN DISTINCT choferSugerido.nombre AS NomnbreChoferRecomendado,
choferSugerido.apellido AS ApellidoChoferRecomendado,
choferSugerido.idChofer AS IdChofer,
COLLECT(DISTINCT {nombrePasajero: p2.nombre, apellidoPasajero: p2.apellido}) AS nombreApellidoDeLosAmigos
LIMIT 5
```



## Punto 4: Saber qué choferes tienen más de 4 viajes.

```cypher
MATCH (:PasajeroAmigo)-[v:VIAJO_CON]->(c:ChoferDeRelacionDeViaje)
WITH c, COUNT(v) AS totalViajes         
WHERE totalViajes > 4                   
RETURN c.nombre AS NombreChofer,
c.apellido AS ApellidoChofer,
c.idChofer AS IdChofer,
totalViajes AS CantidadDeViajes
```



## Punto 5: Saber qué usuario tiene un viaje con un chofer que tiene más de 3 viajes pendientes.

```cypher
MATCH (p:PasajeroAmigo)-[v:VIAJO_CON]->(c:ChoferDeRelacionDeViaje)
WHERE datetime(v.fechaFinalizacion) > datetime()

WITH p, c, v
MATCH (c)<-[chofer_v:VIAJO_CON]-(:PasajeroAmigo)
WHERE datetime(chofer_v.fechaFinalizacion) > datetime()

WITH p, c, COUNT(chofer_v) AS viajesPendientesChofer
WHERE viajesPendientesChofer > 3
RETURN DISTINCT p.nombre AS NombrePasajero,
p.apellido AS ApellidoPasajero,
p.idPasajero AS IdPasajero,
c.nombre AS NombreChofer,
c.apellido AS ApellidoChofer,
c.idChofer AS IdChofer
```
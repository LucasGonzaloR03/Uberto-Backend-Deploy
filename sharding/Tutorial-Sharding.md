# Pasos a seguir para configurar el sharding en mongo
## Pre-requisitos previos antes de empezar la configuracion
Antes de empezar con la configuracion, se tiene que comprobar que en el archivo [*application.yml*](../src/main/resources/application.yml), que se encuentra dentro de *src/main/resources*, que la instancia de mongo con sharding que utilizaremos para esta configuracion se encuentre descomentada y la que tiene que estar comentada seria la instancia de mongo sin sharding, deberia de quedar algo tal que asi.
```yml
#MongoDB sin sharding
#  data:
#    mongodb:
#      uri: mongodb://admin:admin@localhost:27020/uberto_mongo?authSource=admin

  ##MongoDB con sharding
  data:
    mongodb:
      host: localhost
      port: 27117
      database: uberto_mongo
  server:
    error:
      include-message: always
      include-binding-errors: always
```

## Inicio de configuracion de sharding
### Paso 1: Levantar el container del sharding
Para levantar el container del sharding donde se van a encontrar dentro de este las configuraciones de los dos shards y sus replicas set, primero nos tendremos que dirigir dentro de la carpeta donde se encuentra el docker-compose del sharding y abri una terminal de git bash.

![abrir_consola](./images/abrir-git-bash.gif)

Una vez dentro de la consola lo que procedemos hacer es ejecutar el siguente comando para poder levantar el container.

```bash
docker compose up -d
```

Se nos tendria que haber levantado todas nuestras base de datos que utlizamos en la configuracion de esta.

![levantar docker](./images/levantar-container.jpg)

Para comprobar que se nos hayan levantado correctamente nuestras instancias dentro del container, haremos uso del siguiente comando que nos tiene que mostrar por consola las instancias de docker que estan corriendo.

```bash
docker ps
```
![comprobacion_container](./images/comprobacion-container.jpg)
### Paso 2: Levantar los servers de configuracion
Una vez levantado y hecho la comprobacion del primer paso, pasamos a la configuracion de los servers, esto lo haremos haciendo uso del script llamado *init-configserver.js* hecho en java script que lo podemos encontrar dentro de nuestra carpeta scripts.

```js 
rs.initiate({
    _id: "rs-config-server", configsvr: true, version: 1, 
    members: [ 
        { _id: 0, host : 'configsvr01:27017' }, 
        { _id: 1, host : 'configsvr02:27017' }
    ] 
})
```
Para poder utilizar este script en nuestro codigo tendremos que hacer uso del comando que esta a continuacion que nos va a servir para poder hacer las configuracion de los servers de nuestra estructura.

```bash
docker compose exec configsvr01 sh -c "mongosh < /scripts/init-configserver.js"
```
Posdata: Si ya se ejecuto una vez no es necesario ejecutarlo de vuelta.

### Paso 3: Levantar los sharding(Instancias de MongoDB)
El siguiente paso seria levantar nuestros shardinds que dentro de esta podemos encontrar las instancias de mongo db dentro de nuestra estructura.
#### Paso 3.1: Levantar el shard 1 de nuestra estructura
En este paso lo que haremos es levantar nuestras instancias de mongo db dentro del sharding-01 haciendo uso del script llamado *init.shard01.js* que lo podemos encontrar dentro de nuestra carpeta scripts.
```js 
rs.initiate(
    {_id: "rs-shard-01", version: 1, 
     members: [
        { _id: 0, host : "shard01-a:27017" }, 
        { _id: 1, host : "shard01-b:27017" }, 
        { _id: 2, host : "shard01-c:27017" }
     ] 
})
```
Para poder utilizar este script que nos va a servir para levantar el shard 1 dentro de nuestra estructura tendremos que hacer uso del comando que esta a continuacion.
```bash
docker compose exec shard01-a sh -c "mongosh < /scripts/init-shard01.js"
```
Posdata: Si ya se ejecuto una vez no es necesario ejecutarlo de vuelta.
#### Paso 3.2: Levantar el shard 2 de nuestra estructura
En este paso lo que haremos es levantar nuestras instancias de mongo db dentro del sharding-02 haciendo uso del script llamado *init.shard02.js* que lo podemos encontrar dentro de nuestra carpeta scripts.
```js 
rs.initiate(
    {_id: "rs-shard-02", version: 1, 
     members: [
        { _id: 0, host : "shard02-a:27017" },
        { _id: 1, host : "shard02-b:27017" },
        { _id: 2, host : "shard02-c:27017" }
     ]v
 })
```
Para poder utilizar este script que nos va a servir para levantar el shard 2 dentro de nuestra estructura tendremos que hacer uso del comando que esta a continuacion.
```bash
docker compose exec shard02-a sh -c "mongosh < /scripts/init-shard02.js"
```
Posdata: Si ya se ejecuto una vez no es necesario ejecutarlo de vuelta.

### Paso 4: Levantar el router
Una vez hecho el paso 2 y paso 3, esperamos unos 45 segundos aproximadamente y ejecutamos el siguiente script llamado *init-router.js* que lo podemos encontrar dentro de nuestra carpeta script. 
```js 
sh.addShard("rs-shard-01/shard01-a:27017")
sh.addShard("rs-shard-01/shard01-b:27017")
sh.addShard("rs-shard-01/shard01-c:27017")
sh.addShard("rs-shard-02/shard02-a:27017")
sh.addShard("rs-shard-02/shard02-b:27017")
sh.addShard("rs-shard-02/shard02-c:27017")
```
Para poder utilizar este script que nos va a servir para levantar todas las instancias dentro de nuestra estructura tendremos que hacer uso del comando que esta a continuacion.
```bash
docker compose exec router01 sh -c "mongosh < /scripts/init-router.js"
```
### Paso 5: Verificacion si se configuro correctamente
Para poder hacer la verificacion tenemos que ejecutar el siguiente comando para poder entrar dentro del router de nuestra esctuctura.
```bash
docker compose exec router01 mongosh --port 27017
```
Una vez dentro de este hacemos uso del siguiente comando que nos va a servir para poder ver la estructura de este mismo.
```js
sh.status()
```
![comprobacion_estructura](./images/comprobacion-estructura.gif)

## Configurar indice y carga de datos dentro de nuestra base de datos
Primero tendriamos que ir adentro de la instancia del router que podramos acceder a este mismo ejecutando el siguiente comando, antes de ejecutar este comando tendriamos que haber salido del router de la verificacion anterior y volver a nuestra consola de git bash.
```bash
docker exec -it router-01 bash
```
### Paso 1: Comprobar scripts dentro de la estructura
Cuando hicimos la configuracion del [docker compose](./docker-compose.yml) dijimos que copie el directorio *scripts* dentro de nuestro contenedor del sharding, entonces para poder comprobar que se haya realizado esto utilizaremos el siguiente comando.
```bash
ls -la /scripts
```
![comprobacion_scripts](./images/comprobacion-scripts.jpg)

Dentro de estos scripts vamos a poder encontrar un script llamado [*chofer.js*](./scripts/chofer.js) que nos va a servir para poder insertar 30 mil choferes dentro de nuestra coleccion *Chofer* que se encuentra dentro de nuestra base de datos llamada *uberto_mongo*

### Paso 2: Utilizar mongo sh para poder seleccionar el uso de nuestra base de datos
Una vez hecha la comprobacion de que exista nuestros scripts dentro del contenderor, haremos uso de mongo shell para poder seleccionar nuestra base de datos para poder ejecutar los comandos que veremos a continuacion en los siguientes pasos, esto lo tenemos que hacer sin salir del router.
#### Paso 2.1: Acceder a mongo sh
Para poder ingresar dentro de mongo shell tendremos que ejecutar el siguiente comando.
```bash
mongosh
```
#### Paso 2.2: Seleccionar nuestra base de datos de ambiente de trabajo
Una vez dentro de mongo shell seleccionamos nuestra base de datos *uberto_mongo* que utilizaremos para poder nuestro trabajo didactico.
```js
use uberto_mongo
```
Para comprobar que en realidad estamos utilizando esta base de datos haremos uso del siguiente comando y nos tiene que salir por consola *uberto_mongo*
```js
db
```
![comprobacion_base_datos](./images/comprobacion-base-de-datos.jpg)

### Paso 3: Creacion de indice de sharding y habilitacion de sharding dentro de la base de datos
#### Paso 3.1: Creacion del indice del sharding
Para poder llevar acabo esto primero tendremos que crear el indice de sharding que queremos utilizar para hacer el balanceo y reparticion de documentos de choferes dentro de nuestros shardings
```js
// Creamos el índice de choferes por hash del id, lo que nos asegura una buena dispersión
db.Chofer.ensureIndex({"_id": "hashed"})
```
##### ¿Por que se eligio el id del chofer como indice en este ejemplo?
Se eligio como indice el id del propio chofer como balanceo, porque entre varias pruebas que hicimos, por ejemplo, el id del user data del chofer o creando un variable nueva llamada nroChofer dentro del chofer, no quedaba dentro del todo bien balanceada ya que la diferencia de documentos entre los shardings era entre el rango de 400 a 600 documentos, es decir, que al crear 60 mil choferes el sharding 1 se quedaba con 29558 mientras que el sharding 2 se quedaba con 30442 choferes.

![comprobacion_userDataid](./images/comprobacion-indice-userDataId.jpg)

En cambio utilizando el id del chofer, la diferencia de documentos entre los shardings era minima, es decir, que capaz a veces habia 4 documentos de diferencia o por ejemplo la imagen que se puede ver al final la diferencia fue de 113 documentos entre los sharding, asi que pudimos notar que con este obtuvimos un mejor balanceo entre los sharding.
#### Paso 3.2: Habilitar el sharding dentro de nuestra base de datos
Para poder llevar acabo esto tendremos que ejecutar el siguiente comando para poder habilitar el sharding dentro de nuestra base de datos *uberto_mongo*
```js
// Creamos el índice de choferes por hash del id, lo que nos asegura una buena dispersión
sh.enableSharding("uberto_mongo")
```
#### Paso 3.3: Definicion de la clave
Para poder llevar acabo esto tendremos que ejecutar el siguiente comando que nos va a servir para poder utilizar el indice que creamos en el paso 3.1.
```js
// Definimos la clave por el índice que anteriormente generamos
sh.shardCollection("uberto_mongo.Chofer", {"_id": "hashed" }, false)
```
![imagen_paso_3](./images/imagen-punto-3.jpg)

### Paso 4: Insercion de datos dentro de nuestra base de datos.
Una vez realizado el paso 3, vamos a insertar choferes dentro de nuestra coleccion *Chofer* utilizando el script mencionado anteriormente llamado [*chofer.js*](./scripts/chofer.js) que este nos servia para insertar 30 mil choferes dentro de esta misma. Esto lo vamos a llevar acabo ejecutando el siguiente comando.
```js
// Ejecutar por lo menos dos veces
load("/scripts/chofer.js")
```
### Paso 5: Comprobar si se hizo de manera correcta el sharding y el balanceo de datos
Una vez se hayan creado todos los choferes dentro de nuestra coleccion *Chofer* lo que vamos a hacer es ejecutar el siguiente comando para poder ver si se repartio correctamente los documentos de choferes entre nuestros dos shards, esto lo vamos a llevar acabo ejecutando el siguiente comando.
```js
db.Chofer.getShardDistribution()
```

![comprobacion_balanceo](./images/comprobacion-balanceo.jpg)

Entonces como podemos observar en la imagen, el balanceo de los choferes entre nuestros dos shardings se llevo de una manera correcta.

## Extras
Para poder hacer la prueba de balanceo utilizando otro atributo de chofer para crear el indice de balanceo ya sea usando createIndex o ensureIndex(utilizado en nuestro ejemplo), habria que antes eliminiar la coleccion de *Chofer* utilizando el comando que esta a continuacion, una vez eliminada nuestra coleccion, vamos a poder realizar la prueba de vuelta repitiendo los correspondientes pasos del paso 3 al paso 5 del item de arriba.
```js
db.dropDatabase("Chofer")
```

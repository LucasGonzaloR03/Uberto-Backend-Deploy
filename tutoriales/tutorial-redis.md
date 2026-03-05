# Tutorial de Redis
## Pre-requisitos previos
Uno de los requisitios que solicitaban en nuestra entrega era tener una conexion con la base de datos de redis, para esto antes de comenzar con el trabajo requerido, tuvimos que hacer las siguientes configuraciones en los siguientes archivos de nuestro proyecto.
### Paso 1: Agregar lo siguiente en nuestro docker-compose.yml
En nuestro [*docker-compose.yml*](../docker-compose.yml) tendremos que agregar las siguientes lineas para poder configurar nuestro servicio de base de datos para que se levante con nuestros servicios ya configurados previamente.
```yml
services:
  ...
  redis:
    container_name: redis-uberto
    hostname: redis
    image: redis
    ports:
      - "6379:6379"

  redis-commander:
    container_name: redis-commander-uberto
    hostname: redis-commander
    image: rediscommander/redis-commander:latest
    restart: always
    environment:
      - REDIS_HOSTS=local:redis:6379
    ports:
      - "8081:8081"
```
### Paso 2: Agregar las siguientes dependecias en nuestro build.gradle.kts
En nuestro [*build.gradle.kts*](../build.gradle.kts) tuvimos que agregar las siguientes dependecias necesarias para poder llevar acabo nuestro trabajo con la base de datos de redis.
```kts
dependencies{
  ...
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  ...
}
```

## Explicacion de lo que hicimos para completar la entrega
Lo pedido en nuestra entrega fue lo siguiente.

*Se deberá persistir los datos de la última búsqueda realizada con un time to live de 18.000 segundos (5 horas). Al entrar nuevamente en la app, en la home deberá acceder a los mismos si existen y realizar la búsqueda correspondiente.*

Esta ultima busqueda de un viaje va a ser unica por pasajero, es decir, que cada vez que se realice una nueva busqueda de un viaje, esta va a remplazar a la ya existente si es que hay una creada anterioremente.

Entonces para poder hacer esto, tuvimos que seguir los siguientes pasos que los explicaremos a continuacion.
### Configuraciones nuevas necesarias que tuvimos que crear para utilizar redis
#### Paso 1: Crear el dominio UltimaBusquedaDeUnViaje
Creamos un objeto de dominio que lo llamamos [*UltimoBusquedaDeUnViaje*](../src/main/kotlin/ar/edu/unsam/phm/domain/UltimaBusquedaDeUnViaje.kt) que le incorporamos la etiqueta *@RedisHash* que nos sirvio para persistirlo en nuestra base de datos de redis. A su vez tambien agregamos el TLL(Time To Live) que se nos pidio en el enunciado, esto nos permite que despues de un determinado tiempo se borre este dentro de la misma base de datos.
```kt
@RedisHash("UltimaBusquedaDeUnViaje", timeToLive = 18000)// Dicha etiqueta mencionada
data class UltimaBusquedaDeUnViaje(
     val idPasajero: Long,
     var origen: String = "",
     var destino: String = "",
     var fechaInicio: LocalDateTime,
     var cantidadPasajeros: Int = 0
) {
     @Id
     lateinit var id: String

     fun asignarId(){
          this.id = idPasajero.toString()
     }
}
```

#### Paso 2: Crear el service de UltimaBusquedaDeUnViaje
Despues de crear el objeto de dominio, tuvimos que crear el service [*UltimaBusquedaDeUnViajeService*](../src/main/kotlin/ar/edu/unsam/phm/service/UltimaBusquedaDeUnViajeService.kt) cuya funcion cumplio la de hacer el llamado del repositorio para guardar una nueva consulta en nuestra base de datos y a su vez tambien para realizar la llamada para poder obtener la ultima busqueda de un viaje de un pasajero en especifico.
```kt
@Service
class UltimaBusquedaDeUnViajeService {
    @Autowired
    lateinit var ultimaBusquedaDeUnViajeRepository: UltimaBusquedaDeUnViajeRepository

    fun crearUltimaBusquedaDeUnViaje(idPasajero:Long, consultaDeViaje: DetalleViajeDTO){
        val ultimaBusqueda = UltimaBusquedaDeUnViaje(idPasajero, consultaDeViaje.origen,consultaDeViaje.destino,
            LocalDateTime.parse(consultaDeViaje.fechaInicio),consultaDeViaje.cantidadDePasajeros).apply {
          this.asignarId()
        }
        ultimaBusquedaDeUnViajeRepository.save(ultimaBusqueda)
    }

    fun buscarUltimaBusquedaDeUnViajePorId(id: String): UltimaBusquedaDeUnViajeDTO {
        val ultimaBusquedaRealizada = ultimaBusquedaDeUnViajeRepository.findById(id).orElseThrow { throw NotFoundException("No se encontro un viaje con un id") }
        return ultimaBusquedaRealizada.toUltimaBusquedaDeUnViaje()
    }

    fun comprobarUltimaBusquedaDeUnViaje(idPasajero: Long):UltimaBusquedaDeUnViajeDTO{
        val id = idPasajero.toString()
        return if (ultimaBusquedaDeUnViajeRepository.existsById(id)){
            buscarUltimaBusquedaDeUnViajePorId(id)
        }else{
            return UltimaBusquedaDeUnViajeDTO("","","",0)
        }
    }
}
```

#### Paso 3: Crear el repositorio UltimaBusquedaDeUnViaje
A continuacion creamos el repositorio [*UltimaBusquedaDeUnViajeRepository*](../src/main/kotlin/ar/edu/unsam/phm/repository/UltimaBusquedaDeUnViajeRepository.kt) que nos sirvio para poder guardar nuestros nuevos objetos de dominios de UtlimaBusquedaDeUnViaje y/o realizar consultas en nuestra base de datos de redis.
```kt
@Repository
interface UltimaBusquedaDeUnViajeRepository: CrudRepository<UltimaBusquedaDeUnViaje, String> {
}
```

#### Paso 4: Cambios en el controller de Pasajero
En nuestro controller de [*PasajeroController*](../src/main/kotlin/ar/edu/unsam/phm/controller/PasajerosController.kt) creamos un nuevo metodo que nos permitia hacer el llamado necesario al correspondiente service de pasajero para poder obtener la ultima busqueda de un viaje de cierto pasajero.
```kt
@GetMapping("/home/formulario")
fun getUltimaBusquedaDeUnViaje(@RequestParam idPasajero: Long): UltimaBusquedaDeUnViajeDTO{
    return pasajerosService.buscarUltimaBusquedaDeUnPasajero(idPasajero)
}
```

#### Paso 5: Cambios en el service de Pasajero
En nuestro service de [*PasajeroService*](../src/main/kotlin/ar/edu/unsam/phm/service/PasajerosService.kt) creamos un nuevo metodo que nos sirvio llamar al correspondiente service de ultima busqueda de un viaje, que nos devuelve la ultima busqueda de un viaje del pasajero requerido.
```kt
fun buscarUltimaBusquedaDeUnPasajero(idPasajero: Long): UltimaBusquedaDeUnViajeDTO{
    return this.ultimaBusquedaDeUnViajeService.comprobarUltimaBusquedaDeUnViaje(idPasajero)
}
```
### Cambios que tuvimos que hacer sobre lo que ya existia
Los cambios que tuvimos que hacer fueron en los metodos involucrados a la hora de que el pasajero quiera consultar los choferes disponibles, en nuestro caso, dicho metodo que tuvimos que adaptar para cumplir con lo requerido se encuentra en [*ChoferService*](../src/main/kotlin/ar/edu/unsam/phm/service/ChoferesService.kt) que nos permite saber que choferes estan disponibles a la hora que quiere realizar el viaje nuestro pasajero.
```kt
fun choferesDisponibles(viaje: DetalleViajeDTO, idPasajero: Long): List<TarjetaChoferDTO> {
    val pseudoViaje: Viaje = Viaje().apply{
        duracion = viaje.duracion
        cantidadPasajeros = viaje.cantidadDePasajeros
        fechaInicio = Formateador().formatoLocalDateTime(viaje.fechaInicio)
        this.asignarFechaFinalizacion()
    }
    this.ultimaBusquedaDeUnViajeService.crearUltimaBusquedaDeUnViaje(idPasajero,viaje)
    val choferesDisponibles: List<Chofer> = buscarChoferesDiponibles(pseudoViaje.fechaInicio,pseudoViaje.fechaFinalizacion)
    return choferesDisponibles.map { it.toTarjetaChoferDTO(pseudoViaje)}
}
```
Donde lo unico que modificamos fue agregarle el llamado correspondiente al service de UltimaBusquedaDeUnViaje para crear la nueva busqueda de viaje realizada por el pasajero en nuestra base de datos de redis.

## Conclusiones
En esta entrega usamos Redis para guardar la última búsqueda de un viaje que hizo cada pasajero. Pudimos aprender que Redis es una base de datos clave-valor  muy rápida y simple.

Una ventaja que nos brinda Redis es que permite asignarle un tiempo de vida a los datos (time to live) , por lo que la búsqueda se borrará después de las 6 horas. Es útil aplicarlo porque no se necesita guardar esa información para siempre, solo mientras le sirva al pasajero.

Aprendimos que es una base de datos que está dirigida a un propósito particular, ya que en este caso conviene guardar los datos en forma temporal en vez de una base de datos tradicional. Tienen que ser rápidas, y no permanente, sirve para guardar datos de sesiones, para modelar cache o en nuestro caso búsquedas recientes. 

Destacamos que aprendimos otro tipo de base de datos no relacional de manejo de información temporal y lo importante de elegir la tecnología adecuada según el problema que queremos resolver.  



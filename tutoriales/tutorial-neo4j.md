# Tutorial de Neo4j
## Pre-requisitios previos
Uno de los requisitios que solicitaban en nuestra entrega era tener una conexion con la base de datos de neo4j, para esto antes de comenzar con el trabajo requerido, tuvimos que hacer configuraciones en los siguientes archivos de nuestro proyecto.

### Paso 1: Agregar a nuestro docker-compose.yml
En nuestro [*docker-compose.yml*](../docker-compose.yml) tuvimos que agregar las siguientes lineas para poder configurar nuestro servicio de base de datos para que se levante con nuestros servicios ya configurados previamente.
```yml
services:
    ...
    neo4j:
    image: neo4j:latest
    restart: unless-stopped
    ports:
      - "7474:7474"
      - "7687:7687"
    environment:
      # Raise memory limits
      server.memory.pagecache.size: 1G
      server.memory.heap.max_size: 1G
      # auth
      NEO4J_ACCEPT_LICENSE_AGREEMENT: "yes"
      NEO4J_AUTH: neo4j/passw0rd
```

### Paso 2: Agregar a nuestro application.yml
Tambien tuvimos que agregar lo siguiente en nuestro [*application.yml*](../src/main/resources/application.yml) para realizar la conexion necesaria con nuestra base de datos de neo4j en nuestro docker.
```yml
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: passw0rd
```


### Paso 3: Agregar las dependecias correspondientes en nuestro build.gradle.kts
En nuestro [*build.gradle.kts*](../build.gradle.kts) tuvimos que agregar las siguientes dependecias necesarias para poder llevar acabo nuestro trabajo con la base de datos de neo4j y sus correspondientes test.
```kts
dependencies{
  ...
  implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
  testImplementation("org.neo4j.test:neo4j-harness:2025.01.0"){
      // exlude el módulo JAXB de Jackson
      exclude("com.fasterxml.jackson.module", "jackson-module-jaxb-annotations")
  }
  implementation("org.springframework.boot:spring-boot-starter-log4j2")
  modules {
      module("org.springframework.boot:spring-boot-starter-logging") {
          replacedBy("org.springframework.boot:spring-boot-starter-log4j2", "Use Log4j2 instead of Logback")
      }
  }
  ...
}

configurations {
    all {
        exclude(group = "com.fasterxml.jackson.module", module = "jackson-module-jaxb-annotations")
    }
    testImplementation {
        exclude("org.springframework.boot","spring-boot-starter-logging")
        exclude("ch.qos.logback", "logback-classic")
    }
}
```

## Explicacion de lo que hicimos para completar lo pedido en la entrega
Lo pedido en nuestra entrega fue lo siguiente.

*El principal motivo es realizar las sugerencias de amigos teniendo en cuenta a la persona y sus viajes. En la búsqueda para agregar amigos en el perfil de usuario tiene que mostrar los amigos de los amigos del usuario que haya viajado con los mismos choferes.*

En los cambios que realizamos para la entrega de Neo4j podemos mencionar:

* La creación de nuevos objetos de dominios que nos sirvieron para guardar la información que necesitamos para poder realizar este trabajo. Agregamos la anotation @Relationship esto nos permitió definir las relaciones de manera clara con los distintos objetos usando “AMIGO_DE” Y “VIAJO_CON”. También agregamos la anotation @Node en PasajeroAmigo y ChoferDeRelacionDeViaje para que sean los nodos correspondientes de neo4j y la anotation @RelationshipProperties para poder crear el arco relacion de RelacionDeViaje.  

* La modificación del dominio, creamos primero al pasajero en postgres luego pasamos  la info que necesitamos a neo4j y hacemos lo mismo para algunos datos del viaje y del chofer para poder crear las relaciones que necesitamos. 

* Creamos un nuevo DTO específico que nos ayuda a transferir la información necesaria entre las capas de la aplicación y de esta forma brindar los datos que se necesitan para la respuesta relacionadas con el grafo.

* La modificación del service, fue mínima más que nada fue agregar los nuevos repos que necesitábamos tener comunicación.
Agregamos repositorios para las nuevas entidades que necesitábamos persistir en Neo4j.

* La modificación del controller también fue mínima fue adaptar algunos parámetros que pasamos para las consultas o las modificaciones, ejemplo como el dto que tuvimos que agregar.

## Conclusiones
En la última parte del trabajo usamos Neo4j para guardar y consultar datos relacionados con los pasajeros y sus amigos, aprovechando que es una base de datos de grafos.

Las bases de datos de grafos son realizadas a base de nodos y relaciones y son cómodos de navegar, por lo que nos permite representar información relacionada y realizar búsquedas recursivas, como en nuestro caso particular, buscar al amigo del amigo del pasajero que haya viajado con un mismo chofer.

En nuestro caso particular, decidimos que tanto chofer como pasajero sean nodos y que el viaje sea una relación que posea atributos tales como su fecha de inicio, finalización y duración. Vale aclarar que todas las relaciones van en un solo sentido.
Al principio, cuando empezamos a modelar lo quisimos hacer todo dentro del mismo dominio que ya teníamos, pensamos que iba a ser más simple y que podíamos aprovechar lo que ya teníamos. 

Pero después, de idas y vueltas no funcionaba, mezclar el modelado de grafos con el de las otras bases de datos  traía problemas “Neo4j no quiere que todo esté junto sino que prefiere separado.“A Neo4j no le gusta que le digan cuales van a ser sus ID, sino que es mejor dejar que maneje y se los genere solito”. 

También aprendimos que, por ejemplo, el “viaje” al principio lo pusimos como un nodo (anémico al no tener datos) pero después entendimos que tenía más sentido como una relación entre pasajero y chofer ya que sirve para conectar. 
En otras palabras, el viaje no es un nodo, sino que es el arco o relación que une  un pasajero con un chofer.

El hecho de usar varias bases de datos en el mismo proyecto nos hizo darnos cuenta de que no existe una única solución que sirva para todo. Cada tecnología tiene sus ventajas y limitaciones.

Neo4j sirve para manejar datos complejos y muy relacionados, permitiendo hacer búsquedas recursivas y descubrir relaciones indirectas.


# Uberto Backend - PHM - UNSAM
## Descripcion general
Este proyecto consiste en el desarrollo de un backend utilizando Kotlin y Spring Boot, orientado a soportar una aplicación de gestión de viajes, usuarios y choferes. A lo largo de distintas entregas se fue evolucionando la arquitectura, incorporando múltiples tecnologías de persistencia, seguridad y optimización de rendimiento.

El objetivo general fue construir una API robusta, escalable y modular, integrando bases de datos relacionales, no relacionales, sistemas de cache y modelos de grafos, demostrando la aplicación práctica de distintos paradigmas de almacenamiento según el problema a resolver. Además, se incluyeron pruebas de integración, contenedorización y buenas prácticas de arquitectura backend.

## Tecnologías Utilizadas
Durante el desarrollo del proyecto se emplearon las siguientes tecnologías:
- IntelliJ IDEA - IDE utilizado para el proyecto
- Kotlin — Lenguaje principal de desarrollo
- Spring Boot — Framework para construcción de APIs REST
- PostgreSQL — Persistencia relacional principal
- MongoDB — Persistencia no relacional orientada a documentos
- Redis — Persistencia temporal clave/valor
- Neo4j — Base de datos orientada a grafos
- JWT — Autenticación y seguridad
- Docker & Docker Compose — Contenerización del entorno
- Tests de integración — Validación de comportamiento entre componentes

## Entregas del Proyecto

### [Entrega N°0: Fundamentos y Persistencia Local](https://docs.google.com/document/d/1DOmO8t1XiTyRFHlktrd3EUZ13lMCmkbPiYymWd-7EIE/edit?tab=t.0#heading=h.cwt8ykvvsbpg)
 La primera entrega tuvo como objetivo establecer las bases arquitectónicas del backend y modelar la lógica principal del dominio de la aplicación. En esta etapa se trabajó exclusivamente con repositorios locales, sin utilizar todavía motores de bases de datos externas.
 
Se implementaron las entidades principales del sistema, como usuarios, choferes y viajes, junto con sus respectivos servicios y controladores REST. Los datos se almacenaban en estructuras en memoria, lo que permitió enfocarse en la organización del proyecto, el diseño de capas y la correcta separación de responsabilidades.

Esta fase resultó clave para definir la estructura general del backend, validar la lógica de negocio y preparar el terreno para futuras integraciones con sistemas de persistencia reales.

### [Entrega N°1: Migración a Persistencia Relacional (PostgreSQL)](https://docs.google.com/document/d/1-Bda2wxreKAst5PaPqUSAhIhaUeMX9gFROcfTJ9a8j8/edit?tab=t.0)
El objetivo de esta entrega fue reemplazar los repositorios locales por una solución de persistencia relacional real, utilizando PostgreSQL.

Esto implicó configurar la integración con Spring Data JPA, modelar entidades persistentes, definir relaciones entre tablas y garantizar operaciones CRUD completas sobre datos reales. Los servicios existentes fueron adaptados para interactuar con la base relacional, asegurando consistencia y transaccionalidad.

La migración permitió consolidar una capa de almacenamiento confiable, estructurada y preparada para escalar, sentando las bases para operaciones más complejas dentro de la aplicación.

### [Entrega N°2: Integración de MongoDB y Arquitectura Híbrida](https://docs.google.com/document/d/1PVRxFXt0INaY3pZNm8UQfmLATaZxwOvjpDnnS5wEdqM/edit?tab=t.0#heading=h.cwt8ykvvsbpg)
En esta etapa se incorporó una base de datos no relacional para abordar nuevos requerimientos de almacenamiento. El objetivo principal fue migrar la gestión de choferes hacia MongoDB, aprovechando su flexibilidad para manejar datos documentales.

Dado que los viajes continuaron almacenándose en PostgreSQL, surgió la necesidad de integrar ambos modelos de persistencia. Para ello se implementó una entidad intermedia denominada **ChoferDeViaje**, que actúa como puente entre la información del chofer en MongoDB y los viajes registrados en la base relacional.

Además, se generaron copias de los viajes asociados a cada chofer dentro de MongoDB, permitiendo consultas eficientes sin depender constantemente de PostgreSQL. Esta solución demostró el uso práctico de una arquitectura híbrida, combinando fortalezas de sistemas relacionales y no relacionales para optimizar el acceso a datos.

### [Entrega N°3: Persistencia Temporal y Modelado de Relaciones](https://docs.google.com/document/d/1E08xL_BTXjd4wl4hDSDe_GIt4L8J6BTiFM1Fq23gR7o/edit?tab=t.0)
La última entrega amplió la arquitectura incorporando mecanismos avanzados de persistencia y análisis de relaciones, divididos en dos partes.

#### Parte N°1 — Redis: Persistencia temporal de búsquedas
Se integró una tercera alternativa de persistencia utilizando una base de datos no relacional de tipo clave/valor. El objetivo fue almacenar temporalmente la última búsqueda realizada por el usuario, mejorando la experiencia al reingresar a la aplicación.

Cada búsqueda persiste en Redis con un time-to-live (TTL) de 18.000 segundos (5 horas). Al iniciar la aplicación, el sistema verifica si existe información válida almacenada y, de ser así, reconstruye el estado correspondiente sin necesidad de repetir consultas completas.

La implementación se realizó mediante la integración de Redis con Spring Boot (o alternativamente usando el driver Jedis), demostrando el uso de persistencia temporal orientada a optimización de rendimiento y experiencia de usuario

#### Parte N°2 — Neo4j: Persistencia de grafos y sugerencias de amistades
Se incorporó una base de datos orientada a grafos con el objetivo de modelar relaciones complejas entre usuarios y sus interacciones dentro del sistema. Neo4j permitió persistir usuarios y vínculos derivados de sus viajes.

El propósito principal fue implementar un sistema de sugerencias de amistades basado en relaciones reales. Al buscar nuevos amigos desde el perfil de un usuario, el backend analiza los amigos de sus amigos que hayan compartido viajes con los mismos choferes, generando recomendaciones relevantes y contextualizadas.

Este enfoque demuestra el valor de los grafos para modelar redes sociales y relaciones complejas, facilitando consultas que serían más difíciles o costosas en modelos relacionales tradicionales.

## Ejecución del Proyecto
### Levantar servicios
```cmd
docker-compose up
```

Esto iniciará PostgreSQL, MongoDB, Redis y Neo4j.

### Ejecutar la aplicación
```cmd
./gradlew bootRun
```

### Ejecutar tests
```cmd
./gradlew test
```

## Integrantes
- Rodriguez, Lucas
- Mecozzi, Tamara
- Coronel, Carolina
- Murgia, Pablo
- Cejas, Lucas





				




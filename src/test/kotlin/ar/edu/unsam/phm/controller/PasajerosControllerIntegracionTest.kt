package ar.edu.unsam.phm.controller

import TestSecurityConfig
import ar.edu.unsam.phm.dto.ConsultaDeViajeDTO
import ar.edu.unsam.phm.dto.DetalleViajeDTO
import ar.edu.unsam.phm.extras.Formateador
import ar.edu.unsam.phm.service.PasajerosService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integracion")
@Import(TestSecurityConfig::class)

class PasajerosControllerIntegracionTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
    }

    companion object {
        var mapper = ObjectMapper()
        lateinit var embeddedDatabaseServer: Neo4j

        @BeforeAll
        @JvmStatic
        fun initializeNeo4j() {
            embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .build()
        }

        @AfterAll
        @JvmStatic
        fun stopNeo4j() {
            embeddedDatabaseServer.close()
        }

        @DynamicPropertySource
        @JvmStatic
        fun neo4jProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.neo4j.uri", embeddedDatabaseServer::boltURI)
            registry.add("spring.neo4j.authentication.username") { "neo4j" }
            registry.add("spring.neo4j.authentication.password") { null }
        }
    }

    @ParameterizedTest(name = "ID pasajero = {0}, nombre esperado = {1}")
    @CsvSource(
        "1, Pablo",
        "3, Carlos"
    )
    @DisplayName("Debe retornar los datos del pasajero cuando el ID es válido")
    fun informacion_shouldReturnDatos_whenIdEsValido(id: Long, nombreEsperado: String) {
        // Arrange
        val url = "/pasajero/perfil/informacion"

        // Act
        val result = mockMvc.perform(
            get(url)
                .param("idPasajero", id.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        val json = objectMapper.readTree(result.response.contentAsString)

        assertEquals(id.toInt(), json["id"].asInt())
        assertEquals(nombreEsperado, json["nombre"].asText())
        assertNotNull(json["apellido"])
        assertNotNull(json["telefono"])
        assertNotNull(json["saldo"])
        assertNotNull(json["fotoPerfil"])
    }


    @ParameterizedTest(name = "ID pasajero inexistente = {0}")
    @CsvSource("99", "1000")
    @DisplayName("Debe devolver error 404 cuando el pasajero no existe")
    fun informacion_shouldReturn404_whenPasajeroNoExiste(id: Long) {
        val resultado = mockMvc.perform(
            get("/pasajero/perfil/informacion")
                .param("idPasajero", id.toString())
        )
            .andExpect(status().isNotFound)
            .andReturn()

        val mensaje = resultado.resolvedException?.message
        assertEquals("No se encontró el pasajero indicado: $id", mensaje)
    }


    @ParameterizedTest(name = "ID pasajero = {0}, monto = {1}")
    @CsvSource(
        "1, 500.0",
        "2, 150.0"
    )
    @DisplayName("Debe actualizar correctamente el saldo del pasajero cuando el monto es válido")
    fun actualizarSaldo_shouldUpdateSaldo_whenMontoEsValido(id: Long, monto: Double) {
        // Arrange
        val putUrl = "/pasajero/perfil/actualizarsaldo"
        val getUrl = "/pasajero/perfil/informacion"

        // Act
        mockMvc.perform(
            put(putUrl)
                .param("idPasajero", id.toString())
                .param("saldo", monto.toString())
        )
            .andExpect(status().isOk)

        val result = mockMvc.perform(
            get(getUrl)
                .param("idPasajero", id.toString())
        )
            .andReturn()

        // Assert
        val json = objectMapper.readTree(result.response.contentAsString)
        val saldoActualizado = json["saldo"].asDouble()
        assertTrue(saldoActualizado >= monto)
    }


    @ParameterizedTest(name = "Monto inválido = {1} → error 400")
    @CsvSource(
        "1, -100.0",
        "2, -3000.0"
    )

    //Rompe por cambio de metodo en el update de pasajero, no se porque cuando ingresas un numero negativo te da 200 igual
    //esto es a causa del cambio que nos habia planteado juan que el update del saldo se realice a travez de la base de datos de postgres
    //y no desde el dominio del pasajero.
    @DisplayName("Debe devolver error 400 cuando el monto es inválido")
    fun actualizarSaldo_shouldReturn400_whenMontoInvalido(id: Long, monto: Double) {
        mockMvc.perform(
            put("/pasajero/perfil/actualizarsaldo")
                .param("idPasajero", id.toString())
                .param("saldo", monto.toString())
        )
            .andExpect(status().isBadRequest)

    }

    @ParameterizedTest(name = "ID inexistente = {0}, monto = {1} → error 404")
    @CsvSource(
        "99, 200.0",
        "1000, 300.0"
    )
    @DisplayName("Debe devolver error 404 cuando el pasajero no existe")
    fun actualizarSaldo_shouldReturn404_whenIdInexistente(id: Long, monto: Double) {

        mockMvc.perform(
            put("/pasajero/perfil/actualizarsaldo")
                .param("idPasajero", id.toString())
                .param("saldo", monto.toString())
        )
            .andExpect(status().isNotFound)
    }


    @ParameterizedTest(name = "Viaje ID = {0}, puntaje = {1}, comentario = {2}")
    @CsvSource(
        "14, 5, 'Excelente viaje'"
    )
    @DisplayName("Debe registrar una calificación cuando el viaje es válido y no está calificado")
    fun calificarViaje_shouldRegisterCalificacion_whenViajeEsValidoYNoCalificado(
        idViaje: Long,
        puntaje: Int,
        comentario: String
    ) {
        // Arrange
        val dto = mapOf(
            "idViaje" to idViaje,
            "puntaje" to puntaje,
            "comentario" to comentario
        )

        // Act
        val result = mockMvc.perform(
            post("/pasajero/perfil/calificar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andReturn()

        val responseBody = result.response.contentAsString

        // Solo si hay body, se valida el contenido
        if (responseBody.isNotBlank()) {
            val responseJson = objectMapper.readTree(responseBody)
            assertEquals(puntaje, responseJson["puntaje"].asInt())
            assertEquals(comentario, responseJson["comentario"].asText())
            assertEquals(idViaje, responseJson["viaje"]["id"].asLong())
        }
    }

    @ParameterizedTest(name = "Viaje ID = {0}, puntaje = {1}, comentario = {2}, estado esperado = {3}")
    @CsvSource(
        "99, 4, 'No existe', 404",
        "9, 5, 'Ya calificado', 400",
        "13, 3, '', 400"
    )
    @DisplayName("Debe devolver errores adecuados al calificar un viaje inválido")
    fun calificarViaje_shouldReturnError_whenDatosInvalidosOYaCalificado(
        idViaje: Long,
        puntaje: Int,
        comentario: String,
        expectedStatus: Int
    ) {
        val dto = mapOf(
            "idViaje" to idViaje,
            "puntaje" to puntaje,
            "comentario" to comentario
        )

        val result = mockMvc.perform(
            post("/pasajero/perfil/calificar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().`is`(expectedStatus))
            .andReturn()

        val mensaje = result.resolvedException?.message ?: ""

        when (expectedStatus) {
            404 -> assertEquals("No se encontró el viaje indicado: $idViaje", mensaje)
            400 -> assertTrue(
                mensaje.contains("calific", ignoreCase = true) ||
                        mensaje.contains("campos", ignoreCase = true),
                "Esperado 400 con mensaje relevante"
            )
        }
    }


    @ParameterizedTest(name = "Pasajero ID = {0}, amigo a agregar ID = {1}")
    @CsvSource(
        "1, 4",
        "2, 4"
    )
    @DisplayName("Debe agregar un nuevo amigo cuando el usuario aún no es su amigo")
    fun amigo_shouldAddSuccessfully_whenEsUnNuevoAmigo(idPasajero: Long, idAmigo: Long) {
        mockMvc.perform(
            post("/pasajero/perfil/agregaramigo")
                .param("idPasajero", idPasajero.toString())
                .param("idAmigo", idAmigo.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().string(""))
    }


    @ParameterizedTest(name = "Pasajero ID = {0}, amigo a agregar ID = {1}, error esperado = {2}")
    @CsvSource(
        "1, 4, 400",
        "1, 99, 404"
    )
    @DisplayName("Debe devolver error al intentar agregar a un amigo ya existente o inexistente")
    fun amigo_shouldReturnError_whenYaEsAmigoOInexistente(idPasajero: Long, idAmigo: Long, expectedStatus: Int) {
        mockMvc.perform(
            post("/pasajero/perfil/agregaramigo")
                .param("idPasajero", idPasajero.toString())
                .param("idAmigo", idAmigo.toString())
        )
            .andExpect(status().`is`(expectedStatus))
    }


    @ParameterizedTest(name = "Pasajero ID = {0}, amigo a agregar ID = {1},error esperado = {2}")
    @CsvSource(
        "1, 2,200",
    )
    @DisplayName("Debe dar 200 al elminar el amigo y este debe aparecer en mostrarAmigos")
    fun pasajero_deberia_poder_eliminar_amigo(idPasajero: Long, idAmigo: Long, expectedStatus: Int) {

        //

        mockMvc.perform(
            delete("/pasajero/perfil/eliminaramigo")
                .param("idPasajero", idPasajero.toString())
                .param("idAmigo", idAmigo.toString())
        )
            .andExpect(status().`is`(expectedStatus))

        // pense que quiza podiamos usar tambien la funcion verificarEliminarAmigo() que est en el service pero para eso necesito que la funcion sea publica
    }


    @ParameterizedTest(name = "Pasajero ID = {0}, amigo a eliminar ID = {1},error esperado = {2}")
    @CsvSource(
        "3, 2,400",
    )
    @DisplayName("Debe dar error al intentar elminar un amigo que no esta entre sus amigos")
    fun deberia_dar_error_cuando_no_es_amigo(idPasajero: Long, idAmigo: Long, expectedStatus: Int) {

        //

        mockMvc.perform(
            delete("/pasajero/perfil/eliminaramigo")
                .param("idPasajero", idPasajero.toString())
                .param("idAmigo", idAmigo.toString())
        )
            .andExpect(status().`is`(expectedStatus))

        // pense que quiza podiamos usar tambien la funcion verificarEliminarAmigo() que est en el service pero para eso necesito que la funcion sea publica
    }




    @ParameterizedTest(name = "ID pasajero = {0}, nombre = {1}, apellido = {2}, telefono = {3}")
    @CsvSource( "1, Pablo, Garcia, 1169591337",   "2, Laura, Lopez, 1137894568" )
    @DisplayName("Debe actualizar correctamente el nombre, apellido y el telefono del pasajero cuando los datos son válidos")
    fun actualizarDatos_shouldUpdateDatosPasajero_whenCamposValidos(id: Long, nombre: String, apellido: String, telefono: String) {

        // Arrange
        val getUrl = "/pasajero/perfil/informacion"

        val originalResult = mockMvc.perform(get(getUrl).param("idPasajero", id.toString())).andExpect(status().isOk).andReturn()

        val originalJson = objectMapper.readTree(originalResult.response.contentAsString)

        val datosActualizados = mapOf(
            "idUserData" to id,
            "fotoPerfil" to originalJson["fotoPerfil"].asText(),
            "nombre" to nombre,
            "apellido" to apellido,
            "telefono" to telefono,
            "saldo" to originalJson["saldo"].asDouble(),
            "listaAmigos" to originalJson["listaAmigos"]
        )

        val requestBody = objectMapper.writeValueAsString(datosActualizados)

        val putUrl = "/pasajero/perfil/actualizardatos"

        // Act
        mockMvc.perform(put(putUrl).contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isOk)

        val result = mockMvc.perform(get(getUrl).param("idPasajero", id.toString())).andExpect(status().isOk).andReturn()

        val json = objectMapper.readTree(result.response.contentAsString)

        // Assert
        assertEquals(nombre, json["nombre"].asText(), "El nombre no se actualizó correctamente.")
        assertEquals(apellido, json["apellido"].asText(), "El apellido no se actualizó correctamente.")
        assertEquals(telefono, json["telefono"].asText(), "El teléfono no se actualizó correctamente.")

        // Verificación opcional: Asegurarse que no se modificaron campos críticos como 'saldo'
        assertEquals(originalJson["saldo"].asDouble(), json["saldo"].asDouble(), 0.01, "El saldo se ha modificado cuando no debería.")
    }

    @ParameterizedTest(name = "ID = {0}, nombre = {1}, apellido = {2}, teléfono = {3}, status esperado = {4}")
    @CsvSource("1, , Garcia, 1169591337, 400",   "90, Ana, Lopez, 1137894568, 404" )
    @DisplayName("Actualizar datos debe fallar si los datos son inválidos o el pasajero no existe")
    fun actualizarDatos_shouldUpdateDatosPasajero_whenCamposInvalidos(id: Long, nombre: String?, apellido: String?, telefono: String?, expectedStatus: Int) {

        val putUrl = "/pasajero/perfil/actualizardatos"

        val datosActualizados = mapOf(
            "idUserData" to id,
            "fotoPerfil" to "url/foto.png",
            "nombre" to (nombre ?: ""),
            "apellido" to (apellido ?: ""),
            "telefono" to (telefono ?: ""),
            "saldo" to 100.0,
            "listaAmigos" to emptyList<String>()
        )

        val requestBody = objectMapper.writeValueAsString(datosActualizados)

        val result = mockMvc.perform(put(putUrl).contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().`is`(expectedStatus)).andReturn()

        val statusCode = result.response.status

        val mensaje = result.resolvedException?.message ?: result.response.contentAsString

        when (expectedStatus) {
            404 -> {
                assertTrue(
                    mensaje.contains("no se encontró", ignoreCase = true) ||
                            mensaje.contains("no existe", ignoreCase = true),
                    "Esperado 404 para pasajero con ID inexistente"
                )
            }
            400 -> {
                assertTrue(
                    mensaje.contains("campo", ignoreCase = true) ||
                            mensaje.contains("inválido", ignoreCase = true) ||
                            mensaje.contains("nombre", ignoreCase = true),
                    "Esperado 400 por validación de campos inválidos (nombre vacío)"
                )
            }
        }
    }


    @ParameterizedTest(name = "ID pasajero = {0}, origen = {1}, destino = {2}, cantidad de pasajero = {3}")
    @CsvSource(value = ["1, 'federico Lacroze, 4100',  'Calle 123, 4500', 2 "])
    @DisplayName("Debe retornar los viajes pendientes cuando el ID es válido")
    fun viajesPendientes_shouldReturnViajesPendientes_whenIdEsValido(id: Long, origen: String, destino:String, pasajeros:Int) {
        // Arrange
        val cantViajesPendientesBootstrap = 2
        // -> por ahora tiene cargado dos viajes pendientes.

        val url = "/pasajero/perfil/viajespendientes"

        // Act
        val result = mockMvc.perform(get(url).param("idUsuario", id.toString())).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn()

        val jsonArray = objectMapper.readTree(result.response.contentAsString)

        // Assert
        assertNotNull(jsonArray)
        assertEquals(cantViajesPendientesBootstrap, jsonArray.size())

        val primerViajePendiente = jsonArray[0]
        assertEquals(origen, primerViajePendiente["origen"].asText())
        assertEquals(destino, primerViajePendiente["destino"].asText())
        assertEquals(pasajeros, primerViajePendiente["cantidadDePasajeros"].asInt())
    }

    @ParameterizedTest(name = "ID pasajero = {0}, status esperado = {1}")
    @CsvSource("90, 404")
    @DisplayName("Debe lanzar una exepcion cuando no encuentra el id del pasajero a la hora de pedir sus viajes pendientes")
    fun viajesPendiente_shouldReturnViajesPendientes_whenIdNoValido(id: Long, expectedStatus: Int) {
        // Arrange
        val url = "/pasajero/perfil/viajespendientes"
        // Act
        mockMvc.perform(get(url).param("idUsuario", id.toString())).andExpect(status().`is`(expectedStatus))
    }


    @ParameterizedTest(name = "ID pasajero = {0}, origen = {1}, destino = {2}, pasajeros = {3}")
    @CsvSource(value = ["1, 'Amadeo Carizo, 4100',  'Calle 123, 4500', 2 "])
    @DisplayName("Debe retornar los viajes realizados cuando el ID es válido")
    fun viajesRealizados_shouldReturnViajesRealizados_whenIdEsValido(id: Long, origen: String, destino:String, pasajeros:Int) {
        // Arrange
        val cantViajesRealizadosBootstrap = 2
        // -> por ahora tiene cargado dos viajes realizados.

        val url = "/pasajero/perfil/viajesrealizados"

        // Act
        val result = mockMvc.perform(get(url).param("idUsuario", id.toString())).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn()

        val jsonArray = objectMapper.readTree(result.response.contentAsString)

        // Assert
        assertNotNull(jsonArray)
        assertEquals(cantViajesRealizadosBootstrap, jsonArray.size())

        val primerViajePendiente = jsonArray[0]
        assertEquals(origen, primerViajePendiente["origen"].asText())
        assertEquals(destino, primerViajePendiente["destino"].asText())
        assertEquals(pasajeros, primerViajePendiente["cantidadDePasajeros"].asInt())
    }
    
    @ParameterizedTest(name = "ID pasajero = {0}, status esperado = {1}")
    @CsvSource("90, 404")
    @DisplayName("Debe lanzar una exepcion cuando no encuentra el id del pasajero a la hora de pedir sus viajes realizados")
    fun viajesRealizados_shouldReturnViajesRealizados_whenIdNoValido(id: Long, expectedStatus: Int) {
        // Arrange
        val url = "/pasajero/perfil/viajesrealizados"
        // Act
        mockMvc.perform(get(url).param("idUsuario", id.toString())).andExpect(status().`is`(expectedStatus))
    }


    //Chofer no existe, no se puede obtener detalle viaje, 404
    @ParameterizedTest(name = "ID inexistente = {0} → error 404, idPasajero = {1}")
    @CsvSource(
        "6837b06b7f4d960653e4c2daat, 33",
    )
    @DisplayName("Debe devolver error 404 cuando el chofer no existe")
    fun detalleViaje_shouldReturn404_whenIdChoferInexistente(idChofer: String, idPasajero:Long) {

        mockMvc.perform(
            get("/pasajero/home/detalleViaje")
                .param("idChofer", idChofer)
                .param("idPasajero", idPasajero.toString())
        )
            .andExpect(status().isNotFound)
    }

    //Chofer existe, se devuelve el detalle del viaje
    @ParameterizedTest(name = "ID chofer = {0}, idPasajero={1}")
    @CsvSource(
        "6837b06b7f4d960653e4c2dd, 1",
        "6837b06b7f4d960653e4c2de, 2"
    )
    @DisplayName("Debe retornar el detalle del viaje cuando el ID del chofer es válido")
    fun detalle_shouldReturnDatos_whenIdEsValido(idChofer: String, idPasajero:Long) {
        // Arrange
        val url = "/pasajero/home/detalleViaje"

        // Act
        val result = mockMvc.perform(
            get(url)
                .param("idChofer", idChofer)
                .param("idPasajero",idPasajero.toString())
        )
            .andExpect(status().isOk)
            .andReturn()
    }

    //Debe devolver choferes disponibles para un viaje

    @ParameterizedTest(name = "origen = {0}, destino={1}, fechaInicio = {2}, duracion = {3}, cantidadDePasajeros = {4}, idPasajero = {5}")
    @CsvSource(
        "Calle Falopa 123, Calle Merca 123, 2025-07-29T03:42:13.080262, 25, 2, 1",
        "Calle Riquiricon 123, Calle Faustino 123, 2025-09-29T03:42:13.080262, 40, 1, 2"

    )
    @DisplayName("Debe devolver choferes disponibles para un viaje")
    fun homePasajero_shouldReturnChoferes_whenViajeValido(origen:String, destino: String, fechaInicio: String, duracion: Int, cantidadDePasajeros: Int, idPasajero: Long) {
        val viaje = DetalleViajeDTO(
            origen,
            destino,
            fechaInicio,
            duracion,
            cantidadDePasajeros
        )

        mockMvc.perform(
            post("/pasajero/home/choferesdisponibles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(viaje))
                .param("idPasajero", idPasajero.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
    }

    // Debe devolver la ultimaBusquedaDeUnViaje de un pasajero si la ha realizado
    @ParameterizedTest(name = "idPasajero = {0},origen = {1}, destino={2}, fechaInicio = {3}, duracion = {4}, cantidadDePasajeros = {5}")
    @CsvSource(
        "1, Calle Falopa 123, Calle Merca 123, 2025-07-29T03:42:13.080262, 25, 2"
    )
    @DisplayName("Debe devolver la ultimaBusquedaDeUnViaje de un pasajero si la ha realizado")
    fun homePasajero_shouldReturnUltimaBusquedaDeUnViaje_whenPasajeroRealizaViaje(idPasajero: Long, origen:String, destino: String, fechaInicio: String, duracion: Int, cantidadDePasajeros: Int) {
        val viaje = DetalleViajeDTO(
            origen,
            destino,
            fechaInicio,
            duracion,
            cantidadDePasajeros
        )

        mockMvc.perform(
            post("/pasajero/home/choferesdisponibles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(viaje))
                .param("idPasajero", idPasajero.toString())
        )

        val url = "/pasajero/home/formulario"
        val result = mockMvc.perform(
            get(url)
                .param("idPasajero", idPasajero.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andReturn()

        val responseBody = result.response.contentAsString

        // Solo si hay body, se valida el contenido
        if (responseBody.isNotBlank()) {
            val responseJson = objectMapper.readTree(responseBody)
            assertEquals(origen, responseJson["origen"].asText())
            assertEquals(destino, responseJson["destino"].asText())
            assertEquals(fechaInicio, responseJson["fechaInicio"].asText())
            assertEquals(cantidadDePasajeros, responseJson["cantidadPasajeros"].asInt())
        }
    }

    // Debe devolver 400 para fecha mal ingresada de un viaje

    @ParameterizedTest(name = "fechaInicio = {0}, duracion = {1}, cantidadDePasajeros = {2}")
    @CsvSource(
        "2025-15-29T03:42:13.080262, 25, 2",
    )
    @DisplayName("Debe devolver 400 para fecha mal ingresada de un viaje")
    fun homePasajero_shouldReturn400_whenViajeInvalido(fechaInicio: String, duracion: Int, cantidadDePasajeros: Int) {
        val viaje = ConsultaDeViajeDTO(
            fechaInicio = fechaInicio,
            duracion = duracion,
            cantidadDePasajeros = cantidadDePasajeros
        )

        mockMvc.perform(
            post("/pasajero/home/choferesdisponibles")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)

    }

    //Se crea viaje correctamente

    @ParameterizedTest(name = "idChofer = {0}, origen = {1}, destino = {2}, fechaInicio = {3}, duracion = {4}, cantidadDePasajeros = {5}, idPasajero = {6}")
    @CsvSource(
        "6837b06b7f4d960653e4c2dd, Falopa 123, Falopa 1500, 2025-07-29T03:42:13.080262, 25, 2, 1"
    )
    @DisplayName("Debe crearse viaje cuando los datos del viaje son válidos")
    fun confirmarViaje_shouldRegisterViaje_whenViajeEsValido(
        idChofer: String,
        origen: String,
        destino: String,
        fechaInicio: String,
        duracion: Int,
        cantidadDePasajeros: Int,
        idPasajero: Long
    ) {
        // Arrange
        val dto = mapOf(
            "origen" to origen,
            "destino" to destino,
            "fechaInicio" to fechaInicio,
            "duracion" to duracion,
            "cantidadDePasajeros" to cantidadDePasajeros
        )

        // Act
        val result = mockMvc.perform(
            post("/pasajero/home/confirmarviaje")
                .param("idChofer", idChofer)
                .param("idPasajero", idPasajero.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andReturn()

        val responseBody = result.response.contentAsString

        // Solo si hay body, se valida el contenido
        if (responseBody.isNotBlank()) {
            val responseJson = objectMapper.readTree(responseBody)
            assertEquals(origen, responseJson["origen"].asInt())
            assertEquals(destino, responseJson["destino"].asText())
            assertEquals(fechaInicio, responseJson["fechaInicio"].asText())
            assertEquals(duracion, responseJson["duracion"].asText())
            assertEquals(cantidadDePasajeros, responseJson["cantidadDePasajeros"].asText())

        }
    }

    //No se crea viaje porque chofer no existe

    @ParameterizedTest(name = "idChofer = {0}, origen = {1}, destino = {2}, fechaInicio = {3}, duracion = {4}, cantidadDePasajeros = {5}, idPasajero = {6}")
    @CsvSource(
        "100, Falopa 123, Falopa 1500, 2025-07-29T03:42:13.080262, 25, 2, 1"
    )
    @DisplayName("No se crea viaje porque chofer no existe")
    fun confirmarViaje_shouldReturn404_whenChoferInvalido(
        idChofer: Long,
        origen: String,
        destino: String,
        fechaInicio: String,
        duracion: Int,
        cantidadDePasajeros: Int,
        idPasajero: Long
    ) {
        // Arrange
        val dto = mapOf(
            "origen" to origen,
            "destino" to destino,
            "fechaInicio" to fechaInicio,
            "duracion" to duracion,
            "cantidadDePasajeros" to cantidadDePasajeros
        )

        // Act
        val result = mockMvc.perform(
            post("/pasajero/home/confirmarviaje")
                .param("idChofer", idChofer.toString())
                .param("idPasajero", idPasajero.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isNotFound)
            .andReturn()
    }

    //Debe devolver calificaciones creadas por pasajero

    @ParameterizedTest(name = "Pasajero ID = {0}")
    @CsvSource("1", "2")
    @DisplayName("Debe devolver calificaciones cuando el pasajero tiene viajes calificados")
    fun calificaciones_shouldReturnOk_whenPasajeroTieneCalificaciones(idPasajero: Long) {
        mockMvc.perform(
            get("/pasajero/perfil/calificaciones")
                .param("idUsuario", idPasajero.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    //Debe devolver 404 cuando se piden calificaciones de usuario inexistente

    @ParameterizedTest(name = "Pasajero ID = {0}")
    @CsvSource("100", "20")
    @DisplayName("Debe devolver 404 cuando se piden calificaciones de usuario inexistente")
    fun calificaciones_shouldReturn404_whenPasajeroInexistente(idPasajero: Long) {
        mockMvc.perform(
            get("/pasajero/perfil/calificaciones")
                .param("idUsuario", idPasajero.toString())
        )
            .andExpect(status().isNotFound)
    }

    //Debe eliminar calificacion correctamente si existe

    @ParameterizedTest(name = "Calificacion ID = {0}")
    @CsvSource(
        "1, 3",

        )
    @DisplayName("Debe dar 200 al elminar la calificacion si existe")
    fun calificaciones_shouldReturn200_whenEliminaCalificacionCorrectamnete(idCalificacion: Long) {

        //

        mockMvc.perform(
            delete("/pasajero/perfil/eliminarCalificacion")
                .param("idCalificacion", idCalificacion.toString())
        )
            .andExpect(status().isOk)
    }

    //Debe devolver 404 si se pide eliminar calificacion que no existe

    @ParameterizedTest(name = "Calificacion ID = {0}")
    @CsvSource(
        "10, 30",

        )
    @DisplayName("Debe dar 404 al querer elminar la calificacion que no existe")
    fun calificaciones_shouldReturn404_whenNoSePuedeEliminarCalificacionInexistente(idCalificacion: Long) {

        //

        mockMvc.perform(
            delete("/pasajero/perfil/eliminarCalificacion")
                .param("idCalificacion", idCalificacion.toString())
        )
            .andExpect(status().isNotFound)
    }

    //Debe devolver amigos de un pasajero existente

    @ParameterizedTest(name = "Pasajero ID = {0}")
    @CsvSource(
        "1","3"
    )
    @DisplayName("Debe devolver amigos de un pasajero existente")
    fun perfilPasajero_shouldReturnAmigos_whenPasajeroValido(idPasajero: Long) {

        mockMvc.perform(
            get("/pasajero/perfil/mostraramigos")
                .param("idPasajero", idPasajero.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
    }

    //Debe devolver 404 cuando se consulta amigos de pasajero inexistente

    @ParameterizedTest(name = "Pasajero ID = {0}")
    @CsvSource(
        "10","35"
    )
    @DisplayName("Debe devolver 404 cuando se consulta amigos de pasajero inexistente")
    fun perfilPasajero_shouldReturn404_whenPasajeroInvalido(idPasajero: Long) {

        mockMvc.perform(
            get("/pasajero/perfil/mostraramigos")
                .param("idPasajero", idPasajero.toString())
        )
            .andExpect(status().isNotFound)
    }



}
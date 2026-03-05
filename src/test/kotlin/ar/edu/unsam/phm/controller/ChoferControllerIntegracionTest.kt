package ar.edu.unsam.phm.controller

import TestSecurityConfig
import org.bson.types.ObjectId
import ar.edu.unsam.phm.domain.Chofer
import ar.edu.unsam.phm.domain.ChoferMoto
import ar.edu.unsam.phm.domain.ChoferPremium
import ar.edu.unsam.phm.domain.ChoferSimple
import ar.edu.unsam.phm.dto.FiltroViajeDTO
import ar.edu.unsam.phm.dto.InformacionChoferDTO
import ar.edu.unsam.phm.repository.ChoferRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.junit.jupiter.api.Tag
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.collections.set

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integracion")
@Import(TestSecurityConfig::class)

class ChoferesControllerIntegracionTest {

    @Autowired
    lateinit var bootstrapTest: ProyectoBootstrapTest

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var choferRepository: ChoferRepository

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

    @BeforeEach
    fun setup() {
        bootstrapTest.afterPropertiesSet()
//
//        choferRepository.insert(ChoferSimple(
//            userDataId= 5,
//            nombre = "Jorge",
//            apellido = "Lopez",
//            fotoPerfil = "https://media.istockphoto.com/id/1217653245/photo/happy-driver-transporting-a-woman-in-a-car.jpg?s=2048x2048&w=is&k=20&c=1X3XNwqke3g0hb2LhWq0cJMQ21ARyMH-bp5FR6NekJ8=",
//            precioBase=1000.0,
//            modeloVehiculo=1991,
//            marcaVehiculo="Ford",
//            patenteVehiculo="AAA111",
//            promedioDePuntaje=0.0,
//        ).apply{id = "6837b06b7f4d960653e4c2dd"})
//
//        choferRepository.insert(ChoferPremium(
//            userDataId= 6,
//            nombre = "Pepe",
//            apellido = "Lopez",
//            fotoPerfil = "https://plus.unsplash.com/premium_photo-1661402137057-8912a733c52f?q=80&w=2069&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
//            precioBase=1000.0,
//            modeloVehiculo=2020,
//            marcaVehiculo="Fiat",
//            patenteVehiculo="BBB222",
//            promedioDePuntaje=0.0,
//        ).apply { id = "6837b06b7f4d960653e4c2de" })
//
//       choferRepository.insert(ChoferMoto(
//            userDataId= 7,
//            nombre = "Joaquin",
//            apellido = "Hernandez",
//            fotoPerfil = "https://plus.unsplash.com/premium_photo-1664300191065-4dcef93a069c?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
//            precioBase=1000.0,
//            modeloVehiculo=2019,
//            marcaVehiculo="Honda",
//            patenteVehiculo="CCC333",
//            promedioDePuntaje=0.0,
//       ).apply  { id = ObjectId("6837b06b7f4d960653e4c2df").toHexString() })
//
   }

   // @AfterEach
   // fun deleteAll() {
   //     choferRepository.deleteAll()
   // }

    @ParameterizedTest(name = "Chofer ID = {0} → retorna viajes realizados")
    @CsvSource("5"," 6"," 7 ")
    @DisplayName("Debe devolver status 200 y JSON cuando el chofer existe y tiene viajes")
    fun viajesRealizados_shouldReturnOk_whenChoferExiste(idChofer: Long) {
        mockMvc.perform(
            get("/chofer/perfil/viajesrealizados")
                .param("idChofer", idChofer.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest(name = "Chofer ID = {0}")
    @CsvSource("5", "6")
    @DisplayName("Debe devolver calificaciones cuando el chofer tiene viajes calificados")
    fun calificaciones_shouldReturnOk_whenChoferTieneCalificaciones(idChofer: Long) {
        mockMvc.perform(
            get("/chofer/perfil/calificaciones")
                .param("idChofer", idChofer.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest(name = "ID Chofer = {0} → Datos cargados correctamente")
    @CsvSource("5,6837b06b7f4d960653e4c2dd", "6,6837b06b7f4d960653e4c2de", "7,6837b06b7f4d960653e4c2df")
    @DisplayName("Debe obtener correctamente la información del chofer")
    fun datosChofer_shouldReturnInfo_whenIdValido(idUserData: Long, expectedChoferId: String) {
        mockMvc.perform(
            get("/chofer/perfil/informacion")
                .param("idChofer", idUserData.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(expectedChoferId))
    }


    @ParameterizedTest(name = "ID Chofer = {0} → Total calculado")
    @CsvSource("5", "6")
    @DisplayName("Debe devolver el importe total de viajes del chofer")
    fun totalViajes_shouldReturnTotal_whenIdValido(idChofer: Long) {
        mockMvc.perform(
            get("/chofer/perfil/importetotal")
                .param("idChofer", idChofer.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest(name = "Actualizar chofer {0} → éxito")
    @CsvSource(
        "6837b06b7f4d960653e4c2dd, ChoferSimple, fotofalopa.jpg, Juan, Perez, 5000.0, ABC123, Ford, 2015"
    )
    @DisplayName("Debe actualizar la información del chofer con datos válidos")
    fun datosChofer_shouldUpdate_whenDatosValidos(
        id: String, tipoChofer: String, fotoPerfil: String, nombre: String, apellido: String,
        precio: Double, patente: String, marca: String, modelo: Int
    ) {
        val dto = InformacionChoferDTO(
            id = id,
            tipoChofer = tipoChofer,
            fotoPerfil = fotoPerfil,
            nombre = nombre,
            apellido = apellido,
            precioBase = precio,
            patenteVehiculo = patente,
            marcaVehiculo = marca,
            modeloVehiculo = modelo
        )

        mockMvc.perform(
            put("/chofer/perfil/actualizardatos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk)
    }


    @ParameterizedTest(name = "ID Chofer = {0} → No encontrado")
    @CsvSource("999", "888")
    @DisplayName("Debe devolver 404 cuando el ID del chofer no existe")
    fun datosChofer_shouldReturn404_whenIdInexistente(idChofer: Long) {
        val resultado = mockMvc.perform(
            get("/chofer/perfil/informacion")
                .param("idChofer", idChofer.toString())
        )
            .andExpect(status().isNotFound)
            .andReturn()

        val msg = resultado.resolvedException?.message ?: ""
        assertTrue(msg.contains("no se encontró", ignoreCase = true))
    }


    @ParameterizedTest(name = "Chofer ID = {0}, origen = {1}, destino = {2},usuario={3},cantidadDePasajeros = {4}")
    @CsvSource(
        "5, federico Lacroze, Calle 123, juan topo, 3",
        "6, federico Lacroze, Calle 123, lisa simpson, 1"
    )
    @DisplayName("Debe devolver viajes filtrados correctamente para el chofer")
    fun viajesFiltro_shouldReturnViajes_whenFiltroValido(idChofer: Long, origen: String, destino: String, usuario:String, cantidadDePasajeros: Int) {
        val filtro1 = FiltroViajeDTO(
            usuario= usuario,
            origen = origen,
            destino = destino,
            cantidadDePasajeros = cantidadDePasajeros
        )

        mockMvc.perform(
            post("/chofer/home/filtro")
                .param("idChofer", idChofer.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filtro1))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
    }

    @ParameterizedTest(name = "Chofer ID = {0}, origen = {1}, destino = {2}")
    @CsvSource(
        "1, Falopa Lacroze, ''",
        "2, '', Calle 321"
    )
    @DisplayName("Debe devolver 404 cuando el chofer no existe al aplicar filtro")
    fun viajesFiltro_shouldReturn404_whenChoferInexistente(idChofer: Long, origen: String, destino: String) {
        val filtro = FiltroViajeDTO(
            origen = origen,
            destino = destino
        )

        val result = mockMvc.perform(
            post("/chofer/home/filtro")
                .param("idChofer", idChofer.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filtro))
        )
            .andExpect(status().isNotFound)
            .andReturn()

        val errorMsg = result.resolvedException?.message ?: ""
        assertTrue(errorMsg.isNotBlank(), "Se esperaba un mensaje de error no vacío para 404")
    }

}

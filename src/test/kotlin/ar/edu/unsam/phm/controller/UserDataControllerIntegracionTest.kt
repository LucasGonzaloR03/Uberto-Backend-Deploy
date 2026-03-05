package ar.edu.unsam.phm.controller

import TestSecurityConfig
import ar.edu.unsam.phm.dto.RefreshTokenRequestDTO
import ar.edu.unsam.phm.dto.UsuarioDataDTO
import ar.edu.unsam.phm.dto.UsuarioLoginDTO
import ar.edu.unsam.phm.service.PasajerosService
import ar.edu.unsam.phm.service.UserDataService
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
class UserDataControllerIntegracionTest {


    @Autowired
    private lateinit var userDataService: UserDataService

    @Autowired
    private lateinit var pasajerosService: PasajerosService

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

    @ParameterizedTest(name = "username = {0}, contrasenia = {1}")
    @CsvSource(
        "Pgarcia, 1234",
        "JLopez, 1234"
    )
    @DisplayName("Login exitoso debería retornar 200 OK y los datos del usuario")
    fun loginExitoso_deberiaRetornarUsuario(username: String, contrasenia: String) {

        val loginRequest = UsuarioLoginDTO(username, contrasenia)
        // Arrange
        val url = "/api/auth/login"

        // Act
        val result = mockMvc.perform(
            post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
    }


    @ParameterizedTest(name = "username = {0}, contrasenia = {1},error esperado = {2}")
    @CsvSource(
        "sarasa, 1234,401",
        "JLopez, 1111,401"
    )
    @DisplayName("Login exitoso debería retornar 200 OK y los datos del usuario")
    fun loginFallido_deberiaRetornarError(username: String, contrasenia: String, expectedStatus: Int) {

        val loginRequest = UsuarioLoginDTO(username, contrasenia)
        // Arrange
        val url = "/api/auth/login"

        // Act
        val result = mockMvc.perform(
            post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            //.andExpect(status().isOk)
            .andExpect(status().`is`(expectedStatus))
    }


}
package ar.edu.unsam.phm
import ar.edu.unsam.phm.configurationSecurity.PropiedadesJwt
import org.springframework.boot.runApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories

@SpringBootApplication
@Configuration
@EnableConfigurationProperties(PropiedadesJwt::class)
@EnableJpaRepositories(
    basePackages = ["ar.edu.unsam.phm.repository"],
    transactionManagerRef = "transactionManager" // Usa el bean por defecto para JPA
)
@EnableNeo4jRepositories(
    basePackages = ["ar.edu.unsam.phm.neo4jRepository"],
    transactionManagerRef = "neo4jTransactionManager"
)
class ProyectoApplication{}

fun main(args: Array<String>) {
    runApplication<ProyectoApplication>(*args)
}
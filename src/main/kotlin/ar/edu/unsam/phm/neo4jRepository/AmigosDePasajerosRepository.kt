package ar.edu.unsam.phm.neo4jRepository

import ar.edu.unsam.phm.domain.PasajeroAmigo
import ar.edu.unsam.phm.dto.AmigoDelAmigoDTO
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Repository
@Transactional("neo4jTransactionManager")
interface AmigosDePasajerosRepository: Neo4jRepository<PasajeroAmigo, String> {

    fun findByIdPasajero(idPasajero: Long): Optional<PasajeroAmigo>

    @Query("""
        MATCH (yo:PasajeroAmigo {idPasajero: ${'$'}idUsuario})-[:AMIGO_DE]->(miAmigo:PasajeroAmigo)-[:AMIGO_DE]->(amigoDelAmigo:PasajeroAmigo)
        MATCH (yo)-[viajeYo:VIAJO_CON]->(choferEnComun:ChoferDeRelacionDeViaje)
        WHERE datetime(viajeYo.fechaFinalizacion).year = datetime().year
        MATCH (amigoDelAmigo:PasajeroAmigo)-[viajeAmigoDelAmigo:VIAJO_CON]->(choferEnComun)
        WHERE datetime(viajeAmigoDelAmigo.fechaFinalizacion).year = datetime().year
        AND amigoDelAmigo <> yo
        AND NOT (yo)-[:AMIGO_DE]->(amigoDelAmigo)  
        RETURN DISTINCT amigoDelAmigo
        ORDER BY amigoDelAmigo.nombre, amigoDelAmigo.apellido
        LIMIT 10
    """)
    fun encontrarAmigosDeAmigosConViajeMismoAnioYChofer(@Param("idUsuario")idUsuario: Long): List<PasajeroAmigo>

    @Query("MATCH (p:PasajeroAmigo {idPasajero: \$idPasajero})-[r:AMIGO_DE]->(a:PasajeroAmigo{idPasajero:\$idAmigo}) DELETE r")
    fun eliminarRelacionAmigo(@Param("idPasajero") idPasajero:Long, @Param("idAmigo") idAmigo: Long)


    @Query("MATCH (p:PasajeroAmigo {idPasajero: \$idPasajero})-[r:AMIGO_DE]->(a:PasajeroAmigo{idPasajero:\$idAmigo}) RETURN COUNT(r) > 0")
    fun existeRelacionAmigo(@Param("idPasajero") idPasajero: Long, @Param("idAmigo") idAmigo: Long): Boolean
}
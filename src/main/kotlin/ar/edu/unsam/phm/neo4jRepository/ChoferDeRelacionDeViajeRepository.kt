package ar.edu.unsam.phm.neo4jRepository

import ar.edu.unsam.phm.domain.ChoferDeRelacionDeViaje
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Repository
@Transactional("neo4jTransactionManager")

interface ChoferDeRelacionDeViajeRepository: Neo4jRepository<ChoferDeRelacionDeViaje, String> {
    fun findByIdChofer(idChofer:String): Optional<ChoferDeRelacionDeViaje>
}
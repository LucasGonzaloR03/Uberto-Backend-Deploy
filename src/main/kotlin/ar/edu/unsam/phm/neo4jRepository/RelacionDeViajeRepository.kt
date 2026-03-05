package ar.edu.unsam.phm.neo4jRepository

import ar.edu.unsam.phm.domain.RelacionDeViaje
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional("neo4jTransactionManager")

interface RelacionDeViajeRepository: Neo4jRepository<RelacionDeViaje, String> {
}
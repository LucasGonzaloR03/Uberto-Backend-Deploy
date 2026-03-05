package ar.edu.unsam.phm.repository
import ar.edu.unsam.phm.domain.*
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.elemMatch
import com.mongodb.client.model.Filters.gte
import com.mongodb.client.model.Filters.lte
import com.mongodb.client.model.Filters.or
import org.springframework.data.mongodb.core.aggregation.BooleanOperators.not
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface ChoferRepository: MongoRepository<Chofer, String> {

    fun findByUserDataId(login:Long) : Optional<Chofer>

    @Query("{ \$and: [ { \"viajesDelChofer\": { \$not: { \$elemMatch: { \$or: [ " +
    "{ \"fechaInicio\": { \$lte: ?1 }, \"fechaFinalizacion\": { \$gte: ?0 } }, " +
    "{ \"fechaInicio\": { \$gte: ?0, \$lte: ?1 } }, " +
    "{ \"fechaFinalizacion\": { \$gte: ?0, \$lte: ?1 } } " +
    "] } } } } ] }")
    fun findChoferesDisponibles(fechaInicio: LocalDateTime, fechaFinalizacion: LocalDateTime): List<Chofer>
}

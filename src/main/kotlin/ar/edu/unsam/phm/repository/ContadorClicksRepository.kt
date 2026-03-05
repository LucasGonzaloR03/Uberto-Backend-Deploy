package ar.edu.unsam.phm.repository
import ar.edu.unsam.phm.domain.ContadorClick
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ContadorClicksRepository : MongoRepository<ContadorClick, String>{
    fun findByChoferid(idChofer: String): List<ContadorClick>
}
package ar.edu.unsam.phm.repository
import ar.edu.unsam.phm.domain.*
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@Transactional
interface PasajeroRepository: JpaRepository<Pasajero, Long> {
    fun findByUserDataId(login:Long) : Optional<Pasajero>
    override fun findById(id:Long) : Optional<Pasajero>
    @Modifying
    @Transactional
    @Query("UPDATE Pasajero p SET p.saldo = p.saldo + :monto WHERE p.id = :pasajeroId")
    fun incrementarSaldo(pasajeroId: Long, monto: Double): Int
    @Modifying
    @Transactional
    @Query("UPDATE Pasajero p SET p.saldo = p.saldo - :monto WHERE p.id = :pasajeroId AND p.saldo >= :monto")
    fun decrementarSaldo(pasajeroId: Long, monto: Double): Int
}
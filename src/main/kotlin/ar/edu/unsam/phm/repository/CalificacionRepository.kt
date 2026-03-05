package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Calificacion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CalificacionRepository: JpaRepository<Calificacion, Long> {

    fun findByViajeChoferDeViajeId(idChofer: String): List<Calificacion>

    fun findByViajePasajeroId(idPasajero: Long): List<Calificacion>

    @Query(" SELECT AVG(c.puntaje)  FROM Calificacion c WHERE c.viaje.choferDeViaje.id = :idChofer ")
    fun obtenerPromedioPuntajeChofer(
        @Param("idChofer") idChofer: String,
    ): Double?

}
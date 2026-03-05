package ar.edu.unsam.phm.repository
import ar.edu.unsam.phm.domain.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ViajeRepository: JpaRepository<Viaje,Long>{

    fun existsByChoferDeViajeId(idChofer: String): Boolean

    fun existsByPasajeroId(idPasajero: Long): Boolean

    // esta crude remplaza a filtro viajes pasajero pendientes
    fun findByPasajeroIdAndFechaFinalizacionBefore(idPasajero:Long,fecha: LocalDateTime) : List<Viaje>

    // esta crude remplaza a filtro viajes pasajero realizados
    fun findByPasajeroIdAndFechaFinalizacionAfter(idPasajero:Long,fecha: LocalDateTime) : List<Viaje>
    @Query(
        """
    SELECT v
    FROM Viaje v
    WHERE  ( v.choferDeViaje.id = :idChofer)
      AND (:usuario IS NULL OR LOWER(v.pasajero.nombre) LIKE LOWER(CONCAT('%', :usuario, '%')))
      AND (:origen IS NULL OR LOWER(v.origen) LIKE LOWER(CONCAT('%', :origen, '%')))
      AND (:destino IS NULL OR LOWER(v.destino) LIKE LOWER(CONCAT('%', :destino, '%')))
      AND (:cantidadPasajeros IS NULL OR v.cantidadPasajeros = :cantidadPasajeros)
      AND (v.fechaFinalizacion > CURRENT_TIMESTAMP)
    """
    )
    fun filtrarViajes(
        @Param("idChofer") idChofer: String,
        @Param("usuario") usuario: String?,
        @Param("origen") origen: String?,
        @Param("destino") destino: String?,
        @Param("cantidadPasajeros") cantidadPasajeros: Int?
    ): List<Viaje>

    // este trae los viajes de chofer que ya fueron realizados
    fun findByChoferDeViajeIdAndFechaFinalizacionBefore(idChofer:String, fecha: LocalDateTime) : List<Viaje>

    @Query("" +
            "select sum(v.precioChofer) " +
            "from Viaje v " +
            "where v.choferDeViaje.id = :idChofer " +
            "and v.fechaFinalizacion < :fechaActual " )
    fun getImporteTotalChofer(
        @Param ("idChofer")idChofer:String,
        @Param("fechaActual") fechaActual: LocalDateTime
    ): Double?

    @Query(
        """ 
    SELECT NOT EXISTS (
            SELECT 1 FROM Viaje v  
            WHERE v.choferDeViaje.id = :idChofer
              AND v.fechaInicio < :fechaFinalizacion
              AND v.fechaFinalizacion > :fechaInicio
        )
    """
    )
    fun verificarDisponibilidadChofer(
        @Param("idChofer")idChofer: String?,
        @Param("fechaInicio")fechaInicio: LocalDateTime,
        @Param ("fechaFinalizacion")fechaFinalizacion: LocalDateTime
    ): Boolean


}
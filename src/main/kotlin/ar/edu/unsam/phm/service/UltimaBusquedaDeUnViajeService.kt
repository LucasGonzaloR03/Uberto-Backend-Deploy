package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.UltimaBusquedaDeUnViaje
import ar.edu.unsam.phm.dto.DetalleViajeDTO
import ar.edu.unsam.phm.dto.UltimaBusquedaDeUnViajeDTO
import ar.edu.unsam.phm.dto.toUltimaBusquedaDeUnViaje
import ar.edu.unsam.phm.errorHandling.NotFoundException
import ar.edu.unsam.phm.extras.Formateador
import ar.edu.unsam.phm.repository.UltimaBusquedaDeUnViajeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UltimaBusquedaDeUnViajeService {
    @Autowired
    lateinit var ultimaBusquedaDeUnViajeRepository: UltimaBusquedaDeUnViajeRepository

    fun crearUltimaBusquedaDeUnViaje(idPasajero:Long, consultaDeViaje: DetalleViajeDTO){
        val ultimaBusqueda = UltimaBusquedaDeUnViaje(idPasajero, consultaDeViaje.origen,consultaDeViaje.destino,
            LocalDateTime.parse(consultaDeViaje.fechaInicio),consultaDeViaje.cantidadDePasajeros).apply {
          this.asignarId()
        }
        ultimaBusquedaDeUnViajeRepository.save(ultimaBusqueda)
    }

    fun buscarUltimaBusquedaDeUnViajePorId(id: String): UltimaBusquedaDeUnViajeDTO {
        val ultimaBusquedaRealizada = ultimaBusquedaDeUnViajeRepository.findById(id).orElseThrow { throw NotFoundException("No se encontro un viaje con un id") }
        return ultimaBusquedaRealizada.toUltimaBusquedaDeUnViaje()
    }

    fun comprobarUltimaBusquedaDeUnViaje(idPasajero: Long):UltimaBusquedaDeUnViajeDTO{
        val id = idPasajero.toString()
        return if (ultimaBusquedaDeUnViajeRepository.existsById(id)){
            buscarUltimaBusquedaDeUnViajePorId(id)
        }else{
            return UltimaBusquedaDeUnViajeDTO("","","",0)
        }
    }
}

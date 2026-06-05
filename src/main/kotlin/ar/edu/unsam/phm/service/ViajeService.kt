package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.dto.*
import ar.edu.unsam.phm.repository.ViajeRepository
import java.time.LocalDateTime
import ar.edu.unsam.phm.errorHandling.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class ViajeService {


    @Autowired
    lateinit var viajeRepository: ViajeRepository

    fun obtenerViajesPendientesPasajero(idPasajero: Long): List<TarjetaViajeDTO> {
        val existe = viajeRepository.existsByPasajeroId(idPasajero)
        if (!existe) throw NotFoundException("El pasajero que realizo la busqueda todavia no tiene viajes.\n Por favor dirigase a la pagina de inicio y realice uno.")
        val viajesPendientes = viajeRepository.findByPasajeroIdAndFechaFinalizacionAfter(idPasajero, LocalDateTime.now())
        return viajesPendientes.map{ it.toTarjetaViajeDTO() }
    }

    fun obtenerViajesRealizadosPasajero(idPasajero: Long): List<TarjetaViajeDTO> {
        val existe = viajeRepository.existsByPasajeroId(idPasajero)
        if (!existe) throw NotFoundException("El pasajero que realizo la busqueda todavia no termino ningun viaje.\n Por favor espere a que termine el viaje y vuelva a fijarse.")
        val viajesRealizados = viajeRepository.findByPasajeroIdAndFechaFinalizacionBefore(idPasajero, LocalDateTime.now())
        return viajesRealizados.map{ it.toTarjetaViajeDTO() }
    }

    fun obtenerViajesFiltroChofer(filtro: FiltroViajeDTO, chofer: Chofer ): List<TarjetaViajeDTO> {
        val existe = viajeRepository.existsByChoferDeViajeId(chofer.id)
        if (!existe) throw NotFoundException("El chofer que realizo la busqueda todavia no tiene viajes. \n Por favor, espere a que un asociado a la aplicacion realice un viaje con usted.")
        val viajesFiltrados: List<Viaje> = viajeRepository.filtrarViajes( chofer.id,filtro.usuario, filtro.origen,filtro.destino,filtro.cantidadDePasajeros)
        return viajesFiltrados.map{ it.toTarjetaViajeDTO()}
    }

    fun obtenerViajesRealizadosChofer(chofer: Chofer): List<TarjetaViajeDTO> {
        val existe = viajeRepository.existsByChoferDeViajeId(chofer.id)
        if (!existe) throw NotFoundException("El chofer que realizo la busqueda todavia no tiene viajes. \n Por favor, espere a que se realice un viaje completo.")
        val viajesRealizados: List<Viaje> = viajeRepository.findByChoferDeViajeIdAndFechaFinalizacionBefore(chofer.id, LocalDateTime.now())

        return viajesRealizados.map{ it.toTarjetaViajeDTO() }
    }

    fun obtenerImporteTotal(idChofer:String): Double? {
        return viajeRepository.getImporteTotalChofer(idChofer, LocalDateTime.now())
    }

    fun viajeSave(viaje: Viaje): Viaje {
        return viajeRepository.save(viaje)
    }


    fun findById(idParam: Long): Viaje {
        return viajeRepository.findById(idParam ).orElseThrow {
            throw NotFoundException("No se encontró el viaje indicado: $idParam")
        }
    }

    fun verificarDisponibilidadChofer(nuevoViaje: Viaje) {
        if( !viajeRepository.verificarDisponibilidadChofer(
                nuevoViaje.choferMongo.id,
                nuevoViaje.fechaInicio,
                nuevoViaje.fechaFinalizacion)
        ){
            throw BusinessException("el Chofer con id ${nuevoViaje.choferMongo.id} no esta disponible.")
        }
    }

}
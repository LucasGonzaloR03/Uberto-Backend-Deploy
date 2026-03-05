package ar.edu.unsam.phm.service
import org.springframework.stereotype.Service
import ar.edu.unsam.phm.repository.*
import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.extras.*
import ar.edu.unsam.phm.dto.*
import ar.edu.unsam.phm.errorHandling.NotFoundException
import ar.edu.unsam.phm.neo4jRepository.ChoferDeRelacionDeViajeRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@Service
class ChoferService()  {
    @Autowired
    lateinit var choferRepository: ChoferRepository
    @Autowired
    lateinit var choferRelacionDeViajeRepository: ChoferDeRelacionDeViajeRepository
    @Autowired
    lateinit var calificacionService: CalificacionService
    @Autowired
    lateinit var viajeService: ViajeService
    @Autowired
    lateinit var ultimaBusquedaDeUnViajeService: UltimaBusquedaDeUnViajeService

    @Autowired
    lateinit var registroClickRepository: ContadorClicksRepository

    fun choferesDisponibles(viaje: DetalleViajeDTO, idPasajero: Long): List<TarjetaChoferDTO> {
        val pseudoViaje: Viaje = Viaje().apply{
            duracion = viaje.duracion
            cantidadPasajeros = viaje.cantidadDePasajeros
            fechaInicio = Formateador().formatoLocalDateTime(viaje.fechaInicio)
            this.asignarFechaFinalizacion()
        }
        this.ultimaBusquedaDeUnViajeService.crearUltimaBusquedaDeUnViaje(idPasajero,viaje)
        val choferesDisponibles: List<Chofer> = buscarChoferesDiponibles(pseudoViaje.fechaInicio,pseudoViaje.fechaFinalizacion)
        return choferesDisponibles.map { it.toTarjetaChoferDTO(pseudoViaje)}
    }

    fun infoChofer(idChofer: Long): InformacionChoferDTO {
        val chofer: Chofer = this.findByUserDataId(idChofer)
        return chofer.toInformacionChoferDTO()
    }

    fun actualizarPromedioPuntajeChofer(idChofer: String, nuevoPromedioPuntajeChofer:Double) {
        val chofer = findbyChoferId(idChofer)
        chofer.actualizarPromedioPuntaje(nuevoPromedioPuntajeChofer)
        choferRepository.save(chofer)
    }

    fun obtenerDetalleViajeChofer(idChofer: String): DetalleChoferParaViajeDTO{
        val chofer = findbyChoferId(idChofer)
        val calificaciones: List<Calificacion> = calificacionService.findByViajeChoferId(idChofer)
        return chofer.toDetalleChoferParaViajeDTO(calificaciones)
    }

    fun obtenerCalificacionesChofer(idChofer: Long): List<TarjetaCalificacionDTO> {
        val chofer: Chofer = this.findByUserDataId(idChofer)
        val calificaciones: List<Calificacion> = calificacionService.findByViajeChoferId(chofer.id)
        return calificaciones.map{ it.toTarjetaCalificacionDTO() }
    }

    fun obtenerImporteTotalViajes(idChofer: Long): Double {
        val chofer: Chofer = this.findByUserDataId(idChofer)
        return viajeService.obtenerImporteTotal(chofer.id) ?: 0.0
    }

    @Transactional
    fun actualizarInfoChofer(datosActualizados: InformacionChoferDTO) {
       val chofer: Chofer = this.findbyChoferId(datosActualizados.id)
        chofer.apply {
            precioBase = datosActualizados.precioBase
            patenteVehiculo = datosActualizados.patenteVehiculo
            marcaVehiculo = datosActualizados.marcaVehiculo
            modeloVehiculo = datosActualizados.modeloVehiculo
        }
        chofer.validadEntidad()
        choferRepository.save(chofer)
    }

    fun findByUserDataId(userDataID: Long): Chofer{
       return choferRepository.findByUserDataId(userDataID).orElseThrow {
           throw NotFoundException("No se encontró el chofer indicado: $userDataID")
         }
    }

    fun findbyChoferId(choferId: String): Chofer{
        return  choferRepository.findById(choferId).orElseThrow {
            throw NotFoundException("No se encontró el chofer indicado: $choferId")
        }
    }

    fun obtenerViajesFiltroChofer(filtro: FiltroViajeDTO, idChofer:Long ): List<TarjetaViajeDTO>{
        val chofer: Chofer = this.findByUserDataId(idChofer)
        return viajeService.obtenerViajesFiltroChofer(filtro, chofer)
    }

    fun obtenerViajesRealizadosChofer(idChofer: Long): List<TarjetaViajeDTO> {
        val chofer: Chofer = this.findByUserDataId(idChofer)
        return  viajeService.obtenerViajesRealizadosChofer(chofer)
    }

    fun buscarChoferesDiponibles( fechaInicio: LocalDateTime, fechaFinalizacion: LocalDateTime): List<Chofer>{
        val listaChoferesDesocupados = choferRepository.findChoferesDisponibles(fechaInicio,fechaFinalizacion)
       return  listaChoferesDesocupados
    }

    fun obtenerRegistroClicks(idChofer: Long): List<RegistroClickDTO> {
        val chofer: Chofer = this.findByUserDataId(idChofer)
        return registroClickRepository.findByChoferid(chofer.id).map{it.toRegistroClickDTO()}
    }

    fun agregarViajeAUnChofer(idChofer:String,nuevoViajeParaChofer: ViajeParaChofer){
        val chofer: Chofer = this.findbyChoferId(idChofer)
        chofer.agregarNuevoViajeParaChofer(nuevoViajeParaChofer)
        choferRepository.save(chofer)
    }

    fun findbyChoferRelacionId(idChofer: String):ChoferDeRelacionDeViaje{
        val choferEncontrado: ChoferDeRelacionDeViaje = this.choferRelacionDeViajeRepository.findByIdChofer(idChofer).orElseThrow{throw NotFoundException("No se encontro chofer en neo4j")}
        return choferEncontrado
    }

}
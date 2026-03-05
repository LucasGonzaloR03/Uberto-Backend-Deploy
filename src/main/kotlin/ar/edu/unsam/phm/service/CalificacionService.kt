package ar.edu.unsam.phm.service
import java.time.LocalDateTime
import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.dto.NuevaCalificacionDTO
import ar.edu.unsam.phm.dto.TarjetaCalificacionDTO
import ar.edu.unsam.phm.dto.toTarjetaCalificacionDTO
import ar.edu.unsam.phm.repository.*
import ar.edu.unsam.phm.errorHandling.*
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class CalificacionService

{
    @Autowired
    lateinit var calificacionRepository: CalificacionRepository
    @Autowired
    lateinit var viajeService: ViajeService
    @Autowired
    @Lazy
    lateinit var choferService: ChoferService


    @Transactional
    fun calificarViajeRealizado(nuevaCalificacionParams: NuevaCalificacionDTO) {
        val viajeExistente: Viaje = viajeService.findById(nuevaCalificacionParams.idViaje)
        this.verficarPuedeClasificar(viajeExistente)
        this.verificarSiEstaCalificado(viajeExistente)
        this.verificarPuntajeComentario(nuevaCalificacionParams.puntaje,nuevaCalificacionParams.comentario)
        val nuevaCalificacion = Calificacion().apply {
            viaje= viajeExistente
            puntaje = nuevaCalificacionParams.puntaje
            comentario = nuevaCalificacionParams.comentario
            viaje!!.seCalifica()
        }
        calificacionRepository.save(nuevaCalificacion)
        this.updatePromedioPuntajeChofer(viajeExistente.choferDeViaje.id)
    }

    fun updatePromedioPuntajeChofer(idChofer: String) {
        val nuevoPromedioPuntajeChofer=calificacionRepository.obtenerPromedioPuntajeChofer(idChofer)
        choferService.actualizarPromedioPuntajeChofer(idChofer,nuevoPromedioPuntajeChofer!!)
    }

    @Transactional
    fun deleteCalificacion(idCalificacion: Long) {
        val calificacionParaEliminar: Calificacion = this.calificacionById(idCalificacion)
        calificacionParaEliminar.viaje!!.seEliminaCalificacion()
        calificacionRepository.delete(calificacionParaEliminar)
    }

    fun verficarPuedeClasificar(viaje:Viaje) {
        if (viaje.fechaFinalizacion > LocalDateTime.now()){
            throw BusinessException("No se puede calificar este viaje. No se encuentra realizado.")
        }
    }

    fun  verificarSiEstaCalificado(viaje:Viaje){
        if(viaje.estaCalificado){
            throw BusinessException("No se puede calificar un viaje que ya se esta calificado.")
        }
    }

    fun verificarPuntajeComentario(puntaje: Int,comentario: String){
        val validarPuntaje:Boolean = (puntaje >= 0) && (puntaje <= 5)
        if(validarPuntaje && comentario.isBlank()){
            throw BusinessException("Por favor complete todos los campos.")
        }
    }


    fun findByViajeChoferId(idChofer: String): List<Calificacion> {
        return calificacionRepository.findByViajeChoferDeViajeId(idChofer)
    }

    fun findByViajePasajeroId(idPasajero: Long):List<TarjetaCalificacionDTO>{
        return calificacionRepository.findByViajePasajeroId(idPasajero).map{ it.toTarjetaCalificacionDTO() }

    }

    private fun calificacionById(idCalificacion: Long): Calificacion {
        return calificacionRepository.findById(idCalificacion).orElseThrow {
            throw NotFoundException("No se encontró la calificacion indicada: $idCalificacion")
        }
    }

}
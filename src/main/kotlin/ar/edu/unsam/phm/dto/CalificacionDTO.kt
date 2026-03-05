package ar.edu.unsam.phm.dto

import java.time.LocalDate
import ar.edu.unsam.phm.domain.Calificacion

class TarjetaCalificacionDTO(
    val id:Long?,
    val pasajero:PasajeroParaTarjetaDTO,
    val chofer:ChoferParaTarjetaDTO,
    val puntaje:Int,
    val comentario:String,
    val fechaRealizado:LocalDate
)fun Calificacion.toTarjetaCalificacionDTO( )= TarjetaCalificacionDTO(
    id = this.id,
    pasajero = this.viaje!!.pasajero.toPasajeroParaTarjetaDTO(),
    chofer = this.viaje!!.choferDeViaje.toChoferParaTarjetaDTO(),
    puntaje = this.puntaje,
    comentario = this.comentario,
    fechaRealizado=this.viaje!!.fechaFinalizacion.toLocalDate()
)


class NuevaCalificacionDTO(
    val idViaje:Long,
    val comentario:String,
    val puntaje:Int,

)
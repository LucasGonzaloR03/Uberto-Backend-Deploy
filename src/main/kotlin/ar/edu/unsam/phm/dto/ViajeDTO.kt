package ar.edu.unsam.phm.dto

import java.time.LocalDateTime
import ar.edu.unsam.phm.domain.Viaje

class ConsultaDeViajeDTO(
    val fechaInicio:String,
    val duracion:Int,
    val cantidadDePasajeros:Int
)

class DetalleViajeDTO(
    val origen:String,
    val destino:String,
    val fechaInicio: String,
    val duracion: Int,
    val cantidadDePasajeros: Int
)

class TarjetaViajeDTO(
    val id:Long?,
    val pasajero:PasajeroParaTarjetaDTO,
    val chofer:ChoferParaTarjetaDTO,
    val cantidadDePasajeros: Int,
    val origen: String,
    val destino: String,
    val fechaInicio: LocalDateTime,
    val fechaFin: LocalDateTime,
    val importeComision:Double,
    val importeNormal:Double,
    val fueCalificado:Boolean
)

fun Viaje.toTarjetaViajeDTO() = TarjetaViajeDTO(
    id = this.id,
    pasajero = this.pasajero.toPasajeroParaTarjetaDTO(),
    chofer = this.choferDeViaje.toChoferParaTarjetaDTO(),
    cantidadDePasajeros = this.cantidadPasajeros,
    origen = this.origen,
    destino= this.destino,
    fechaInicio = this.fechaInicio,
    fechaFin = this.fechaFinalizacion,
    importeComision = this.precioPasajero,
    importeNormal = this.precioChofer,
    fueCalificado = this.estaCalificado
)

data class FiltroViajeDTO(
    val usuario: String? = null,
    val origen: String? = null,
    val destino: String? = null,
    val cantidadDePasajeros: Int? = null
)

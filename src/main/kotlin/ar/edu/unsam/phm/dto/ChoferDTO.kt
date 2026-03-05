package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.Chofer
import ar.edu.unsam.phm.domain.ChoferDeViaje
import ar.edu.unsam.phm.domain.Viaje

class TarjetaChoferDTO(
    val id:String?,
    val patenteVehiculo:String,
    val nombreCompleto:String,
    val marcaVehiculo:String,
    val modeloVehiculo:Int,
    val costoComision:Double,
    val puntajeChofer:Double
)

fun Chofer.toTarjetaChoferDTO(viaje: Viaje) = TarjetaChoferDTO(
    id= this.id,
    patenteVehiculo = this.patenteVehiculo,
    nombreCompleto = "${this.nombre} ${this.apellido}",
    marcaVehiculo = this.marcaVehiculo,
    modeloVehiculo = this.modeloVehiculo,
    costoComision = this.costoComision(viaje),
    puntajeChofer = this.promedioDePuntaje
)

class DetalleChoferParaViajeDTO(
    val id: String?,
    val nombreCompleto: String,
    val tipoChofer:String,
    val marcaVehiculo:String,
    val modeloVehiculo:Int,
    val patenteVehiculo:String,
    val puntaje: Double,
    val listaCalificacion:List<TarjetaCalificacionDTO>
)

fun Chofer.toDetalleChoferParaViajeDTO( listaCalificacion: List<Calificacion>) = DetalleChoferParaViajeDTO(
    id = this.id,
    nombreCompleto= "${this.nombre} ${this.apellido}",
    tipoChofer = this.toNombreClaseDTO().tipoChoferStr,
    marcaVehiculo = this.marcaVehiculo,
    modeloVehiculo = this.modeloVehiculo,
    patenteVehiculo = this.patenteVehiculo,
    puntaje = this.promedioDePuntaje,
    listaCalificacion = listaCalificacion.map{it.toTarjetaCalificacionDTO()}
)

class ChoferParaTarjetaDTO(
    val fotoPerfil: String,
    val nombreCompleto: String
)
fun ChoferDeViaje.toChoferParaTarjetaDTO() = ChoferParaTarjetaDTO(
    fotoPerfil = this.fotoPerfil,
    nombreCompleto = "${this.nombreChofer} ${this.apellidoChofer}"
)

class InformacionChoferDTO(
    val id:String,
    val tipoChofer:String,
    val fotoPerfil: String,
    val nombre:String,
    val apellido:String,
    val precioBase:Double,
    val patenteVehiculo: String,
    val marcaVehiculo: String,
    val modeloVehiculo: Int
)

fun Chofer.toInformacionChoferDTO() = InformacionChoferDTO(
    id = this.id,
    tipoChofer = this.toNombreClaseDTO().tipoChoferStr ,
    fotoPerfil = this.fotoPerfil,
    nombre = this.nombre,
    apellido = this.apellido,
    precioBase = this.precioBase,
    patenteVehiculo = this.patenteVehiculo,
    marcaVehiculo = this.marcaVehiculo,
    modeloVehiculo = this.modeloVehiculo
)

data class NombreClaseDTO(val tipoChoferStr :String)

fun Chofer.toNombreClaseDTO() = NombreClaseDTO(this::class.simpleName?: "desconocido")




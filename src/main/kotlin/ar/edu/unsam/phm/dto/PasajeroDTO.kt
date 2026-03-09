package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Pasajero
import ar.edu.unsam.phm.domain.PasajeroAmigo

class TarjetaAmigoDTO(
    val id:Long?,
    val fotoPerfil:String,
    val nombreCompleto:String
)

fun PasajeroAmigo.toTarjetaAmigoDTO() = TarjetaAmigoDTO(
    id = this.idPasajero,
    fotoPerfil = this.fotoPerfil,
    nombreCompleto = "${this.nombre} ${this.apellido}"
)

class InformacionPasajeroDTO(
    val id: Long?,
    val idUserData:Long?,
    val fotoPerfil:String,
    val nombre:String,
    val apellido:String,
    val telefono:String,
    val saldo:Long,
    val listaAmigos:List<TarjetaAmigoDTO>
)

fun Pasajero.toInformacionPasajero(listaPasajeroAmigo: List<PasajeroAmigo>) = InformacionPasajeroDTO(
    id = this.id,
    idUserData = this.userData!!.id,
    fotoPerfil = this.userData!!.fotoPerfil,
    nombre = this.nombre,
    apellido = this.apellido,
    telefono = this.telefono,
    saldo = this.consultarSaldo(),
    listaAmigos = listaPasajeroAmigo.map{it.toTarjetaAmigoDTO()}
)

class PasajeroParaTarjetaDTO(
    val fotoPerfil: String,
    val nombreCompleto: String
)

fun Pasajero.toPasajeroParaTarjetaDTO() = PasajeroParaTarjetaDTO(
    fotoPerfil = this.userData!!.fotoPerfil,
    nombreCompleto = "${this.nombre} ${this.apellido}"
)

data class AmigoDelAmigoDTO(
    val id:Long,
    val nombre: String,
    val apellido: String
)

fun PasajeroAmigo.toAmigoDelAmigoDTO() = AmigoDelAmigoDTO(
    id = this.idPasajero,
    nombre = this.nombre,
    apellido = this.apellido
)


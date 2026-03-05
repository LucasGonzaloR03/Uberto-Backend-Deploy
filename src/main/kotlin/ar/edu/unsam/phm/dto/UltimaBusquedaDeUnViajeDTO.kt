package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.UltimaBusquedaDeUnViaje

data class UltimaBusquedaDeUnViajeDTO(
    val origen: String,
    val destino: String,
    val fechaInicio: String,
    val cantidadPasajeros: Int,
)

fun UltimaBusquedaDeUnViaje.toUltimaBusquedaDeUnViaje() = UltimaBusquedaDeUnViajeDTO(
    origen = this.origen,
    destino = this.destino,
    fechaInicio = this.fechaInicio.toString(),
    cantidadPasajeros = this.cantidadPasajeros
)
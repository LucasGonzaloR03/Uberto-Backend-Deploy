package ar.edu.unsam.phm.domain

import java.time.LocalDateTime

data class ViajeParaChofer(
    var fechaInicio: LocalDateTime,
    var fechaFinalizacion: LocalDateTime,
)

fun Viaje.toViajeParaChofer() = ViajeParaChofer(this.fechaInicio, this.fechaFinalizacion)
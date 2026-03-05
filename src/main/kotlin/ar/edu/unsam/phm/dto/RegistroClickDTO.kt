package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.ContadorClick
import java.time.LocalDateTime


class RegistroClickDTO(
    val id:String,
    val nombrePasajero: String,
    val fechaHoraClick: LocalDateTime,

    )


fun ContadorClick.toRegistroClickDTO() = RegistroClickDTO(
    id = this.id,
    nombrePasajero = this.nombrePasajero,
    fechaHoraClick = this.fechaHoraClick
)



package ar.edu.unsam.phm.domain

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import java.time.LocalDateTime


@Document (collection = "ContadorClick")
@TypeAlias("ContadorClick")
data class ContadorClick(
    var choferid: String,
    var pasajeroid: Long,
    var nombreChofer: String,
    var nombrePasajero: String,
    var fechaHoraClick: LocalDateTime
){
    @Id
    lateinit var id: String
}

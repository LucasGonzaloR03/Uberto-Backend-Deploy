package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Chofer
import ar.edu.unsam.phm.domain.ContadorClick
import ar.edu.unsam.phm.domain.Pasajero
import ar.edu.unsam.phm.extras.Formateador
import ar.edu.unsam.phm.repository.ContadorClicksRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Service
class ContadorClicksService (){
    @Autowired
    lateinit var contadorClicksRepository: ContadorClicksRepository

    fun registrarClick(chofer : Chofer, pasajero: Pasajero) {

        val nombreChofer = "${chofer.nombre} ${chofer.apellido}".trim()

        val nombrePasajero = "${pasajero.nombre} ${pasajero.apellido}".trim()
        val log = ContadorClick(
            choferid = chofer.id,
            pasajeroid = pasajero.id!!,
            nombreChofer = nombreChofer,
            nombrePasajero = nombrePasajero,
            fechaHoraClick = Formateador().formatoLocalDateTime(LocalDateTime.now().toString())
        )
        contadorClicksRepository.save(log)
    }
}

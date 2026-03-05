package ar.edu.unsam.phm.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.time.LocalDateTime

@RedisHash("UltimaBusquedaDeUnViaje", timeToLive = 18000)
data class UltimaBusquedaDeUnViaje(
     val idPasajero: Long,
     var origen: String = "",
     var destino: String = "",
     var fechaInicio: LocalDateTime,
     var cantidadPasajeros: Int = 0
) {
     @Id
     lateinit var id: String

     fun asignarId(){
          this.id = idPasajero.toString()
     }
}
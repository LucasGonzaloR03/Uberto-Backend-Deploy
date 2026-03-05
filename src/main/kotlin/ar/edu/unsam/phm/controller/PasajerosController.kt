package ar.edu.unsam.phm.controller
import ar.edu.unsam.phm.domain.UltimaBusquedaDeUnViaje
import ar.edu.unsam.phm.service.*
import ar.edu.unsam.phm.dto.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/pasajero")
class PasajerosController{
    @Autowired lateinit var  pasajerosService: PasajerosService

    @GetMapping("/home/detalleViaje")
    fun getDetalleViaje(@RequestParam idChofer: String,@RequestParam idPasajero: Long): DetalleChoferParaViajeDTO{
        return pasajerosService.obtenerDetalleChoferViaje(idChofer,idPasajero)
    }

    @PostMapping("/home/choferesdisponibles")
    fun getChoferesDisponibles(@RequestBody viaje: DetalleViajeDTO, @RequestParam idPasajero:Long): List<TarjetaChoferDTO> {
        return pasajerosService.obtenerChoferesDisponibles(viaje, idPasajero).toList()
    }

    @PostMapping("/home/confirmarviaje")
    fun postConfirmarViaje(@RequestBody viaje: DetalleViajeDTO, @RequestParam idChofer:String, @RequestParam idPasajero:Long) {
        pasajerosService.confirmarViaje(viaje, idChofer, idPasajero)
    }

    @GetMapping("/perfil/informacion")
    fun getDatosPasajero(@RequestParam idPasajero: Long):InformacionPasajeroDTO{
        return pasajerosService.obtenerInfoPasajero(idPasajero)
    }

    @PutMapping("/perfil/actualizardatos")
    fun putDatosPasajero(@RequestBody datosActualizados:InformacionPasajeroDTO){
        pasajerosService.actualizarInformacionPasajero(datosActualizados)
    }

    @PutMapping("/perfil/actualizarsaldo")
    fun putActualizarSaldo(@RequestParam idPasajero: Long, @RequestParam saldo: Double) {
        pasajerosService.actualizarSaldoPasajero(idPasajero, saldo)
    }

    @GetMapping("/perfil/viajesrealizados")
    fun getViajesRealizados(@RequestParam idUsuario: Long): List<TarjetaViajeDTO> {
        return pasajerosService.buscarViajesRealizados(idUsuario)
    }

    @GetMapping("/perfil/viajespendientes")
    fun getViajesPendientes(@RequestParam idUsuario: Long): List<TarjetaViajeDTO> {
        return pasajerosService.buscarViajesPendientesPasajero(idUsuario)
    }

    @GetMapping("/perfil/calificaciones")
    fun getCalificaciones(@RequestParam idUsuario:Long):List<TarjetaCalificacionDTO>{
        return pasajerosService.buscarCalificacionesPasajero(idUsuario)
    }

    @PostMapping("/perfil/calificar")
    fun postCalificarViaje(@RequestBody nuevaCalificacion:NuevaCalificacionDTO) {
       return pasajerosService.calificarViajeRealizado(nuevaCalificacion)
    }

    @DeleteMapping("/perfil/eliminarCalificacion")
    fun deleteCalificacion(@RequestParam idCalificacion: Long) {
       return pasajerosService.eliminarCalificacion(idCalificacion)
    }

    @GetMapping("/perfil/mostraramigos")
    fun getPasajerosApp(@RequestParam idPasajero: Long): List<AmigoDelAmigoDTO> {
        return pasajerosService.obtenerPasajeros(idPasajero)
    }

    @PostMapping("/perfil/agregaramigo")
    fun postAgregarAmigo(@RequestParam idPasajero: Long, @RequestParam idAmigo:Long) {
        pasajerosService.agregarAmigoPasajero(idPasajero,idAmigo)
    }

    @DeleteMapping("/perfil/eliminaramigo")
    fun deleteAmigo(@RequestParam idPasajero: Long, @RequestParam idAmigo: Long) {
        pasajerosService.eliminarAmigoPasajero(idPasajero,idAmigo)
    }

    @GetMapping("/home/formulario")
    fun getUltimaBusquedaDeUnViaje(@RequestParam idPasajero: Long): UltimaBusquedaDeUnViajeDTO{
        return pasajerosService.buscarUltimaBusquedaDeUnPasajero(idPasajero)
    }

}
package ar.edu.unsam.phm.controller
import ar.edu.unsam.phm.dto.*
import ar.edu.unsam.phm.service.ChoferService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/chofer")
class ChoferesController {
    @Autowired
    lateinit var choferService: ChoferService

    @PostMapping("/home/filtro")
    fun getViajesFiltro(@RequestBody filtroViajes: FiltroViajeDTO, @RequestParam idChofer:Long): List<TarjetaViajeDTO> {
        return choferService.obtenerViajesFiltroChofer(filtroViajes, idChofer)
    }

    @GetMapping("/perfil/viajesrealizados")
    fun getViajesRealizados(@RequestParam idChofer: Long): List<TarjetaViajeDTO> {
        return choferService.obtenerViajesRealizadosChofer(idChofer)
    }
    @GetMapping("/perfil/calificaciones")
    fun getCalificaciones(@RequestParam idChofer:Long): List<TarjetaCalificacionDTO>{
        return choferService.obtenerCalificacionesChofer(idChofer)
    }
    @GetMapping("/perfil/informacion")
    fun getDatosChofer(@RequestParam idChofer: Long): InformacionChoferDTO{
        return choferService.infoChofer(idChofer)
    }
    @GetMapping("/perfil/importetotal")
    fun getTotalViajes(@RequestParam idChofer:Long): Double {
        return choferService.obtenerImporteTotalViajes(idChofer)
    }
    @PutMapping("/perfil/actualizardatos")
    fun putDatosChofer(@RequestBody datosActualizados: InformacionChoferDTO){
        choferService.actualizarInfoChofer(datosActualizados)
    }

    @GetMapping("/perfil/registroclicks")
    fun getRegistroClicks(@RequestParam idChofer: Long): List<RegistroClickDTO> {
        return choferService.obtenerRegistroClicks(idChofer)
    }


}
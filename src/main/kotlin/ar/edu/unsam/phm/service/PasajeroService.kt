package ar.edu.unsam.phm.service
import ar.edu.unsam.phm.dto.*
import ar.edu.unsam.phm.extras.*
import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.repository.*
import ar.edu.unsam.phm.errorHandling.*
import ar.edu.unsam.phm.neo4jRepository.AmigosDePasajerosRepository
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PasajerosService {
    @Autowired
    private lateinit var amigosDePasajerosRepository: AmigosDePasajerosRepository

    @Autowired
    lateinit var viajeService: ViajeService
    @Autowired
    lateinit var choferService: ChoferService
    @Autowired
    lateinit var calificacionService: CalificacionService
    @Autowired
    lateinit var pasajeroRepository: PasajeroRepository
    @Autowired
    lateinit var pasajeroAmigoRepository: AmigosDePasajerosRepository
    @Autowired
    lateinit var contadorClicksService: ContadorClicksService
    @Autowired
    lateinit var choferDeViajeRepository: ChoferDeViajeRepository
    @Autowired
    lateinit var ultimaBusquedaDeUnViajeService: UltimaBusquedaDeUnViajeService

    fun buscarViajesRealizados(idPasajero: Long): List<TarjetaViajeDTO> {
        val pasajero: Pasajero = findByUserDataId(idPasajero)
        return viajeService.obtenerViajesRealizadosPasajero(pasajero.id!!)
    }

    fun buscarViajesPendientesPasajero(idPasajero: Long): List<TarjetaViajeDTO> {
        val pasajero: Pasajero = findByUserDataId(idPasajero)
        return viajeService.obtenerViajesPendientesPasajero(pasajero.id!!)
    }

    @Transactional
    fun confirmarViaje(viaje: DetalleViajeDTO, idChofer:String, idPasajero: Long){
        val pasajeroDeViaje: Pasajero = findByUserDataId(idPasajero)
        val choferAsignado: Chofer = choferService.findbyChoferId(idChofer)
        val fechaInicioNueva: LocalDateTime =  Formateador().formatoLocalDateTime(viaje.fechaInicio)
        val choferPostgres = choferDeViajeRepository.findById(choferAsignado.id).orElse(choferDeViajeRepository.save(choferAsignado.toChoferDeViaje()))
        val nuevoViaje:Viaje = Viaje().apply {
            duracion = viaje.duracion
            origen = viaje.origen
            destino = viaje.destino
            cantidadPasajeros = viaje.cantidadDePasajeros
            fechaInicio = fechaInicioNueva
            this.asignarChofer(choferAsignado, choferPostgres)
            pasajero = pasajeroDeViaje
            asignarFechaFinalizacion()
            asignarPrecio()
        }
        this.verificarPuedePagarViaje(nuevoViaje)
        this.verificarDisponibilidadChofer(nuevoViaje)
        viajeService.viajeSave(nuevoViaje)
        choferService.agregarViajeAUnChofer(choferAsignado.id,nuevoViaje.toViajeParaChofer())
        this.decrementarSaldoPasajero(pasajeroDeViaje.id!!,nuevoViaje.precioPasajero)
        this.agregarViajeParaRelacion(idPasajero,nuevoViaje.choferDeViaje.id,nuevoViaje)
    }

    fun agregarViajeParaRelacion(idPasajero: Long, idChofer: String, viaje:Viaje){
        val pasajeroAmigoEncontrado: PasajeroAmigo = this.pasajeroAmigoRepository.findByIdPasajero(idPasajero).orElseThrow{throw NotFoundException("No se encontro pasajero en neo4j")}
        val choferAsignado: ChoferDeRelacionDeViaje = choferService.findbyChoferRelacionId(idChofer)
        val relacionDeViajeParaPasajero = RelacionDeViaje().apply {
            this.idViaje = viaje.id!!
            this.fechaInicio = viaje.fechaInicio
            this.fechaFinalizacion = viaje.fechaFinalizacion
            this.chofer = choferAsignado
        }

        pasajeroAmigoEncontrado.agregarUnViajeParaRelacion(relacionDeViajeParaPasajero)
        this.pasajeroAmigoRepository.save(pasajeroAmigoEncontrado)
    }

    fun decrementarSaldoPasajero(idPasajero:Long, monto:Double){
        try {
            this.pasajeroRepository.decrementarSaldo(idPasajero,monto)
        }catch (e:Exception){
            throw BusinessException("No se pudo decrementar el saldo porque el monto es mayor a lo que ya tiene")
        }
    }

    fun buscarCalificacionesPasajero(idPasajero: Long): List<TarjetaCalificacionDTO>{
        val pasajero: Pasajero = findByUserDataId(idPasajero)
        return  calificacionService.findByViajePasajeroId(pasajero.id!!)
    }

    @Transactional(readOnly=true)
    fun obtenerInfoPasajero(idPasajero: Long): InformacionPasajeroDTO {
        val pasajero: Pasajero = findByUserDataId(idPasajero)
        val pasajeroAmigoEncontrado = this.amigosDePasajerosRepository.findByIdPasajero(pasajero.id!!).orElseThrow { throw NotFoundException("No se encontro pasajero") }
        return pasajero.toInformacionPasajero(pasajeroAmigoEncontrado.listaPasajeroAmigos.toList())
    }

    @Transactional
    fun actualizarSaldoPasajero(idPasajero: Long, saldo:Double) {
        verificarSaldoNegativo(saldo)
        val pasajero: Pasajero = findByUserDataId(idPasajero)
        try {
            this.pasajeroRepository.incrementarSaldo(pasajero.id!!,saldo)
        }catch (e:Exception){
            throw BusinessException("No se pudo actualizar saldo")
        }
    }

    @Transactional
    fun actualizarInformacionPasajero(datosActualizados: InformacionPasajeroDTO){
        val pasajero: Pasajero = findByUserDataId(datosActualizados.idUserData!!)
        pasajero.apply {
            nombre = datosActualizados.nombre
            apellido = datosActualizados.apellido
            telefono = datosActualizados.telefono
        }
        pasajero.validadEntidad()
        pasajeroRepository.save(pasajero)
    }

    fun obtenerPasajeros(idPasajero: Long): List<AmigoDelAmigoDTO> {
        val pasajero: Pasajero = findByUserDataId(idPasajero)
        val noAmigosDelUsuario: List<PasajeroAmigo> = pasajeroAmigoRepository.encontrarAmigosDeAmigosConViajeMismoAnioYChofer(pasajero.id!!)
        return noAmigosDelUsuario.map { it.toAmigoDelAmigoDTO() }
    }
    @Transactional
    fun eliminarAmigoPasajero(idPasajero: Long, idAmigo: Long) {
        val pasajeroRelacionEncontrado: PasajeroAmigo = pasajeroAmigoRepository.findByIdPasajero(idPasajero).orElseThrow{throw NotFoundException("No se encontro pasajero en neo4j")}
        val amigoRelacionEncontrado: PasajeroAmigo = pasajeroAmigoRepository.findByIdPasajero(idAmigo).orElseThrow{throw NotFoundException("No se encontro pasajero en neo4j")}
        this.verificarEliminarAmigo(pasajeroRelacionEncontrado, amigoRelacionEncontrado)
        amigosDePasajerosRepository.eliminarRelacionAmigo(pasajeroRelacionEncontrado.idPasajero,amigoRelacionEncontrado.idPasajero)
    }

    @Transactional
    fun agregarAmigoPasajero(idPasajero: Long, idAmigo: Long ) {
        val pasajeroRelacionEncontrado: PasajeroAmigo = pasajeroAmigoRepository.findByIdPasajero(idPasajero).orElseThrow{throw NotFoundException("No se encontro pasajero en neo4j")}
        val amigoRelacionEncontrado: PasajeroAmigo = pasajeroAmigoRepository.findByIdPasajero(idAmigo).orElseThrow{throw NotFoundException("No se encontro pasajero en neo4j")}
        this.verificarAgregarAmigo(pasajeroRelacionEncontrado, amigoRelacionEncontrado)
        pasajeroRelacionEncontrado.agregarUnAmigoParaRelacion(amigoRelacionEncontrado)
        amigosDePasajerosRepository.save(pasajeroRelacionEncontrado)
    }

    fun findByUserDataId(idParam: Long): Pasajero {
        return pasajeroRepository.findByUserDataId(idParam).orElseThrow {
            throw NotFoundException("No se encontró el pasajero indicado: $idParam")
        }
    }

    private fun verificarDisponibilidadChofer(nuevoViaje: Viaje) {
            return viajeService.verificarDisponibilidadChofer(nuevoViaje)
    }

    private fun verificarSaldoNegativo(saldo: Double) {
        if(saldo < 0.0){
            throw BusinessException("Esta intentando agregar un saldo negativo.")
        }
    }

    private fun verificarEliminarAmigo(pasajero: PasajeroAmigo, amigo: PasajeroAmigo) {
        if(!amigosDePasajerosRepository.existeRelacionAmigo(pasajero.idPasajero,amigo.idPasajero)){
            throw BusinessException("No lo tiene en su lista de amigos a ${amigo.nombre} ${amigo.apellido} ")
        }
    }

    private fun verificarAgregarAmigo(pasajero: PasajeroAmigo, amigo: PasajeroAmigo) {
        if(amigosDePasajerosRepository.existeRelacionAmigo(pasajero.idPasajero,amigo.idPasajero)){
            throw BusinessException("No se puede agregar. Ya se encuentra ${amigo.nombre} ${amigo.apellido} en su lista.")
        }
    }

    private fun verificarPuedePagarViaje(nuevoViaje: Viaje) {
        val pasajero = nuevoViaje.pasajero
        val precio = nuevoViaje.precioPasajero
        if(!pasajero.saldoDiponible(precio)){
            throw NotFoundException("Saldo insuficiente para realizar este viaje.")
        }
    }

    fun obtenerChoferesDisponibles(viaje: DetalleViajeDTO, idPasajero: Long): List<TarjetaChoferDTO>{
        val pasajero = this.findByUserDataId(idPasajero)
        return choferService.choferesDisponibles(viaje, pasajero.id!!)
    }


    fun eliminarCalificacion(idCalificacion: Long) {
        calificacionService.deleteCalificacion(idCalificacion)
    }

    fun calificarViajeRealizado(nuevaCalificacionParams: NuevaCalificacionDTO) {
        return calificacionService.calificarViajeRealizado(nuevaCalificacionParams)
    }

    fun obtenerDetalleChoferViaje(idChofer: String,idPasajero: Long): DetalleChoferParaViajeDTO{
        this.registrarClick(idChofer,idPasajero)
        return choferService.obtenerDetalleViajeChofer(idChofer)
    }

    fun registrarClick(idChofer: String, idPasajero: Long) {
        val chofer = this.choferService.findbyChoferId(idChofer)
        val pasajero = this.findByUserDataId(idPasajero)

        this.contadorClicksService.registrarClick(chofer,pasajero)
    }

    fun buscarUltimaBusquedaDeUnPasajero(idPasajero: Long): UltimaBusquedaDeUnViajeDTO{
        return this.ultimaBusquedaDeUnViajeService.comprobarUltimaBusquedaDeUnViaje(idPasajero)
    }

    fun registrarUnNuevoPasajero(registerData: RegisterRequestDataDTO, nuevoUserData: UserData){
        val nuevoPasajero: Pasajero = Pasajero().apply {
            nombre = registerData.nombre
            apellido = registerData.apellido
            userData = nuevoUserData
            telefono = registerData.telefono
            edad = registerData.edad
            saldo = 1
        }

        nuevoPasajero.validadEntidad()
        pasajeroRepository.save(nuevoPasajero)
        pasajeroAmigoRepository.save(nuevoPasajero.toPasajeroAmigo())
    }
}

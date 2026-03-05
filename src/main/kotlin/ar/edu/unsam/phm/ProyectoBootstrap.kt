package ar.edu.unsam.phm

import java.time.LocalDateTime
import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.errorHandling.NotFoundException
import ar.edu.unsam.phm.neo4jRepository.AmigosDePasajerosRepository
import ar.edu.unsam.phm.neo4jRepository.ChoferDeRelacionDeViajeRepository
import ar.edu.unsam.phm.neo4jRepository.RelacionDeViajeRepository
import ar.edu.unsam.phm.repository.*
import org.springframework.stereotype.Service
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
class ProyectoBootstrap : InitializingBean {
    @Autowired
    private lateinit var repoCalificacion: CalificacionRepository
    @Autowired
    private lateinit var userDataRepo: UserDataRepository
    @Autowired
    private lateinit var repoViajes: ViajeRepository
    @Autowired
    private lateinit var repoChofer: ChoferRepository
    @Autowired
    private lateinit var repoChoferDeViaje: ChoferDeViajeRepository
    @Autowired
    private lateinit var repoPasajero: PasajeroRepository
    @Autowired
    private lateinit var repoAmigosDePasajeros: AmigosDePasajerosRepository
    @Autowired
    private lateinit var repoChoferDeRelacionDeViaje: ChoferDeRelacionDeViajeRepository
    @Autowired
    private lateinit var repoRelacionDeViaje: RelacionDeViajeRepository


    private lateinit var userData1: UserData
    private lateinit var userData2: UserData
    private lateinit var userData3: UserData
    private lateinit var userData4: UserData
    private lateinit var userData5: UserData
    private lateinit var userData6: UserData
    private lateinit var userData7: UserData
    private lateinit var userData8: UserData

    private lateinit var pasajero1: Pasajero
    private lateinit var pasajero2: Pasajero
    private lateinit var pasajero3: Pasajero
    private lateinit var pasajero4: Pasajero

    private lateinit var choferSimple1 : ChoferSimple
    private lateinit var choferPremium1 : ChoferPremium
    private lateinit var choferMoto1 : ChoferMoto
    private lateinit var choferMoto2 : ChoferMoto

    private lateinit var viajePendiente1:Viaje
    private lateinit var viajePendiente2:Viaje
    private lateinit var viajePendiente3:Viaje
    private lateinit var viajePendiente4:Viaje
    private lateinit var viajePendiente5:Viaje
    private lateinit var viajePendiente6:Viaje
    private lateinit var viajePendiente7:Viaje
    private lateinit var viajePendiente8:Viaje
    private lateinit var viajeRealizado1:Viaje
    private lateinit var viajeRealizado2:Viaje
    private lateinit var viajeRealizado3:Viaje
    private lateinit var viajeRealizado4:Viaje
    private lateinit var viajeRealizado5:Viaje
    private lateinit var viajeRealizado6:Viaje
    private lateinit var viajeRealizado7:Viaje
    private lateinit var viajeRealizado8:Viaje
    private lateinit var viajeRealizado9:Viaje

    private lateinit var calificacion1:Calificacion
    private lateinit var calificacion2:Calificacion
    private lateinit var calificacion3:Calificacion
    private lateinit var calificacion4:Calificacion


    val logger: Logger = LoggerFactory.getLogger(ProyectoBootstrap::class.java)

    private var passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    //map que va a contener los ID creados de los choferes por MongoRepository
    val choferUsernameToIdMap = mutableMapOf<String, String>()

    // USERDATA
    fun iniciarUserData() {
        userData1 = UserData().apply {
            id=1
            username="Pgarcia"
            password=passwordEncoder.encode("1234")
            tipoUsuario= TipoUsuario.PASAJERO
            fotoPerfil="https://plus.unsplash.com/premium_photo-1689977968861-9c91dbb16049?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8c21pbGV8ZW58MHx8MHx8fDA%3D"
        }
        userData2 = UserData().apply {
            id=2
            username="Arodriguez"
            password=passwordEncoder.encode("1234")
            tipoUsuario= TipoUsuario.PASAJERO
            fotoPerfil="https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8cGVyZmlsfGVufDB8fDB8fHww"
        }
        userData3 = UserData().apply {
            id=3
            username="Cmartinez"
            password=passwordEncoder.encode("1234")
            tipoUsuario= TipoUsuario.PASAJERO
            fotoPerfil="https://plus.unsplash.com/premium_photo-1689568126014-06fea9d5d341?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8cGVyZmlsfGVufDB8fDB8fHww"
        }
        userData4 = UserData().apply {
            id=4
            username="Pbenitez"
            password=passwordEncoder.encode("1234")
            tipoUsuario= TipoUsuario.PASAJERO
            fotoPerfil="https://images.unsplash.com/photo-1438761681033-6461ffad8d80?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        }
        userData5 = UserData().apply {
            id=5
            username="JLopez"
            password=passwordEncoder.encode("1234")
            tipoUsuario= TipoUsuario.CHOFER
            fotoPerfil="https://media.istockphoto.com/id/1217653245/photo/happy-driver-transporting-a-woman-in-a-car.jpg?s=2048x2048&w=is&k=20&c=1X3XNwqke3g0hb2LhWq0cJMQ21ARyMH-bp5FR6NekJ8="
        }
        userData6 = UserData().apply {
            id=6
            username="PMecozzi"
            password=passwordEncoder.encode("1234")
            tipoUsuario= TipoUsuario.CHOFER
            fotoPerfil="https://plus.unsplash.com/premium_photo-1661402137057-8912a733c52f?q=80&w=2069&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        }
        userData7 = UserData().apply {
            id=7
            username="JCejas"
            password=passwordEncoder.encode("1234")
            tipoUsuario= TipoUsuario.CHOFER
            fotoPerfil="https://plus.unsplash.com/premium_photo-1664300191065-4dcef93a069c?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        }
        userData8 = UserData().apply {
            id=8
            username="TCoronel"
            password=passwordEncoder.encode("1234")
            tipoUsuario= TipoUsuario.CHOFER
            fotoPerfil="https://plus.unsplash.com/premium_photo-1664300191065-4dcef93a069c?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        }

        val todosLosLogins = listOf(userData1, userData2, userData3, userData4, userData5, userData6, userData7, userData8)

        this.crearUserDataSiNoExiste(todosLosLogins)
    }

    fun encontrarUserData(idUserData: Long):UserData {
        val userData: UserData = userDataRepo.findById(idUserData).orElseThrow {
            throw NotFoundException("No se encontro el userData con id: ${idUserData}")
        }
        return userData
    }

    fun crearUserDataSiNoExiste(userDatas: List<UserData>) {
        userDatas.forEach {
            if(userDataRepo.findByUsername(it.username).isEmpty){
                userDataRepo.save(it)
                logger.info("Usuario ${it.username} creado")
            }else{
                logger.info("Usuario ${it.username} ya existe")
                val usuario = userDataRepo.findByUsername(it.username).get()
                userDataRepo.save(usuario)
            }
        }
    }


    // PASAJERO POSTGRES + NEO4J
    fun iniciarPasajeros() {
        // Limpiar Neo4j
        repoAmigosDePasajeros.deleteAll()

        // Crear pasajeros
        pasajero1 = Pasajero().apply {
            userData = userData1
            nombre = "Pablo"
            apellido = "Garcia"
            saldo = 10000000000.0
            edad = 18
            telefono = "1122334455"
        }
        pasajero2 = Pasajero().apply {
            userData = userData2
            nombre = "Ana"
            apellido = "Rodriguez"
            saldo = 10000000000.0
            edad = 18
            telefono = "1122334455"
        }
        pasajero3 = Pasajero().apply {
            userData = userData3
            nombre = "Carlos"
            apellido = "Martínez"
            saldo = 800.0
            edad = 30
            telefono = "6677889900"
        }
        pasajero4 = Pasajero().apply {
            userData = userData4
            nombre = "Pedro"
            apellido = "Benitez"
            saldo = 1200.0
            edad = 22
            telefono = "0099887766"
        }

        // Crear relaciones de amistad

        // Persistir en Postgres
        val todosLosPasajeros = listOf(pasajero1, pasajero2, pasajero3, pasajero4)
        val pasajerosPersistidos = repoPasajero.saveAll(todosLosPasajeros)

        // Crear nodos en Neo4j
        val pasajerosAmigos = pasajerosPersistidos.map { it.toPasajeroAmigo() }
        repoAmigosDePasajeros.saveAll(pasajerosAmigos)

        this.agregarAmigosParaPasajerosAmigos(todosLosPasajeros)
    }


    //Agregar amigos a pasajeros de neo4j
    fun agregarAmigosParaPasajerosAmigos(todosLosPasajeros:List<Pasajero>){
        val pasajerosAmigosEncontrados = todosLosPasajeros.map{this.obtenerPasajeroAmigo(it.id!!)}

        pasajerosAmigosEncontrados[0].agregarUnAmigoParaRelacion(pasajerosAmigosEncontrados[1])
        pasajerosAmigosEncontrados[0].agregarUnAmigoParaRelacion(pasajerosAmigosEncontrados[2])
        pasajerosAmigosEncontrados[1].agregarUnAmigoParaRelacion(pasajerosAmigosEncontrados[0])
        pasajerosAmigosEncontrados[2].agregarUnAmigoParaRelacion(pasajerosAmigosEncontrados[0])
        pasajerosAmigosEncontrados[2].agregarUnAmigoParaRelacion(pasajerosAmigosEncontrados[3])
        pasajerosAmigosEncontrados[3].agregarUnAmigoParaRelacion(pasajerosAmigosEncontrados[2])

        this.repoAmigosDePasajeros.saveAll(pasajerosAmigosEncontrados)
    }

    //CHOFER [MONGO] + CHOFER VIAJE [POSTGRES] + CHOFER DE RELACION DE VIAJE [NEO4J]
    fun iniciarChoferes() {
        repoChofer.deleteAll()
        this.repoChoferDeRelacionDeViaje.deleteAll()

        val userDataChoferSimple1:UserData = this.encontrarUserData(userData5.id!!)

        val userDataChoferPremium1:UserData  = this.encontrarUserData(userData6.id!!)

        val userDataChoferMoto1:UserData = this.encontrarUserData(userData7.id!!)

        val userDataChoferMoto2:UserData = this.encontrarUserData(userData8.id!!)

        choferSimple1 = ChoferSimple(
            userDataId= userDataChoferSimple1.id,
            nombre="Jorge",
            apellido="Lopez",
            fotoPerfil = userDataChoferSimple1.fotoPerfil,
            precioBase=1000.0,
            modeloVehiculo=1991,
            marcaVehiculo="Ford",
            patenteVehiculo="AAA111",
            promedioDePuntaje=0.0,
        )
        choferPremium1 = ChoferPremium(
            userDataId= userDataChoferPremium1.id,
            nombre="Pepe",
            apellido="Mecozzi",
            fotoPerfil = userDataChoferPremium1.fotoPerfil,
            precioBase=1000.0,
            modeloVehiculo=2020,
            marcaVehiculo="Fiat",
            patenteVehiculo="BBB222",
            promedioDePuntaje=0.0,
        )
        choferMoto1 = ChoferMoto(
            userDataId= userDataChoferMoto1.id,
            nombre="Joaquin",
            apellido="Cejas",
            fotoPerfil = userDataChoferMoto1.fotoPerfil,
            precioBase=1000.0,
            modeloVehiculo=2019,
            marcaVehiculo="Honda",
            patenteVehiculo="CCC333",
            promedioDePuntaje=0.0,
        )
        choferMoto2 = ChoferMoto(
            userDataId= userDataChoferMoto2.id,
            nombre="Tamara",
            apellido="Coronel",
            fotoPerfil = userDataChoferMoto2.fotoPerfil,
            precioBase=1000.0,
            modeloVehiculo=2019,
            marcaVehiculo="Honda",
            patenteVehiculo="CCC333",
            promedioDePuntaje=0.0,
        )

        val todosLosChoferes = listOf(choferSimple1, choferPremium1, choferMoto1, choferMoto2)

        val choferesGuardados = repoChofer.saveAll(todosLosChoferes) // Save MONGO

        choferesGuardados.forEach { chofer -> chofer.id.let { choferUsernameToIdMap[this.encontrarUserData(chofer.userDataId!!).username] = it } }

        val choferesDeViajes = choferesGuardados.map {this.convertirChoferAChoferDeViaje(it)}

        val choferesDeViajeGuardados = repoChoferDeViaje.saveAll(choferesDeViajes) // Save POSTGRES

        choferesDeViajeGuardados.forEach{ choferDeViaje -> repoChoferDeRelacionDeViaje.save(choferDeViaje.toChoferDeRelacionDeViaje())} // Save NEO

    }
    
    fun convertirChoferAChoferDeViaje(chofer:Chofer): ChoferDeViaje{
        return chofer.toChoferDeViaje()
    }


    // Viajes
    fun iniciarViajes() {
        this.repoRelacionDeViaje.deleteAll()
        viajePendiente1 = Viaje().apply {
            id=1
            duracion=30
            origen="federico Lacroze, 4100"
            destino="Calle 123, 4500"
            cantidadPasajeros= 2
            fechaInicio= LocalDateTime.now().plusHours(3)
        }

        viajePendiente2 = Viaje().apply {
            id = 2
            duracion = 40
            origen = "federico Lacroze, 4700"
            destino = "Calle 123, 4100"
            cantidadPasajeros = 3
            fechaInicio = LocalDateTime.now().plusMinutes(10)
        }

        viajePendiente3 = Viaje().apply {
            id = 3
            duracion = 50
            origen = "callao y corrientes, 5100"
            destino = "Calle 123, 7600"
            cantidadPasajeros = 1
            fechaInicio = LocalDateTime.now().plusMinutes(5)
        }

        viajePendiente4 = Viaje().apply {
            id = 4
            duracion = 45
            origen = "federico Lacroze, 4500"
            destino = "Calle 123, 4200"
            cantidadPasajeros = 4
            fechaInicio = LocalDateTime.now().plusMinutes(20)
        }

        viajePendiente5 = Viaje().apply {
            id = 5
            duracion = 15
            origen = "federico Lacroze, 3500"
            destino = "Calle 123, 4180"
            cantidadPasajeros = 2
            fechaInicio = LocalDateTime.now().plusDays(1)
        }

        viajePendiente6 = Viaje().apply {
            id = 6
            duracion = 35
            origen = "callao y corrientes, 4780"
            destino = "Calle 123, 6700"
            cantidadPasajeros = 3
            fechaInicio = LocalDateTime.now().plusDays(1)
        }

        viajePendiente7 = Viaje().apply {
            id = 7
            duracion = 70
            origen = "federico Lacroze, 1200"
            destino = "Calle 123, 3400"
            cantidadPasajeros = 1
            fechaInicio = LocalDateTime.of(2025, 6, 10, 13, 40)
        }

        viajePendiente8 = Viaje().apply {
            id = 8
            duracion = 85
            origen = "federico Lacroze, 6500"
            destino = "Calle 123, 6200"
            cantidadPasajeros = 2
            fechaInicio = LocalDateTime.of(2025, 6, 18, 13, 40)
        }

        viajeRealizado1 = Viaje().apply {
            id = 9
            duracion = 15
            origen = "Amadeo Carizo, 4100"
            destino = "Calle 123, 4500"
            cantidadPasajeros = 2
            fechaInicio = LocalDateTime.of(2025, 2, 20, 13, 40)
        }

        viajeRealizado2 = Viaje().apply {
            id = 10
            duracion = 25
            origen = "Picazzo, 4700"
            destino = "Calle 123, 4100"
            cantidadPasajeros = 3
            fechaInicio = LocalDateTime.of(2025, 1, 15, 13, 40)
        }

        viajeRealizado3 = Viaje().apply {
            id = 11
            duracion = 75
            origen = "Calle Falopa, 5100"
            destino = "Calle Siempre Viva, 7600"
            cantidadPasajeros = 1
            fechaInicio = LocalDateTime.of(2025, 2, 10, 13, 40)
        }

        viajeRealizado4 = Viaje().apply {
            id = 12
            duracion = 56
            origen = "Calle Primogenio, 4500"
            destino = "Calle Fernandez, 4200"
            cantidadPasajeros = 4
            fechaInicio = LocalDateTime.of(2025, 1, 13, 13, 40)
        }

        viajeRealizado5 = Viaje().apply {
            id = 13
            duracion = 21
            origen = "Calle Ricardo, 3500"
            destino = "Calle Perez, 4180"
            cantidadPasajeros = 2
            fechaInicio = LocalDateTime.of(2025, 2, 9, 13, 40)
        }

        viajeRealizado6 = Viaje().apply {
            id = 14
            duracion = 25
            origen = "callao y corrientes, 4780"
            destino = "Calle 127, 6700"
            cantidadPasajeros = 3
            fechaInicio = LocalDateTime.of(2025, 3, 4, 13, 40)
        }

        viajeRealizado7 = Viaje().apply {
            id = 15
            duracion = 90
            origen = "federico Lacroze, 1200"
            destino = "Calle 130, 3400"
            cantidadPasajeros = 1
            fechaInicio = LocalDateTime.of(2025, 3, 10, 13, 40)
        }

        viajeRealizado8 = Viaje().apply {
            id = 16
            duracion = 5
            origen = "federico Lacroze, 6500"
            destino = "Calle 120, 6200"
            cantidadPasajeros = 2
            fechaInicio = LocalDateTime.of(2025, 2, 12, 13, 40)
        }
        viajeRealizado9 = Viaje().apply {
            id = 17
            duracion = 5
            origen = "federico Lacroze, 6500"
            destino = "Calle 120, 6200"
            cantidadPasajeros = 2
            fechaInicio = LocalDateTime.of(2025, 2, 12, 13, 40)
        }

        //aca junto los viajes en un lista
        val todosLosViajes = listOf(
            viajePendiente1,
            viajePendiente2,
            viajePendiente3,
            viajePendiente4,
            viajePendiente5,
            viajePendiente6,
            viajePendiente7,
            viajePendiente8,
            viajeRealizado1,
            viajeRealizado2,
            viajeRealizado3,
            viajeRealizado4,
            viajeRealizado5,
            viajeRealizado6,
            viajeRealizado7,
            viajeRealizado8,
            viajeRealizado9

        )

        this.asignarChoferDeViaje()
        this.asignarPasajeroDeViaje()
        this.asignarPrecioViaje()
        this.asignarDuracionFinDeViaje()
        //aca guardo los viajes
        val viajesGuardados = this.repoViajes.saveAll(todosLosViajes)
        todosLosViajes.forEach{this.agregarViajeParaUnChofer(it.choferMongo, it)}
        viajesGuardados.forEach{ viaje -> this.crearRelacionDeViaje(viaje)}

    }

    fun agregarViajeParaUnChofer(chofer: Chofer, viaje:Viaje){
        val choferDelViaje = repoChofer.findById(chofer.id).orElseThrow { throw NotFoundException("No se encontro chofer.") }
        choferDelViaje.agregarNuevoViajeParaChofer(viaje.toViajeParaChofer())
        repoChofer.save(choferDelViaje)
    }


    fun obtenerChofer(id:String):Chofer{
        return repoChofer.findById(id).orElseThrow{throw NotFoundException("No se encontro un chofer con el id proporcionado") }
    }

    fun obtenerChoferDeViaje(id: String): ChoferDeViaje {
        return repoChoferDeViaje.findById(id).orElseThrow { throw NotFoundException("No se encontro un chofer con ese id") }
    }

    fun obtenerPasajeroAmigo(idPasajero: Long): PasajeroAmigo{
        return this.repoAmigosDePasajeros.findByIdPasajero(idPasajero).orElseThrow { throw NotFoundException("No se encontro un pasajero con ese id") }
    }

    fun obtenerChoferDeRelacionDeViaje(idChofer: String): ChoferDeRelacionDeViaje{
        return this.repoChoferDeRelacionDeViaje.findByIdChofer(idChofer).orElseThrow { throw NotFoundException("No se encontro un chofer con ese id") }
    }

    fun crearRelacionDeViaje(viaje:Viaje){
        val choferDeRelacionDeViajeEncontrado = this.obtenerChoferDeRelacionDeViaje(viaje.choferDeViaje.id)
        val pasajeroAmigoEncontrado = this.obtenerPasajeroAmigo(viaje.pasajero.id!!)
        val relacionDeViaje = RelacionDeViaje().apply {
            this.idViaje = viaje.id!!
            this.fechaInicio = viaje.fechaInicio
            this.fechaFinalizacion = viaje.fechaFinalizacion
            this.chofer = choferDeRelacionDeViajeEncontrado


        }
        pasajeroAmigoEncontrado.agregarUnViajeParaRelacion(relacionDeViaje)
        this.repoAmigosDePasajeros.save(pasajeroAmigoEncontrado)

    }

    fun asignarChoferDeViaje(){
        val jLopezChofer = obtenerChofer(choferUsernameToIdMap["JLopez"]!!)
        val jCejasChofer = obtenerChofer(choferUsernameToIdMap["JCejas"]!!)
        val pMecozziChofer = obtenerChofer(choferUsernameToIdMap["PMecozzi"]!!)
        val tCoronelChofer = obtenerChofer(choferUsernameToIdMap["TCoronel"]!!)
        val jLopezChoferDeViaje = obtenerChoferDeViaje(choferUsernameToIdMap["JLopez"]!!)
        val jCejasChoferDeViaje = obtenerChoferDeViaje(choferUsernameToIdMap["JCejas"]!!)
        val pMecozziChoferDeViaje = obtenerChoferDeViaje(choferUsernameToIdMap["PMecozzi"]!!)
        val tCoronelChoferDeViaje = obtenerChoferDeViaje(choferUsernameToIdMap["TCoronel"]!!)

        viajePendiente1.asignarChofer(jLopezChofer,jLopezChoferDeViaje)
        viajePendiente2.asignarChofer(jCejasChofer, jCejasChoferDeViaje)
        viajePendiente3.asignarChofer(pMecozziChofer,pMecozziChoferDeViaje)
        viajePendiente4.asignarChofer(jLopezChofer,jLopezChoferDeViaje)
        viajePendiente5.asignarChofer(pMecozziChofer,pMecozziChoferDeViaje)
        viajePendiente6.asignarChofer(pMecozziChofer,pMecozziChoferDeViaje)
        viajePendiente7.asignarChofer(jLopezChofer,jLopezChoferDeViaje)
        viajePendiente8.asignarChofer(jCejasChofer,jCejasChoferDeViaje)
        viajeRealizado1.asignarChofer(jLopezChofer,jLopezChoferDeViaje)
        viajeRealizado2.asignarChofer(jCejasChofer,jCejasChoferDeViaje)
        viajeRealizado3.asignarChofer(pMecozziChofer,pMecozziChoferDeViaje)
        viajeRealizado4.asignarChofer(jLopezChofer,jLopezChoferDeViaje)
        viajeRealizado5.asignarChofer(jCejasChofer,jCejasChoferDeViaje)
        viajeRealizado6.asignarChofer(pMecozziChofer,pMecozziChoferDeViaje)
        viajeRealizado7.asignarChofer(jLopezChofer,jLopezChoferDeViaje)
        viajeRealizado8.asignarChofer(jCejasChofer,jCejasChoferDeViaje)
        viajeRealizado9.asignarChofer(tCoronelChofer,tCoronelChoferDeViaje)
    }

    fun asignarPasajeroDeViaje(){
        viajePendiente1.asignarPasajero(pasajero1)
        viajePendiente2.asignarPasajero(pasajero2)
        viajePendiente3.asignarPasajero(pasajero3)
        viajePendiente4.asignarPasajero(pasajero4)
        viajePendiente5.asignarPasajero(pasajero1)
        viajePendiente6.asignarPasajero(pasajero2)
        viajePendiente7.asignarPasajero(pasajero3)
        viajePendiente8.asignarPasajero(pasajero4)
        viajeRealizado1.asignarPasajero(pasajero1)
        viajeRealizado2.asignarPasajero(pasajero2)
        viajeRealizado3.asignarPasajero(pasajero3)
        viajeRealizado4.asignarPasajero(pasajero4)
        viajeRealizado5.asignarPasajero(pasajero1)
        viajeRealizado6.asignarPasajero(pasajero2)
        viajeRealizado7.asignarPasajero(pasajero3)
        viajeRealizado8.asignarPasajero(pasajero4)
        viajeRealizado9.asignarPasajero(pasajero4)
    }

    fun asignarPrecioViaje(){
        viajePendiente1.asignarPrecio()
        viajePendiente2.asignarPrecio()
        viajePendiente3.asignarPrecio()
        viajePendiente4.asignarPrecio()
        viajePendiente5.asignarPrecio()
        viajePendiente6.asignarPrecio()
        viajePendiente7.asignarPrecio()
        viajePendiente8.asignarPrecio()
        viajeRealizado1.asignarPrecio()
        viajeRealizado2.asignarPrecio()
        viajeRealizado3.asignarPrecio()
        viajeRealizado4.asignarPrecio()
        viajeRealizado5.asignarPrecio()
        viajeRealizado6.asignarPrecio()
        viajeRealizado7.asignarPrecio()
        viajeRealizado8.asignarPrecio()
        viajeRealizado9.asignarPrecio()
    }

    fun asignarDuracionFinDeViaje(){
        viajePendiente1.asignarFechaFinalizacion()
        viajePendiente2.asignarFechaFinalizacion()
        viajePendiente3.asignarFechaFinalizacion()
        viajePendiente4.asignarFechaFinalizacion()
        viajePendiente5.asignarFechaFinalizacion()
        viajePendiente6.asignarFechaFinalizacion()
        viajePendiente7.asignarFechaFinalizacion()
        viajePendiente8.asignarFechaFinalizacion()
        viajeRealizado1.asignarFechaFinalizacion()
        viajeRealizado2.asignarFechaFinalizacion()
        viajeRealizado3.asignarFechaFinalizacion()
        viajeRealizado4.asignarFechaFinalizacion()
        viajeRealizado5.asignarFechaFinalizacion()
        viajeRealizado6.asignarFechaFinalizacion()
        viajeRealizado7.asignarFechaFinalizacion()
        viajeRealizado8.asignarFechaFinalizacion()
        viajeRealizado9.asignarFechaFinalizacion()
    }

    fun iniciarCalificaciones(){
        calificacion1 = Calificacion().apply {
            id = 1
            viaje = viajeRealizado1
            puntaje = 4
            comentario = "Fue un viaje muy bueno, me tocó un chofer increíble"
        }

        calificacion2 = Calificacion().apply {
            id = 2
            viaje = viajeRealizado2
            puntaje = 1
            comentario = "Fue un viaje para el orto, el chofer no pegaba ni una, se pasaba los semáforos en rojo y ni siquiera bajaba la música"
        }

        calificacion3 = Calificacion().apply {
            id = 3
            viaje = viajeRealizado3
            puntaje = 5
            comentario = "Fue un viaje increíble, el chofer fue muy amable y el viaje fue agradable"
        }

        calificacion4 = Calificacion().apply {
            id = 4
            viaje = viajeRealizado4
            puntaje = 2
            comentario = "La verdad que no me gustó para nada el viaje, el chofer que me tocó era un desastre, no sirve ni siquiera como chofer"
        }


        viajeRealizado1.seCalifica()
        viajeRealizado2.seCalifica()
        viajeRealizado3.seCalifica()
        viajeRealizado4.seCalifica()

        //aca junto los pasjeros en un lista
        val todasLasCalificaciones = listOf(
            calificacion1,
            calificacion2,
            calificacion3,
            calificacion4,
        )

        val viajesCalificados = listOf(
            viajeRealizado1,
            viajeRealizado2,
            viajeRealizado3,
            viajeRealizado4
        )

        repoViajes.saveAll(viajesCalificados)
        this.crearCalificacionSiNoExiste(todasLasCalificaciones)
    }

    fun crearCalificacionSiNoExiste(calificaciones: List<Calificacion>){
        calificaciones.forEach {
            if(repoCalificacion.findById(it.id!!).isEmpty){
                repoCalificacion.save(it)
            }else{
                logger.info("La calificacion con id ${it.id} ya existe")
            }
        }
    }

    fun actualizarPromediosChoferes() {
        val choferes = repoChofer.findAll()

        choferes.forEach { chofer ->
            if(chofer.promedioDePuntaje == 0.0) {
                val calificacionesChofer = repoCalificacion.findByViajeChoferDeViajeId(chofer.id)
                if (calificacionesChofer.isNotEmpty()) {
                    val promedio = calificacionesChofer.map { it.puntaje }.average()
                    chofer.actualizarPromedioPuntaje(promedio)
                } else {
                    chofer.actualizarPromedioPuntaje(0.0)
                }
                repoChofer.save(chofer)
            }
            else{
                logger.info("Ya se actualizo el promedio del chofer con id ${chofer.id}")
            }
        }
    }

    override fun afterPropertiesSet() {
        if(repoCalificacion.count() == 0L) {
            this.iniciarUserData()
            this.iniciarPasajeros()
            this.iniciarChoferes()
            this.iniciarViajes()
            this.iniciarCalificaciones()
            this.actualizarPromediosChoferes()
        }
    }
}
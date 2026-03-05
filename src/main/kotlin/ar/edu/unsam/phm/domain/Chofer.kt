package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.errorHandling.BusinessException
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document

@Document (collection = "Chofer")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes(
    JsonSubTypes.Type(value = ChoferSimple::class, name = "CSIMPLE"),
    JsonSubTypes.Type(value = ChoferPremium::class, name = "CPREMIUM"),
    JsonSubTypes.Type(value = ChoferMoto::class, name = "CMOTO")
)
abstract class Chofer (
    var userDataId: Long?,
    var nombre: String,
    var apellido: String,
    var fotoPerfil: String,
    var precioBase: Double,
    var modeloVehiculo: Int,
    var marcaVehiculo: String,
    var patenteVehiculo: String,
    var promedioDePuntaje: Double,
){
    @Id
    lateinit var id: String

    fun validadEntidad(){
        require(nombre.isNotBlank() && apellido.isNotBlank()){
            throw BusinessException("El nombre y apellido del chofer no puede estar vacio")
        }
        require(precioBase > 0.0){
            throw BusinessException("El precio base del viaje tiene que ser mayor a 0")
        }
        require(patenteVehiculo.isNotBlank()){
            throw BusinessException("La patente del vehiculo no puede estar vacia")
        }
        require(marcaVehiculo.isNotBlank()){
            throw BusinessException("La marca del vehiculo no puede estar vacia")
        }
        require(modeloVehiculo > 0){
            throw BusinessException("El modelo del vehiculo tiene que ser mayor a 0")
        }
    }

    var viajesDelChofer:MutableList<ViajeParaChofer> = mutableListOf()

    fun costoComision(viaje: Viaje): Double = this.costoBase(viaje) + this.costoBase(viaje) * 0.05
    fun costoBase(viaje: Viaje): Double = this.precioBase + this.costoPlus(viaje)

    abstract fun costoPlus(viaje: Viaje): Double

    fun actualizarPromedioPuntaje(nuevoPuntaje: Double) {
        this.promedioDePuntaje = nuevoPuntaje
    }

    fun agregarNuevoViajeParaChofer(viajeParaChofer: ViajeParaChofer){
        this.viajesDelChofer.add(viajeParaChofer)
    }
}

@TypeAlias("CSIMPLE")
class ChoferSimple(
    userDataId: Long?,
    nombre: String,
    apellido: String,
    fotoPerfil: String,
    precioBase: Double,
    modeloVehiculo: Int,
    marcaVehiculo: String,
    patenteVehiculo: String,
    promedioDePuntaje: Double,
    ) : Chofer( userDataId,nombre, apellido, fotoPerfil, precioBase, modeloVehiculo, marcaVehiculo, patenteVehiculo, promedioDePuntaje) {
    @Transient
    private var tarifaSimple = 1000.0
    override fun costoPlus(viaje: Viaje): Double = viaje.duracion * tarifaSimple
}

@TypeAlias("CPREMIUM")
class ChoferPremium(
    userDataId: Long?,
    nombre: String,
    apellido: String,
    fotoPerfil: String,
    precioBase: Double,
    modeloVehiculo: Int,
    marcaVehiculo: String,
    patenteVehiculo: String,
    promedioDePuntaje: Double,

) : Chofer( userDataId,nombre, apellido, fotoPerfil, precioBase, modeloVehiculo, marcaVehiculo, patenteVehiculo, promedioDePuntaje) {

    @Transient
    private var tarifaPremiumUnPasajero = 2000.0

    @Transient
    private var tarifaPremiumMuchosPasajero = 1500.0

    override fun costoPlus(viaje:Viaje): Double {
        return if (viaje.cantidadPasajeros > 1) viaje.duracion * tarifaPremiumMuchosPasajero
        else viaje.duracion * tarifaPremiumUnPasajero
    }
}

@TypeAlias("CMOTO")
class ChoferMoto(
    userDataId: Long?,
    nombre: String,
    apellido: String,
    fotoPerfil: String,
    precioBase: Double,
    modeloVehiculo: Int,
    marcaVehiculo: String,
    patenteVehiculo: String,
    promedioDePuntaje: Double,

) : Chofer( userDataId,nombre, apellido, fotoPerfil, precioBase, modeloVehiculo, marcaVehiculo, patenteVehiculo, promedioDePuntaje) {

    @Transient
    private var tarifaMotoCorto = 500.0
    @Transient
    private var tarifaMotoLargo = 600.0
    @Transient
    private var umbralMoto = 30

    override fun costoPlus(viaje:Viaje): Double {
        return if(viaje.duracion < umbralMoto ) viaje.duracion * tarifaMotoCorto else viaje.duracion * tarifaMotoLargo
    }
}

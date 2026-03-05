package ar.edu.unsam.phm.domain

import java.time.LocalDateTime
import ar.edu.unsam.phm.extras.*
import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import kotlin.jvm.Transient

@Entity
class Viaje{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ManyToOne(cascade = [(CascadeType.MERGE)])
    @JoinColumn(name = "id_pasajero", referencedColumnName = "id")
    lateinit var pasajero: Pasajero
    @ManyToOne (cascade = [CascadeType.MERGE])
    @JoinColumn(name = "id_chofer_de_viaje", referencedColumnName = "id")
    lateinit var choferDeViaje: ChoferDeViaje
    @Min(1) @Max(120)
    var duracion = 0
    @Column(length=100)
    var origen = ""
    @Column(length=100)
    var destino = ""
    @Min(1)
    var cantidadPasajeros = 0
    var estaCalificado: Boolean = false
    lateinit var fechaInicio:LocalDateTime
    lateinit var fechaFinalizacion: LocalDateTime
    @Min(1)
    var precioPasajero = 0.0
    @Min(1)
    var precioChofer = 0.0
    @Transient
    lateinit var  choferMongo: Chofer

    fun asignarPrecio() {
        precioChofer = choferMongo.costoBase(this)
        precioPasajero = choferMongo.costoComision(this)
    }

    fun seCalifica() { this.estaCalificado = true }

    fun seEliminaCalificacion() { this.estaCalificado = false }

    fun asignarChofer(chofer: Chofer, nuevoChoferDeViaje: ChoferDeViaje) {
        this.choferMongo = chofer
        this.choferDeViaje = nuevoChoferDeViaje
    }

    fun asignarPasajero(pasajero: Pasajero) { this.pasajero = pasajero }

    fun asignarFechaFinalizacion() { this.fechaFinalizacion = Formateador().fechaFinalizar(this.fechaInicio,this.duracion)  }

}
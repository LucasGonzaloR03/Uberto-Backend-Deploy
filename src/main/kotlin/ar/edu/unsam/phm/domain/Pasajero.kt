package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.errorHandling.BusinessException
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.PositiveOrZero


@Entity
class Pasajero  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToOne(cascade = [(CascadeType.MERGE)]) @JoinColumn(name = "id_user_data", unique = true)
    var userData: UserData? = null

    @Column(length=100)
    var nombre = ""

    @Column(length=100)
    var apellido = ""

    @Column(nullable = false)
    @PositiveOrZero(message = "El saldo no puede ser negativo.")
    var saldo: Double = 0.0

    @Min(1) @Max(120)
    var edad: Int = 0

    @Column(length=50)
    var telefono: String = ""

    fun validadEntidad(){
        require(nombre.isNotBlank() && apellido.isNotBlank()){
            throw BusinessException("El nombre y apellido del pasajero no puede estar vacio")
        }
        require(telefono.isNotBlank()){
            throw BusinessException("El numero de telefono no puede estar vacio")
        }
    }

    fun consultarSaldo(): Double = this.saldo

    fun saldoDiponible(precio: Double): Boolean = saldo > precio


}
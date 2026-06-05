package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.errorHandling.BusinessException
import jakarta.persistence.*

@Entity
class UserData{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id :Long? = null
    var username : String = ""
    var password : String = ""
    @Enumerated(EnumType.STRING)
    var tipoUsuario: TipoUsuario? = null
    @Column
    var fotoPerfil = ""

    fun validadEntidad(){
        require(username.isNotBlank()){
            throw BusinessException("El nombre de usuario no puede estar vacio")
        }
        require(password.isNotBlank()){
            throw BusinessException("La contraseña no puede estar vacia")
        }
    }
}

enum class TipoUsuario(val tipoUsuarioStr: String) {
    CHOFER("CHOFER"),
    PASAJERO("PASAJERO")
}

fun convertirStringATipoUsuario(tipoUsuarioStr:String):TipoUsuario{
    val tipoUsuarioMap = mapOf(
        "CHOFER" to TipoUsuario.CHOFER,
        "PASAJERO" to TipoUsuario.PASAJERO,
    )

    return tipoUsuarioMap[tipoUsuarioStr.uppercase()]?:TipoUsuario.PASAJERO
}
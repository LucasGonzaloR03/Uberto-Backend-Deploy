package ar.edu.unsam.phm.domain

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
}

enum class TipoUsuario(val tipoUsuarioStr: String) {
    CHOFER("CHOFER"),
    PASAJERO("PASAJERO")
}
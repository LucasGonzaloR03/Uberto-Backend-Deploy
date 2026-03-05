package ar.edu.unsam.phm.dto

data class UsuarioLoginDTO (val username: String, val password: String)

data class UsuarioLogeadoDTO (val userLogedID: Long?, val tipoUsuario: String,val fotoPerfil: String, )

data class UsuarioDataDTO (val userLogedID: Long?, val tipoUsuario: String,val fotoPerfil: String, val tokenAcceso:String, val tokenRefresco:String)

data class RefreshTokenRequestDTO(val refreshToken: String)
package ar.edu.unsam.phm.service
import ar.edu.unsam.phm.configurationSecurity.UtilidadJwt
import ar.edu.unsam.phm.dto.*
import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.repository.*
import ar.edu.unsam.phm.errorHandling.*
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@Transactional
class UserDataService(
    private val userDataRepository: UserDataRepository,
    private val passwordEncoder: PasswordEncoder,
    private val utilidadJwt: UtilidadJwt,
    private val refreshTokenService: RefreshTokenService
){

    fun login(username: String, password :String): UsuarioDataDTO {
        val userData: UserData = userDataRepository.findByUsername(username).orElseThrow{CredencialesInvalidasException()}

        if (!passwordEncoder.matches(password, userData.password)){
            throw CredencialesInvalidasException()
        }

        val tokenDeAcceso:String = utilidadJwt.generarTokenAcceso(userData.username, userData.tipoUsuario!!.tipoUsuarioStr)
        val tokenDeRefresco: RefreshToken = refreshTokenService.createRefreshToken(userData.username)

        return UsuarioDataDTO(userData.id, userData.tipoUsuario!!.tipoUsuarioStr, userData.fotoPerfil, tokenDeAcceso, tokenDeRefresco.token)
    }

    fun refreshToken(refreshTokenRequest: String): UsuarioDataDTO {
        val refreshToken = refreshTokenService.findByToken(refreshTokenRequest)
            ?: throw TokenRefreshException("Refresh token no encontrado")

        refreshTokenService.verifyExpiration(refreshToken)

        val userData: UserData = userDataRepository.findByUsername(refreshToken.username).orElseThrow{CredencialesInvalidasException()}

        val nuevoTokenDeAcceso = utilidadJwt.generarTokenAcceso(userData.username, userData.tipoUsuario!!.tipoUsuarioStr)
        val nuevoTokenDeRefresco:RefreshToken = refreshTokenService.createRefreshToken(userData.username)

        return UsuarioDataDTO(userData.id, userData.tipoUsuario!!.tipoUsuarioStr, userData.fotoPerfil, nuevoTokenDeAcceso, nuevoTokenDeRefresco.token)
    }
    fun save(userDataChofer: UserData){
        userDataRepository.save(userDataChofer)
    }

    fun findById(id: Long): UserData {
        return this.userDataRepository.findById(id).orElseThrow {
            throw NotFoundException("No se encontró el chofer con userDataId indicado: $id")
        }
    }

}
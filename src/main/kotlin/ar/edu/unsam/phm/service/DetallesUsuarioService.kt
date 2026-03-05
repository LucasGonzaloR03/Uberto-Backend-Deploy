package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.UserData
import ar.edu.unsam.phm.errorHandling.CredencialesInvalidasException
import ar.edu.unsam.phm.repository.UserDataRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class DetallesUsuarioService(
    private val userDataRepository: UserDataRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val usuarioEncontrado: UserData = userDataRepository.findByUsername(username).orElseThrow { CredencialesInvalidasException() }
        return usuarioEncontrado.mapToUserDetails()
    }

    private fun UserData.mapToUserDetails(): UserDetails =
        User.builder()
            .username(this.username)
            .password(this.password)
            .roles(this.tipoUsuario!!.name)
            .build()
}

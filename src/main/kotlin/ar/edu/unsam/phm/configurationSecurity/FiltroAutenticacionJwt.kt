package ar.edu.unsam.phm.configurationSecurity

import ar.edu.unsam.phm.errorHandling.TokenExpiradoException
import ar.edu.unsam.phm.service.DetallesUsuarioService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class FiltroAutenticacionJwt: OncePerRequestFilter(){
    @Autowired
    lateinit var  utilidadJwt: UtilidadJwt
    @Autowired
    lateinit var detallesUsuarioService: DetallesUsuarioService


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try{
            val authHeader : String?  = request.getHeader("Authorization")
            if (authHeader.doesNotContainBearerToken()) {
                filterChain.doFilter(request, response)
                return
            }
            val jwtToken = authHeader!!.extractTokenValue()
            val username = utilidadJwt.obtenerNombreUsuarioDesdeToken(jwtToken)
            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val usuarioEncontrado = detallesUsuarioService.loadUserByUsername(username)
                if (utilidadJwt.validarToken(jwtToken, usuarioEncontrado)) {
                    updateContext(usuarioEncontrado, request)
                    filterChain.doFilter(request, response)
                }
            }
        }catch(e:TokenExpiradoException){
            response.sendError(HttpStatus.UNAUTHORIZED.value(), e.message)
        }
    }

    private fun String?.doesNotContainBearerToken() =
        this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue() =
        this.substringAfter("Bearer ")

    private fun updateContext(usuarioEncontrado: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(usuarioEncontrado, null, usuarioEncontrado.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }
}
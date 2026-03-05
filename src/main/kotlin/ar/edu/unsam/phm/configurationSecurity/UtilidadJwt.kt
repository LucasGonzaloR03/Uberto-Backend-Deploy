package ar.edu.unsam.phm.configurationSecurity

import ar.edu.unsam.phm.errorHandling.TokenExpiradoException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.Date

@Component
class UtilidadJwt (
    propiedadesJwt: PropiedadesJwt
) {
    private val clave = Keys.hmacShaKeyFor(propiedadesJwt.key.toByteArray())
    private val duracionTokenAcceso: Long = propiedadesJwt.accessTokenExpiration
    private val duracionTokenRefresco: Long = propiedadesJwt.refreshTokenExpiration

    fun generarTokenAcceso(nombreUsuario: String, tipoUsuario: String): String {
        return Jwts.builder()
            .subject(nombreUsuario)
            .claim("tipoUsuario", tipoUsuario)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + duracionTokenAcceso))
            .signWith(clave)
            .compact()
    }

    fun generarTokenRefresco(username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + duracionTokenRefresco))
            .signWith(clave)
            .compact()
    }

    fun obtenerNombreUsuarioDesdeToken(token: String): String? {
        try{
            return getAllClaims(token).subject
        }catch (expiredJwtException: ExpiredJwtException){
            throw TokenExpiradoException("Sesión vencida")
        }
    }

    private fun getAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(clave)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun validarToken(token: String, userDetails: UserDetails): Boolean {
        val username = obtenerNombreUsuarioDesdeToken(token)
        return username == userDetails.username && !esTokenExpirado(token)
    }

    fun esTokenExpirado(token: String): Boolean =
        getAllClaims(token).expiration.before(Date(System.currentTimeMillis()))
}
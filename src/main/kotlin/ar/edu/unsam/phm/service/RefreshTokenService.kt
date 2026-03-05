package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.configurationSecurity.PropiedadesJwt
import ar.edu.unsam.phm.configurationSecurity.UtilidadJwt
import ar.edu.unsam.phm.domain.RefreshToken
import ar.edu.unsam.phm.repository.RefreshTokenRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*


@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val utilidadJwt: UtilidadJwt,
    private val propiedadesJwt: PropiedadesJwt
) {


    private val duracionTokenRefresco: Long = propiedadesJwt.refreshTokenExpiration

    fun findByToken(token: String): RefreshToken? {
        return refreshTokenRepository.findByToken(token)
    }

    @Transactional
    fun createRefreshToken(username: String): RefreshToken {
        this.deleteByUsername(username)

        val refreshToken = RefreshToken(
            token = utilidadJwt.generarTokenRefresco(username),
            username = username,
            expiryDate = Date(System.currentTimeMillis() + duracionTokenRefresco)
        )
        refreshTokenRepository.save(refreshToken)
        return refreshToken
    }

    @Transactional
    fun verifyExpiration(token: RefreshToken): RefreshToken {
        if (token.expiryDate.before(Date(System.currentTimeMillis()))) {
            refreshTokenRepository.delete(token)
            throw TokenRefreshException("El refresh token ha expirado")
        }

        return token
    }

    @Transactional
    fun deleteByUsername(username: String) {
        refreshTokenRepository.deleteByUsername(username)
    }
}

class TokenRefreshException(message: String) : RuntimeException(message)


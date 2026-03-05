package ar.edu.unsam.phm.configurationSecurity

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt")
data class PropiedadesJwt(
    val key: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long,
)
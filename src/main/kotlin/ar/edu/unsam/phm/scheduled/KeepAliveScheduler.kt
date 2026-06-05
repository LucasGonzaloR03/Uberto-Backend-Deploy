package ar.edu.unsam.phm.scheduled

import ar.edu.unsam.phm.repository.PasajeroRepository
import ar.edu.unsam.phm.repository.ChoferRepository
import ar.edu.unsam.phm.neo4jRepository.ChoferDeRelacionDeViajeRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class KeepAliveScheduler {

    private val logger = LoggerFactory.getLogger(KeepAliveScheduler::class.java)

    @Autowired
    private lateinit var pasajeroRepository: PasajeroRepository

    @Autowired
    private lateinit var choferRepository: ChoferRepository

    @Autowired
    private lateinit var choferRelacionDeViajeRepository: ChoferDeRelacionDeViajeRepository

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    private val restTemplate = RestTemplate()

    // Cada 10 minutos — mantiene las DBs activas
    @Scheduled(fixedRate = 10 * 60 * 1000)
    fun pingBaseDeDatos() {
        try {
            pasajeroRepository.count()
            logger.info("[KeepAlive] PostgreSQL OK")
        } catch (e: Exception) {
            logger.warn("[KeepAlive] PostgreSQL ERROR: ${e.message}")
        }

        try {
            choferRepository.count()
            logger.info("[KeepAlive] MongoDB OK")
        } catch (e: Exception) {
            logger.warn("[KeepAlive] MongoDB ERROR: ${e.message}")
        }

        try {
            choferRelacionDeViajeRepository.count()
            logger.info("[KeepAlive] Neo4j OK")
        } catch (e: Exception) {
            logger.warn("[KeepAlive] Neo4j ERROR: ${e.message}")
        }

        try {
            redisTemplate.connectionFactory?.connection?.ping()
            logger.info("[KeepAlive] Redis OK")
        } catch (e: Exception) {
            logger.warn("[KeepAlive] Redis ERROR: ${e.message}")
        }
    }
}

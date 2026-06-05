package ar.edu.unsam.phm.scheduled

import ar.edu.unsam.phm.repository.ChoferRepository
import ar.edu.unsam.phm.repository.UserDataRepository
import ar.edu.unsam.phm.neo4jRepository.ChoferDeRelacionDeViajeRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DatabaseKeepAliveScheduler {

    private val logger = LoggerFactory.getLogger(DatabaseKeepAliveScheduler::class.java)

    @Autowired
    lateinit var userDataRepository: UserDataRepository

    @Autowired
    lateinit var choferRepository: ChoferRepository

    @Autowired
    lateinit var choferRelacionDeViajeRepository: ChoferDeRelacionDeViajeRepository

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, String>

    // Se ejecuta cada 5 minutos
    @Scheduled(fixedRateString = "PT5M")
    fun keepAlive() {
        pingPostgres()
        pingMongo()
        pingNeo4j()
        pingRedis()
    }

    private fun pingPostgres() {
        try {
            userDataRepository.count()
            logger.info("[KeepAlive] PostgreSQL OK")
        } catch (e: Exception) {
            logger.warn("[KeepAlive] PostgreSQL no respondió: ${e.message}")
        }
    }

    private fun pingMongo() {
        try {
            choferRepository.count()
            logger.info("[KeepAlive] MongoDB OK")
        } catch (e: Exception) {
            logger.warn("[KeepAlive] MongoDB no respondió: ${e.message}")
        }
    }

    private fun pingNeo4j() {
        try {
            choferRelacionDeViajeRepository.count()
            logger.info("[KeepAlive] Neo4j OK")
        } catch (e: Exception) {
            logger.warn("[KeepAlive] Neo4j no respondió: ${e.message}")
        }
    }

    private fun pingRedis() {
        try {
            redisTemplate.opsForValue().set("keep-alive", "ok")
            logger.info("[KeepAlive] Redis OK")
        } catch (e: Exception) {
            logger.warn("[KeepAlive] Redis no respondió: ${e.message}")
        }
    }
}

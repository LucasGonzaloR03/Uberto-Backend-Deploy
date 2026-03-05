package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.ChoferDeViaje
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ChoferDeViajeRepository: JpaRepository<ChoferDeViaje, String> {
    override fun findById(id: String): Optional<ChoferDeViaje>
}
package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.UltimaBusquedaDeUnViaje
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UltimaBusquedaDeUnViajeRepository: CrudRepository<UltimaBusquedaDeUnViaje, String> {
}
package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.UserData

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserDataRepository : CrudRepository<UserData, Long> {
    fun findByUsername(username: String): Optional<UserData>
}
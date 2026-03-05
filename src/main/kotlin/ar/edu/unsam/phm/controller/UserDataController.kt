package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Chofer
import ar.edu.unsam.phm.domain.UserData
import ar.edu.unsam.phm.dto.RefreshTokenRequestDTO
import ar.edu.unsam.phm.dto.UsuarioDataDTO
import ar.edu.unsam.phm.dto.UsuarioLogeadoDTO
import ar.edu.unsam.phm.dto.UsuarioLoginDTO
import ar.edu.unsam.phm.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.Optional

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["*"])

class UserDataController {

    @Autowired
    lateinit var userDataService: UserDataService

    @PostMapping("/login")
    fun loginUser(@RequestBody dataUser: UsuarioLoginDTO): UsuarioDataDTO {
        val username = dataUser.username
        val password = dataUser.password

        return userDataService.login(username,password )
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequestDTO): UsuarioDataDTO {
        return userDataService.refreshToken(request.refreshToken)
    }
}
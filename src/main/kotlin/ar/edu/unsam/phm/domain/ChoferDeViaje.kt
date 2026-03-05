package ar.edu.unsam.phm.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id


@Entity
data class ChoferDeViaje(
    @Id
    val id: String,
    val nombreChofer: String = "",
    val apellidoChofer: String = "",
    val fotoPerfil: String = ""
)

fun Chofer.toChoferDeViaje() = ChoferDeViaje(this.id,this.nombre,this.apellido, this.fotoPerfil)
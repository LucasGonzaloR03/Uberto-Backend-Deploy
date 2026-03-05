package ar.edu.unsam.phm.domain

import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank


@Entity
class Calificacion{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long?=null
    @OneToOne(cascade = [(CascadeType.MERGE)])
    var viaje : Viaje? = null
    @Min(1) @Max(5)
    var puntaje = 0
    @NotBlank(message = "El comentario no puede estar vacío")
    @Column(length=200)
    var comentario = ""
}
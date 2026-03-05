package ar.edu.unsam.phm.domain

import org.springframework.data.neo4j.core.schema.Property
import org.springframework.data.neo4j.core.schema.RelationshipId
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode
import java.time.LocalDateTime

@RelationshipProperties
class RelacionDeViaje {

    @RelationshipId
    lateinit var id: String

    @Property("idViaje")
    var idViaje: Long = 0

    @Property("fechaInicio")
    var fechaInicio: LocalDateTime = LocalDateTime.now()

    @Property( "fechaFinalizacion")
    var fechaFinalizacion: LocalDateTime = LocalDateTime.now()

    @TargetNode
    lateinit var chofer: ChoferDeRelacionDeViaje

}

fun Viaje.toRelacionDeViaje()= RelacionDeViaje().apply {
    this.idViaje=this@toRelacionDeViaje.id!!
    this.fechaInicio=this@toRelacionDeViaje.fechaInicio
    this.fechaFinalizacion=this@toRelacionDeViaje.fechaFinalizacion
}
package ar.edu.unsam.phm.domain

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property

@Node("ChoferDeRelacionDeViaje")
class ChoferDeRelacionDeViaje (
    @Property("idChofer")
    var idChofer: String = "",
    @Property("nombre")
    var nombre: String = "",
    @Property("apellido")
    var apellido: String = ""
) {
    @Id
    @GeneratedValue
    lateinit var id: String
}
fun ChoferDeViaje.toChoferDeRelacionDeViaje()=ChoferDeRelacionDeViaje(this.id,this.nombreChofer,this.apellidoChofer)
package ar.edu.unsam.phm.domain
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property
import org.springframework.data.neo4j.core.schema.Relationship

@Node("PasajeroAmigo")
class PasajeroAmigo(

    @Property("idPasajero")
    var idPasajero: Long,

    @Property("nombre")
    var nombre: String,

    @Property("apellido")
    var apellido: String,

    @Property("fotoDePerfil")
    var fotoPerfil: String

) {
    @Id @GeneratedValue
    lateinit var id: String

    @Relationship(type = "AMIGO_DE", direction = Relationship.Direction.OUTGOING)
    var listaPasajeroAmigos: MutableSet<PasajeroAmigo> = mutableSetOf()

    @Relationship(type = "VIAJO_CON", direction = Relationship.Direction.OUTGOING)
    var viajes: MutableSet<RelacionDeViaje> = mutableSetOf()

    fun agregarUnAmigoParaRelacion(amigo: PasajeroAmigo){
        listaPasajeroAmigos.add(amigo)
    }

    fun agregarUnViajeParaRelacion(viaje: RelacionDeViaje){
        viajes.add(viaje)
    }


}

fun Pasajero.toPasajeroAmigo(): PasajeroAmigo = PasajeroAmigo(this.id!!, this.nombre, this.apellido, this.userData!!.fotoPerfil)
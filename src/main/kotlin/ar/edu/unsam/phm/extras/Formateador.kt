package ar.edu.unsam.phm.extras
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Formateador() {

    fun formatoLocalDateTime(fechaISO: String): LocalDateTime {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        // Parseamos la fecha asumiendo que está en UTC
        val utcDateTime = LocalDateTime.parse(fechaISO, formatter)
        // Obtenemos la zona horaria UTC
        val utcZone = ZoneId.of("UTC")
        // Creamos un ZonedDateTime en UTC
        val zonedDateTimeUTC = ZonedDateTime.of(utcDateTime, utcZone)
        // Obtenemos la zona horaria por defecto del sistema
        val deviceZone = ZoneId.systemDefault()
        // Convertimos el ZonedDateTime a la zona horaria del dispositivo
        val zonedDateTimeDevice = zonedDateTimeUTC.withZoneSameInstant(deviceZone)
        // Finalmente, obtenemos el LocalDateTime en la zona horaria del dispositivo
        return zonedDateTimeDevice.toLocalDateTime()
    }

    fun fechaFinalizar(fechaInicio: LocalDateTime, duracion: Int): LocalDateTime {
        return fechaInicio.plusMinutes(duracion.toLong())
    }

}

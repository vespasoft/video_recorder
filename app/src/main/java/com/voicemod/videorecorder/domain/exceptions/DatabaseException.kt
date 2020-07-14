package com.voicemod.videorecorder.domain.exceptions

/**
 * Exception throw by the application when there is a database exception.
 */
class DatabaseException(message: String? = null) : Exception(message) {

    val defaultMessage: String
        get() = "Se ha producido un error en el acceso a base de datos. " + "Si el error persiste, contacte con el administrador de la aplicación."

}

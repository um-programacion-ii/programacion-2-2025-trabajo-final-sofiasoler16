package com.um.eventosmovil

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
package com.um.eventosmovil.service

import com.um.eventosmovil.data.EventoDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class EventoService(private val token: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val BASE_URL = "http://10.0.2.2:8080/api/app"

    suspend fun getEventos(): Result<List<EventoDTO>> {
        return try {
            val response = client.get("$BASE_URL/eventos") {
                // Enviamos el token en la cabecera
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Error al cargar eventos: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
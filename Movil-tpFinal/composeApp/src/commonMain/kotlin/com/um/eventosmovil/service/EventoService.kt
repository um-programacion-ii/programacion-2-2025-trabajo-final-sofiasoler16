package com.um.eventosmovil.service

import com.um.eventosmovil.data.EventoDTO
import com.um.eventosmovil.data.EventoDetalleDTO
import com.um.eventosmovil.data.MapaAsientosResponse
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
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val BASE_URL = "http://10.0.2.2:8080/api/app/eventos"

    suspend fun getEventos(): Result<List<EventoDTO>> {
        return try {
            val response = client.get(BASE_URL) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventoDetalle(id: Long): Result<EventoDetalleDTO> {
        return try {
            val response = client.get("$BASE_URL/$id") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMapaAsientos(eventId: Long, filas: Int, columnas: Int): Result<MapaAsientosResponse> {
        return try {
            val response = client.get("http://10.0.2.2:8080/api/app/eventos/$eventId/asientos") {
                parameter("filas", filas)
                parameter("columnas", columnas)
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
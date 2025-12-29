package com.um.eventosmovil.service

import com.um.eventosmovil.data.AsientoDTO
import com.um.eventosmovil.data.AsientoPosicion
import com.um.eventosmovil.data.BloqueoRequest
import com.um.eventosmovil.data.EventoDTO
import com.um.eventosmovil.data.EventoDetalleDTO
import com.um.eventosmovil.data.MapaAsientosResponse
import com.um.eventosmovil.data.SesionCompraDTO
import com.um.eventosmovil.data.VentaDTO
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

    suspend fun bloquearAsientos(eventId: Long, asientos: List<AsientoPosicion>): Result<Unit> {
        return try {
            val response = client.post("http://10.0.2.2:8080/api/app/bloquear") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)

                setBody(BloqueoRequest(eventId, asientos))
            }

            if (response.status == HttpStatusCode.OK) {
                Result.success(Unit)
            } else {
                val errorMsg = response.headers["X-error-message"] ?: "Error desconocido (${response.status})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun realizarVenta(asientos: List<AsientoDTO>): Result<Unit> {
        return try {
            val response = client.post("http://10.0.2.2:8080/api/app/venta") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(asientos)
            }
            if (response.status == HttpStatusCode.OK) Result.success(Unit)
            else Result.failure(Exception("Error en la venta"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getMisVentas(): Result<List<VentaDTO>> {
        return try {
            val response = client.get("http://10.0.2.2:8080/api/app/mis-ventas") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Error al obtener ventas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recuperarSesion(): Result<SesionCompraDTO?> {
        return try {
            val response = client.get("http://10.0.2.2:8080/api/app/sesion/recuperar") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else if (response.status == HttpStatusCode.NoContent) {
                Result.success(null) // No hay sesión previa
            } else {
                Result.failure(Exception("Error al recuperar sesión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.um.eventosmovil.service

import com.um.eventosmovil.data.LoginRequest
import com.um.eventosmovil.data.LoginResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AuthService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val BASE_URL = "http://10.0.2.2:8080/api"

    suspend fun authenticate(request: LoginRequest): Result<String> {
        return try {
            val response = client.post("$BASE_URL/authenticate") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status == HttpStatusCode.OK) {
                val body: LoginResponse = response.body()
                Result.success(body.idToken)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
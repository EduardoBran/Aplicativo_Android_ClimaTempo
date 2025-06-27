package com.luizeduardobrandao.climatempo.network

import com.luizeduardobrandao.climatempo.model.Location
import retrofit2.http.GET
import retrofit2.http.Query

// * Interface Retrofit para a Geocoding API.
// * Permite buscar até [limit] localidades que correspondem ao nome fornecido.

interface GeocodingApiService {
    @GET("geo/1.0/direct")
    suspend fun getLocations(
        @Query("q") city: String,         // Nome da cidade (pode incluir estado)
        @Query("limit") limit: Int = 5,   // Número máximo de resultados
        @Query("appid") apiKey: String    // Chave de acesso à API
    ): List<Location>
}

// Chamada quando uma busca de clima retorna 404, para descobrir latitude/longitude
// ou distinguir cidades com nomes iguais.
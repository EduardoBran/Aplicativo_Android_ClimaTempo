package com.luizeduardobrandao.climatempo.network

import com.luizeduardobrandao.climatempo.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// * Interface Retrofit para a API Current Weather.
// * Retorna um objeto [Response] contendo [WeatherResponse] ou código de erro.

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") cityQuery: String,                // Consulta no formato "Cidade,UF"
        @Query("appid") apiKey: String,               // Chave de API OpenWeatherMap
        @Query("units") units: String = "metric",     // Unidade: Celsius
        @Query("lang") lang: String = "pt_br"         // Idioma: português (Brasil)
    ): Response<WeatherResponse>
}

// suspend fun marca uma função que pode ser suspensa e executada dentro de uma coroutine
// sem bloquear a thread principal.


// Chamada sempre que o usuário clica em “Pesquisar” para obter os dados de
// clima (temperatura e descrição).
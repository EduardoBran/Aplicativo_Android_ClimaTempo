package com.luizeduardobrandao.climatempo.repository

import com.luizeduardobrandao.climatempo.helper.ConstantApiKey
import com.luizeduardobrandao.climatempo.model.Location
import com.luizeduardobrandao.climatempo.model.WeatherResponse
import com.luizeduardobrandao.climatempo.network.GeocodingApiService
import com.luizeduardobrandao.climatempo.network.WeatherApiService
import retrofit2.HttpException
import retrofit2.Response

// * Sealed class que representa todos os estados possíveis da requisição de clima.

sealed class WeatherResult {

    // Carregando
    object Loading: WeatherResult()
    // Dados
    data class Success(val data: WeatherResponse): WeatherResult()
    // Cidade não encontrada
    object CityNotFound: WeatherResult()
    // Múltiplas opções
    data class MultipleLocations(val options: List<Location>): WeatherResult()
    // Erro genérico
    data class Error(val throwable: Throwable): WeatherResult()
}
// Uma sealed class define uma hierarquia de classes fechada: só quem está no mesmo
// arquivo-fonte pode declarar subclasses.


// * Repositório responsável por chamar as APIs de clima e geocoding, e por aplicar a
//   lógica de tratamento de erro e múltiplas cidades.
// * A chave de API é obtida de Constants.OPEN_WEATHER_API_KEY.
class WeatherRepository(
    private val weatherApi: WeatherApiService,
    private val geocodingApi: GeocodingApiService
) {

    // * Busca o clima para a cidade fornecida.
    // * Em caso de 404, tenta Geocoding para sugerir cidades.
    suspend fun fetchWeather(city: String): WeatherResult {
        return try{
            val response: Response<WeatherResponse> = weatherApi.getCurrentWeather(
                cityQuery = city,
                apiKey = ConstantApiKey.OPEN_WEATHER_API_KEY
            )

            when {
                response.isSuccessful -> {
                    response.body()?.let {
                        WeatherResult.Success(it)
                    } ?: WeatherResult.Error(IllegalStateException("Resposta vazia"))
                }
                response.code() == 404 -> {
                    val locations = geocodingApi.getLocations(
                        city = city,
                        apiKey = ConstantApiKey.OPEN_WEATHER_API_KEY
                    )
                    if (locations.size > 1) WeatherResult.MultipleLocations(locations)
                    else WeatherResult.CityNotFound
                }
                else -> WeatherResult.Error(HttpException(response))
            }
        }
        catch (t: Throwable){
            WeatherResult.Error(t)
        }
    }
}

package com.luizeduardobrandao.climatempo.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.luizeduardobrandao.climatempo.network.WeatherApiService
import com.luizeduardobrandao.climatempo.network.GeocodingApiService
import com.luizeduardobrandao.climatempo.repository.WeatherRepository


// * Factory para criar MainViewModel com WeatherRepository.
// * Se não utilizar um framework de DI, implementa manualmente a criação das dependências.

// ***** POR QUE USAR UM FACTORY? *****
// O Android só sabe instanciar ViewModels sem parâmetros.
// Como nosso MainViewModel recebe o WeatherRepository no construtor, precisamos de um
// ViewModelProvider.Factory para criar corretamente o ViewModel com essa dependência.
// Sem isso, o delegate by viewModels() não conseguiria chamar um construtor customizado.

// * Instancia internamente o Retrofit em singleton, sem uso de objeto externo.

object MainViewModelFactory : ViewModelProvider.Factory {

    // Cria única instância de Retrofit configurada
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Serviços Retrofit reutilizados
    private val weatherApi: WeatherApiService = retrofit.create(WeatherApiService::class.java)
    private val geocodingApi: GeocodingApiService = retrofit.create(GeocodingApiService::class.java)

    // Repositório único criado com os serviços
    private val repository: WeatherRepository = WeatherRepository(weatherApi, geocodingApi)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

// Retrofit
//  - É uma biblioteca de cliente HTTP para Android e Java que simplifica chamadas a APIs REST.
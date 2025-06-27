package com.luizeduardobrandao.climatempo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luizeduardobrandao.climatempo.model.Location
import com.luizeduardobrandao.climatempo.repository.WeatherRepository
import com.luizeduardobrandao.climatempo.repository.WeatherResult
import kotlinx.coroutines.launch

// ViewModel da tela principal.

// * Responsável por:
// *  - Manter o estado da busca de clima (Loading, Success, CityNotFound, MultipleLocations, Error)
// *  - Iniciar chamadas ao repositório sem bloquear a UI
// *  - Expor um LiveData para a Activity observar e reagir às mudanças

class MainViewModel(
    private val repository: WeatherRepository // injetado pelo MainViewModelFactory
): ViewModel() {

    // LiveData interna mutável que guarda o estado atual da busca
    private val _weatherState = MutableLiveData<WeatherResult>()

    // * LiveData pública para a Activity observar:
    //     * - WeatherResult.Loading        → mostrar ProgressBar
    //     * - WeatherResult.Success(data)  → exibir dados de clima
    //     * - WeatherResult.CityNotFound   → mostrar mensagem de cidade não encontrada
    //     * - WeatherResult.MultipleLocations(list) → mostrar diálogo de seleção
    //     * - WeatherResult.Error(error)   → mostrar mensagem de erro genérico
    val weatherState: LiveData<WeatherResult> = _weatherState


    // * Inicia a busca de clima para a [city] fornecida.
    //
    //     * 1. Emite Loading
    //     * 2. Lança coroutine em viewModelScope
    //     * 3. Chama repository.fetchWeather(city)
    //     * 4. Publica o resultado no LiveData

    fun fetchWeather(city: String) {

        // 1. Estado de carregamento
        _weatherState.value = WeatherResult.Loading

        // 2. Coroutine para não travar a UI
        viewModelScope.launch {
            // 3. Executa a busca no repositório
            val result = repository.fetchWeather(city)
            // 4. Publica resultado (Success, CityNotFound, etc.)
            _weatherState.value = result
        }
    }

    // * Tratamento para quando houver múltiplas localidades retornadas
    //   pela Geocoding API.
    //     * Constrói a query no formato "Cidade,UF" ou "Cidade,PAÍS" e
    //     * reexecuta a busca de clima.

    fun onLocationSelected(location: Location){

        // UF se disponível, caso contrário país
        val suffix = location.state ?: location.country
        val query = "${location.name}, $suffix"
        fetchWeather(query)
    }
}
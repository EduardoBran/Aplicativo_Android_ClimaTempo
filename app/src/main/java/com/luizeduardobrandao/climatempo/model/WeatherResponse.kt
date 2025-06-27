package com.luizeduardobrandao.climatempo.model

//Data class que representa a resposta da API de clima (/data/2.5/weather).

//* @property name    Nome da cidade retornada pela API.
//* @property sys     Objeto interno que contém o código do país.
//* @property main    Objeto interno que contém informações principais, como temperatura.
//* @property weather Lista de condições climáticas; geralmente usamos o primeiro item.

data class WeatherResponse (
    val name: String,
    val sys: Sys,
    val main: Main,
    val weather: List<Weather>
) {

    // * Representa o nó "sys" do JSON, contendo o país.
    // * @property country Código do país (ex: "BR").
    data class Sys (
        val country: String
    )

    // * Representa o nó "main" do JSON, contendo detalhes de temperatura.
    // * @property temp Temperatura atual em Celsius.
    data class Main(
        val temp: Double
    )

    // * Representa um elemento do array "weather" do JSON.
    // * @property description Descrição textual do clima (ex: "céu limpo").
    data class Weather(
        val description: String,
        val icon: String
    )
}

// Vem da Current Weather API (/data/2.5/weather), depois que a cidade é confirmada.
// - Representa a resposta completa de clima, incluindo name (nome da cidade), sys.country (país),
//   main.temp (temperatura) e weather[0].description (descrição do tempo).
// - É o modelo final cujos dados você exibe na interface: cidade + país, temperatura e descrição.
package com.luizeduardobrandao.climatempo.model

// * Data class para mapear cada resultado da Geocoding API (/geo/1.0/direct).

// * @property name    Nome da localidade (cidade).
// * @property state   Sigla do estado, pode ser null se não informado.
// * @property country Código do país (ex: "BR").
// * @property lat     Latitude.
// * @property lon     Longitude.

data class Location (
    val name: String,
    val state: String?,
    val country: String,
    val lat: Double,
    val lon: Double
)

// Vem da Geocoding API (/geo/1.0/direct).
// - É usado quando a busca de clima retorna 404 (“cidade não encontrada”) e você precisa
//   sugerir ao usuário quais cidades existem com aquele nome.
// - Contém atributos name, state, country, lat, lon, que permitem apresentar uma lista de
//   opções (e até refazer a consulta como “Cidade,UF”).
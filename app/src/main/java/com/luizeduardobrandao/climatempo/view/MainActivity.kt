package com.luizeduardobrandao.climatempo.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luizeduardobrandao.climatempo.R
import com.luizeduardobrandao.climatempo.databinding.ActivityMainBinding
import com.luizeduardobrandao.climatempo.helper.BannerAds
import com.luizeduardobrandao.climatempo.repository.WeatherResult
import com.luizeduardobrandao.climatempo.viewmodel.MainViewModel
import com.luizeduardobrandao.climatempo.viewmodel.MainViewModelFactory
import com.luizeduardobrandao.climatempo.model.Location
import com.bumptech.glide.Glide

// * Activity principal: configura binding, listeners e observers do ViewModel.

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Obtém o MainViewModel usando o MainViewModelFactory singleton
    // para injetar WeatherRepository no construtor do ViewModel
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permite que o layout ocupe toda a tela, incluindo áreas do sistema
        enableEdgeToEdge()

        // Infla o layout e configura o root view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajusta padding para compensar barras de status e navegação
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configura observadores de LiveData/StateFlow do ViewModel
        setObservers()

        // Configura listeners de UI (botões, campos de texto etc.)
        setListeners()

        // carrega o banner no container da view binding
        BannerAds.loadBanner(this, binding.frameBanner)
    }

    // Observa o estado da busca de clima exposto pelo ViewModel
    private fun setObservers(){
        viewModel.weatherState.observe(this) { state ->
            // Antes de processar novo estado, oculta todos os elementos de resultado
            binding.progressBar.visibility = View.GONE
            binding.tvCityName.visibility = View.GONE
            binding.tvCountry.visibility = View.GONE
            binding.tvTemperature.visibility = View.GONE
            binding.tvDescription.visibility = View.GONE

            when(state) {

                // Exibe o ProgressBar enquanto a requisição está em progresso
                is WeatherResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                // Quando recebe dados com sucesso, popula e exibe os campos
                is WeatherResult.Success -> {
                    val data = state.data
                    binding.tvCityName.text = data.name
                    // nome do país
                    val locale = java.util.Locale("", data.sys.country)
                    binding.tvCountry.text = locale.displayCountry
                    binding.tvTemperature.text = "${data.main.temp} °C"
                    binding.tvDescription.text = data.weather.firstOrNull()?.description.orEmpty()
                    // Carrega o ícone do tempo via Glide
                    data.weather.firstOrNull()?.icon?.let { iconCode ->
                        val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                        Glide.with(this)
                            .load(iconUrl)
                            .into(binding.ivWeatherIcon)
                        binding.ivWeatherIcon.visibility = View.VISIBLE
                    }
                    binding.tvCityName.visibility = View.VISIBLE
                    binding.tvCountry.visibility = View.VISIBLE
                    binding.tvTemperature.visibility = View.VISIBLE
                    binding.tvDescription.visibility = View.VISIBLE
                }

                // Se a cidade não foi encontrada, mostra toast de erro específico
                is WeatherResult.CityNotFound -> {
                    Toast.makeText(
                        this, getString(R.string.error_city_not_found), Toast.LENGTH_LONG)
                        .show()
                }

                // Se houver múltiplas possíveis localidades, abre diálogo para seleção
                is WeatherResult.MultipleLocations -> {
                    showLocationDialog(state.options)
                }

                // Para qualquer outro erro, exibe toast genérico
                is WeatherResult.Error -> {
                    Toast.makeText(
                        this, getString(R.string.error_generic), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    // Ao clicar no botão de pesquisa
    private fun setListeners() {
        binding.btnSearch.setOnClickListener {
            // Lê e valida o texto digitado no EditText
            val city = binding.etCity.text.toString().trim()

            if (city.isNotEmpty()){
                // Solicita ao ViewModel busca de clima para a cidade
                viewModel.fetchWeather(city)
            }
            else {
                // Se vazio, informa ao usuário via Toast
                Toast.makeText(
                    this, getString(R.string.error_empty_city), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showLocationDialog(options: List<Location>) {
        // Constrói array de labels "Cidade, Estado ou País"
        val labels = options.map { loc ->
            "${loc.name}, ${loc.state ?: loc.country}"
        }.toTypedArray()

        // Exibe um AlertDialog com as opções para o usuário escolher
        AlertDialog.Builder(this)
            .setTitle(R.string.title_select_city)
            .setItems(labels) { _, index ->
                // Quando o usuário seleciona, notifica o ViewModel
                viewModel.onLocationSelected(options[index])
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
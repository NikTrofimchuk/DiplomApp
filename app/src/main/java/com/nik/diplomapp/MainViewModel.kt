package com.nik.diplomapp

import android.annotation.SuppressLint
import android.app.Application
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.CountDownTimer
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.nik.diplomapp.data.entities.ProfileEntity
import com.nik.diplomapp.data.Repository
import com.nik.diplomapp.data.entities.HeatEntity
import com.nik.diplomapp.utils.Constants.Companion.WiFi_PASSWORD
import com.nik.diplomapp.utils.Constants.Companion.WiFi_SSID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException


class MainViewModel@ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private lateinit var wifiManager: WifiManager

    private val client = OkHttpClient()

    private var countDownTimer: CountDownTimer? = null
    var insrument_status = false

    val readProfiles: LiveData<List<ProfileEntity>> = repository.local.readProfiles().asLiveData()
    val readHeat: LiveData<List<HeatEntity>> = repository.local.readHeat().asLiveData()

    val temperatureList = mutableListOf<String>()

    val timeLiveData: MutableLiveData<String> = MutableLiveData()
    private val _temperatureLiveData = MutableLiveData<String?>()
    val temperatureLiveData: MutableLiveData<String?>
        get() = _temperatureLiveData

    private lateinit var temperatureUpdateThread: TemperatureUpdateThread

    init {
        startTemperatureUpdates()
    }

    fun addInProfiles(profile: ProfileEntity){
        insertProfiles(profile)
    }

    fun addInHeat(heat: HeatEntity){
        insertHeat(heat)
    }

    fun deleteProfile(name: String){
        deleteFromProfiles(name)
    }

    fun deleteHeat(){
        deleteHeatTable()
    }

    fun updateInProfile(name:String, oldName:String){
        updateProfile(name, oldName)
    }

    private fun insertProfiles(profile: ProfileEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertProfiles(profile)
        }

    private fun insertHeat(heat: HeatEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertHeat(heat)
        }

    private fun deleteFromProfiles(name: String) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFromProfiles(name)
        }

    private fun updateProfile(name:String, oldName:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.updateProfile(name, oldName)
        }
    }

    private fun deleteHeatTable(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteHeatTable()
        }
    }

    private fun startTemperatureUpdates() {
        temperatureUpdateThread = TemperatureUpdateThread(this)
        temperatureUpdateThread.start()
    }

    override fun onCleared() {
        super.onCleared()
        temperatureUpdateThread.stopThread()
    }

    fun setConnection(){
        viewModelScope.launch {
            connectToWiFi()
        }
    }

    fun makeRequest(message: String){
        viewModelScope.launch{
            sendRequest(message)
        }
    }

    fun startTimer(seconds: Int?, temp: Int?) {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer((seconds?.toLong() ?: 0) * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val totalSeconds = (millisUntilFinished / 1000).toInt()
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)
                timeLiveData.value = timeString
            }

            override fun onFinish() {
                makeRequest("power?value=255")
                makeRequest("fixtemp?value=$temp")
            }
        }

        countDownTimer?.start()
    }

    private suspend fun connectToWiFi(){
        withContext(Dispatchers.IO) {
            val wifiConfig = WifiConfiguration().apply {
                SSID = WiFi_SSID
                preSharedKey = WiFi_PASSWORD
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            }

            wifiManager.addNetwork(wifiConfig)
            val networkId = wifiManager.connectionInfo.networkId

            if (networkId == -1) {
                try {
                    val networks = wifiManager.configuredNetworks
                    for (network in networks) {
                        if (network.SSID == WiFi_SSID) {
                            wifiManager.disconnect()
                            wifiManager.enableNetwork(network.networkId, true)
                            wifiManager.reconnect()
                            break
                        }
                    }
                }
                catch (_: SecurityException){

                }
            } else {
                wifiManager.disconnect()
                wifiManager.enableNetwork(networkId, true)
                wifiManager.reconnect()
            }
        }
    }

    private suspend fun sendRequest(message: String){

        val request = Request.Builder()
            .url("http://192.168.4.1/$message")
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Запрос к серверу не был успешен:" +
                                " ${response.code} ${response.message}")
                    }

                    Log.d("Server:", "Response Body: ${response.code}")
                }
            } catch (e: IOException) {
                Log.d("Server:", "Ошибка подключения")
            }
        }
    }

    fun requestTemperature() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://192.168.4.1/currenttemp")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Обработка ошибки при выполнении запроса
                e.printStackTrace()
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() // Получение содержимого ответа в виде строки
                Log.d("Response", "Response Body: $responseBody")
                // Обработка полученного значения температуры
                _temperatureLiveData.postValue(responseBody)
                if (responseBody != null) {
                    temperatureList.add(responseBody)
                }
            }
        })
    }
}
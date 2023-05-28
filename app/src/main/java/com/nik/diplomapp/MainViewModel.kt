package com.nik.diplomapp

import android.annotation.SuppressLint
import android.app.Application
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.nik.diplomapp.data.DAO
import com.nik.diplomapp.data.DataBase
import com.nik.diplomapp.data.Entities.ProfileEntity
import com.nik.diplomapp.utils.Constants.Companion.WiFi_PASSWORD
import com.nik.diplomapp.utils.Constants.Companion.WiFi_SSID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var wifiManager: WifiManager

    private val client = OkHttpClient()

    private val Dao : DAO

    val temperatureList = mutableListOf<String>()

    private val _temperatureLiveData = MutableLiveData<String?>()
    val temperatureLiveData: MutableLiveData<String?>
        get() = _temperatureLiveData

    private lateinit var temperatureUpdateThread: TemperatureUpdateThread

    init {
        val appDatabase = Room.databaseBuilder(getApplication(), DataBase::class.java, "app-database").build()
        startTemperatureUpdates()
        Dao = appDatabase.Dao()

        val readProfiles: Flow<List<ProfileEntity>> = Dao.readProfiles()
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

    fun insertProfiles(profile: ProfileEntity){
        viewModelScope.launch{
            Dao.insertProfiles(profile)
        }
    }

    fun readProfiles(profile: ProfileEntity){
        viewModelScope.launch{
            Dao.readProfiles()
        }
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
package com.nik.diplomapp

class TemperatureUpdateThread(private val viewModel: MainViewModel) : Thread() {
    @Volatile
    private var isRunning = true

    override fun run() {
        while (isRunning) {
            viewModel.requestTemperature()
            sleep(1000) // Задержка между запросами в миллисекундах
        }
    }

    fun stopThread() {
        isRunning = false
    }
}
package com.nik.diplomapp.ui.fragments

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nik.diplomapp.MainViewModel
import com.nik.diplomapp.R
import com.nik.diplomapp.data.entities.HeatEntity
import com.nik.diplomapp.data.entities.ProfileEntity
import com.nik.diplomapp.databinding.FragmentCalibrationBinding
import com.nik.diplomapp.databinding.FragmentHomeBinding

class CalibrationFragment : Fragment() {

    private var _binding: FragmentCalibrationBinding? = null
    private val binding get() = _binding!!

    private var seconds: Int = 0
    private lateinit var chronometer: Chronometer
    private var isRunning = false
    private var elapsedTime: Long = 0
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalibrationBinding.inflate(inflater, container, false)
        chronometer = binding.chronometer
        chronometer.base = SystemClock.elapsedRealtime()

        binding.button2.setOnClickListener(){
            if (!isRunning) {
                chronometer.base = SystemClock.elapsedRealtime() - elapsedTime
                chronometer.start()
                isRunning = true
                binding.button2.text = "Остановить"
                viewModel.deleteHeat()
            }
            else{
                chronometer.stop()
                isRunning = false
                chronometer.base = SystemClock.elapsedRealtime()
                elapsedTime = 0
                binding.button2.text = "Запуск"
            }
        }

        chronometer.setOnChronometerTickListener {
            elapsedTime = SystemClock.elapsedRealtime() - it.base
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.temperatureLiveData.observe(viewLifecycleOwner) { temp ->
            Log.d("Temp updated", temp.toString())
            binding.tempTvCal.text = temp + " °C"
            if(isRunning){
                seconds++
                val heat = HeatEntity(
                    temperature = temp!!.toFloat(),
                    time = seconds
                )
                viewModel.addInHeat(heat)
            }
        }
        viewModel.readHeat.observe(viewLifecycleOwner) { database ->
            Log.d("database", database.toString())
        }
    }

}
package com.nik.diplomapp.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nik.diplomapp.MainViewModel
import com.nik.diplomapp.R
import com.nik.diplomapp.data.entities.ProfileEntity
import com.nik.diplomapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var temperature: Int? = null
    private var seconds: Int? = null
    private var power: Int? = null
    private var instrument = false

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        instrument = viewModel.insrument_status

        seconds = arguments?.getInt("seconds")
        temperature = arguments?.getInt("temperature")
        power = arguments?.getInt("power")

        Log.d("seconds:", seconds.toString())
        if (seconds != 0){
            viewModel.startTimer(seconds,temperature)
        }

        viewModel.timeLiveData.observe(viewLifecycleOwner) { timeString ->
            if(timeString == "00:00")binding.timerTv.text = ""
            else binding.timerTv.text = timeString
        }

        if (temperature != 0 && power != 0){
            if (power == 10){
                binding.btn10W.isChecked = true
                viewModel.makeRequest("power?value=51")
            }
            if (power == 20){
                binding.btn20W.isChecked = true
                viewModel.makeRequest("power?value=102")
            }
            if (power == 30){
                binding.btn30W.isChecked = true
                viewModel.makeRequest("power?value=153")
            }
            if (power == 40){
                binding.btn40W.isChecked = true
                viewModel.makeRequest("power?value=204")
            }
            if (power == 50){
                binding.btn50W.isChecked = true
                viewModel.makeRequest("power?value=255")
            }
            if (temperature != null) {
                binding.tempBar.progress = temperature as Int

                viewModel.makeRequest("fixtemp?value="+temperature.toString())
                setTextViewPosition(binding.tempBar)
            }
        }

        binding.powerButton.setOnClickListener(){
            if(viewModel.insrument_status){
                binding.powerButton.setImageResource(R.drawable.ic_baseline_power_settings_new_24)
                viewModel.makeRequest("on/off")
                viewModel.insrument_status = false
            }
            else{
                binding.powerButton.setImageResource(R.drawable.ic_baseline_power_settings_new_48)
                viewModel.makeRequest("on/off")
                viewModel.insrument_status = true
            }
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if(viewModel.insrument_status){
                group.findViewById<RadioButton>(checkedId)
                when (checkedId) {
                    R.id.btn_10W -> {
                        viewModel.makeRequest("power?value=51")
                    }
                    R.id.btn_20W -> {
                        viewModel.makeRequest("power?value=102")
                    }
                    R.id.btn_30W -> {
                        viewModel.makeRequest("power?value=153")
                    }
                    R.id.btn_40W -> {
                        viewModel.makeRequest("power?value=204")
                    }
                    R.id.btn_50W -> {
                        viewModel.makeRequest("power?value=255")
                    }
                }
            }
            else Toast.makeText(context, "Включите устройство", Toast.LENGTH_SHORT).show()
        }
        binding.WiFiBtn.setOnClickListener(){
            viewModel.setConnection()
        }
        binding.setTimeBtn.setOnClickListener(){
            findNavController().navigate(R.id.action_homeFragment_to_timerBottomSheet)
        }
        binding.templateBtn.setOnClickListener(){
            var power = 0
            val progress = binding.tempBar.progress

            if (binding.btn10W.isChecked)power=10
            if (binding.btn20W.isChecked)power=20
            if (binding.btn30W.isChecked)power=30
            if (binding.btn40W.isChecked)power=40
            if (binding.btn50W.isChecked)power=50

            val profile = ProfileEntity(
                name = "$power W $progress °C",
                temperature = progress,
                power = power
            )
            profile.temperature = binding.tempBar.progress
            viewModel.addInProfiles(profile)
        }
        viewModel.temperatureLiveData.observe(viewLifecycleOwner) { temperature ->
            Log.d("Temp updated", temperature.toString())
            binding.tempTv.text = temperature + " °C"
        }

        binding.tempBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    setTextViewPosition(seekBar)
                    binding.fixedTempTv.text = progress.toString()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if(viewModel.insrument_status)
                        viewModel.makeRequest("fixtemp?value="+binding.tempBar.progress.toString())
                    else Toast.makeText(context, "Включите устройство", Toast.LENGTH_SHORT).show()
                }
        })
    }

    private fun setTextViewPosition(seekBar: SeekBar){
        val thumbOffset = seekBar.thumbOffset
        val thumb = seekBar.thumb
        val thumbBounds = thumb?.bounds
        Log.d("thumbBounds", thumb.bounds.toString())

        val textX = thumbBounds?.left?.toFloat() ?: (0f + thumbOffset.toFloat())

        binding.fixedTempTv.x = textX + 22

    }
}
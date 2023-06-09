package com.nik.diplomapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.nik.diplomapp.MainViewModel
import com.nik.diplomapp.R
import com.nik.diplomapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
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
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
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
        binding.WiFiBtn.setOnClickListener(){
            viewModel.setConnection()
        }
        binding.setTimeBtn.setOnClickListener(){
            findNavController().navigate(R.id.action_homeFragment_to_timerBottomSheet)
        }
        viewModel.temperatureLiveData.observe(viewLifecycleOwner) { temperature ->
            Log.d("Temp updated", temperature.toString())
            binding.tempTv.text = temperature + " °C"
        }

        binding.tempBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val thumbOffset = seekBar?.thumbOffset ?: 0
                val thumb = seekBar?.thumb
                val thumbBounds = thumb?.bounds

                // Рассчитываем положение текста под ползунком
                val textX = thumbBounds?.left?.toFloat() ?: (0f + thumbOffset.toFloat())

                // Обновляем позицию текстового поля
                if(textX > 100)
                    binding.fixedTempTv.x = textX + 19
                else
                    binding.fixedTempTv.x = textX + 22

                // Обновляем значение в текстовом поле
                binding.fixedTempTv.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Метод вызывается при начале перемещения ползунка
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.makeRequest("fixtemp?value="+binding.tempBar.progress.toString())
                Log.d("Request", "fixtemp?value="+binding.tempBar.progress.toString())
            }
        })
    }
}
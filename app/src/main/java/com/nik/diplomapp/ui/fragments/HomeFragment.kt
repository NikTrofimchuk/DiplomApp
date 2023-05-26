package com.nik.diplomapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
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
        startTemperatureUpdates()
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
                    viewModel.makeRequest("10W")
                }
                R.id.btn_20W -> {
                    viewModel.makeRequest("20W")
                }
                R.id.btn_30W -> {
                    viewModel.makeRequest("30W")
                }
                R.id.btn_40W -> {
                    viewModel.makeRequest("40W")
                }
                R.id.btn_50W -> {
                    viewModel.makeRequest("50W")
                }
            }
        }
        binding.WiFiBtn.setOnClickListener(){
            viewModel.setConnection()
        }
        binding.setTimeBtn.setOnClickListener(){
            findNavController().navigate(R.id.action_homeFragment_to_timerBottomSheet)
        }
    }
    private fun startTemperatureUpdates() {
        Thread {
            while (true) {
                viewModel.requestTemperature()
                Thread.sleep(500) // Задержка между запросами в миллисекундах
            }
        }.start()
    }
}
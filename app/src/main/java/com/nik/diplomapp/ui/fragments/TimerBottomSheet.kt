package com.nik.diplomapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nik.diplomapp.MainViewModel
import com.nik.diplomapp.R
import com.nik.diplomapp.data.entities.ProfileEntity
import com.nik.diplomapp.databinding.FragmentHomeBinding
import com.nik.diplomapp.databinding.TimerBottomSheetBinding

class TimerBottomSheet: BottomSheetDialogFragment() {

    private var _binding: TimerBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TimerBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                setTextViewPosition(seekBar)
                binding.tempTvBar.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        binding.button.setOnClickListener(){
            val minut = binding.editTextTime.text.toString().toIntOrNull()
            val second = binding.editTextTime2.text.toString().toIntOrNull()
            val time = 60 * minut!! + second!!
            val bundle = Bundle().apply {
                putInt("temperature", 0)
                putInt("power", 0)
                putInt("seconds", time)
            }
            findNavController().navigate(R.id.action_timerBottomSheet_to_homeFragment, bundle)
        }
    }

    private fun setTextViewPosition(seekBar: SeekBar){
        val thumbOffset = seekBar.thumbOffset
        val thumb = seekBar.thumb
        val thumbBounds = thumb?.bounds
        Log.d("thumbBounds", thumb.bounds.toString())

        val textX = thumbBounds?.left?.toFloat() ?: (0f + thumbOffset.toFloat())
        binding.tempTvBar.x = textX + 22

    }
}
package com.nik.diplomapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate.getHoloBlue
import com.nik.diplomapp.MainViewModel
import com.nik.diplomapp.databinding.FragmentGraphsBinding

class GraphsFragment : Fragment() {

    private var _binding: FragmentGraphsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    private lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphsBinding.inflate(inflater, container, false)

        chart = binding.chart
        setupChart()
        viewModel.temperatureLiveData.observe(viewLifecycleOwner) { temperature ->
            Log.d("Chart updated", temperature.toString())
            // Обновление графика с новыми данными температуры
            if (temperature != null) {
                updateChart(temperature.toFloat())
            }
        }
        return binding.root
    }

    private fun setupChart() {
        val entries: MutableList<Entry> = mutableListOf()
        // Опции графика
        chart.setDrawGridBackground(false)
        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.legend.isEnabled = false

        chart.axisLeft.axisMaximum = 600f
        chart.axisLeft.axisMinimum = 0f

        val lineData = LineData()
        chart.data = lineData

        // Обновление отображения графика
        chart.invalidate()
    }

    private fun updateChart(temperature: Float) {
        val lineData = chart.data

        Log.d("Chart updated", temperature.toString())

        if (lineData != null) {
            var lineDataSet = lineData.getDataSetByIndex(0) as LineDataSet?

            if (lineDataSet == null) {
                lineDataSet = createLineDataSet()
                lineData.addDataSet(lineDataSet)
            }

            // Добавление новой точки данных
            val entry = Entry(lineDataSet.entryCount.toFloat(), temperature)
            lineDataSet.addEntry(entry)

            // Уведомление об изменении данных
            lineData.notifyDataChanged()
            chart.notifyDataSetChanged()

            // Ограничение количества отображаемых точек на графике
            chart.setVisibleXRangeMaximum(20f)
            chart.moveViewToX(lineData.entryCount.toFloat())
        }
    }

    private fun createLineDataSet(): LineDataSet {

        val entries: MutableList<Entry> = mutableListOf()

        for (i in viewModel.temperatureList.indices) {
            val entry = Entry(i.toFloat(), viewModel.temperatureList[i].toFloat())
            entries.add(entry)
        }
        // Данные графика (начальное состояние без данных)
        val lineDataSet = LineDataSet(entries, "Temperature")

        val lineData = LineData(lineDataSet) // Создание LineData с LineDataSet

        chart.data = lineData
        lineDataSet.setDrawIcons(false)
        lineDataSet.color = getHoloBlue()
        lineDataSet.setCircleColor(getHoloBlue())
        lineDataSet.lineWidth = 2f
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.valueTextSize = 10f
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor = getHoloBlue()
        lineDataSet.setDrawValues(false)

        return lineDataSet
    }
}
package com.nik.diplomapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nik.diplomapp.R
import org.achartengine.GraphicalView
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer

class GraphsFragment : Fragment() {

    private val mChart: GraphicalView? = null
    private val mDataset: XYMultipleSeriesDataset = XYMultipleSeriesDataset()
    private val mRenderer: XYMultipleSeriesRenderer = XYMultipleSeriesRenderer()
    private val mCurrentSeries: XYSeries? = null
    private val mCurrentRenderer: XYSeriesRenderer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_graphs, container, false)
    }

}
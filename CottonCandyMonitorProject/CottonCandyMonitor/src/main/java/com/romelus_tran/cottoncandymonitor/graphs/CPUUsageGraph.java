package com.romelus_tran.cottoncandymonitor.graphs;

import android.content.Context;

import com.romelus_tran.cottoncandymonitor.R;
import com.romelus_tran.cottoncandymonitor.activities.MainActivity;
import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;
import com.romelus_tran.cottoncandymonitor.utils.CCMUtils;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Brian on 11/21/13.
 * Create a CPUUsageGraph, in this case this will be the CPU display.
 */
public class CPUUsageGraph {

    private static final int TOTAL_POINTS = 20;

    private int _count = 0;

    private GraphicalView _view;

    private TimeSeries _userCPUSeries;
    private TimeSeries _systemCPUSeries;
    private TimeSeries _iowCPUSeries;
    private TimeSeries _irqCPUSeries;

    /**
     * Generates the intent for the line graph to pass into the graph activity.
     * @param context refers to the current activity
     * @return the view
     */
    public GraphicalView getView(Context context) {
        // Initialize the series
        _userCPUSeries = new TimeSeries("User CPU Usage");
        _systemCPUSeries = new TimeSeries("System CPU Usage");
        _iowCPUSeries = new TimeSeries("IOW CPU Usage");
        _irqCPUSeries = new TimeSeries("IRQ CPU Usage");

        // Create dataset and add _userCPUSeries to dataset
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(_userCPUSeries); // add line to dataset
        dataset.addSeries(_systemCPUSeries);
        dataset.addSeries(_iowCPUSeries);
        dataset.addSeries(_irqCPUSeries);

        // create renderer for the _userCPUSeries
        // Change renderer settings to tweak the line aesthetic
        XYSeriesRenderer userRenderer = new XYSeriesRenderer();
        userRenderer.setColor(context.getResources().getColor(R.color.lineColor1)); // Set the color of the line to white
        userRenderer.setLineWidth(5);
        userRenderer.setPointStyle(PointStyle.POINT);

        XYSeriesRenderer systemRenderer = new XYSeriesRenderer();
        systemRenderer.setColor(context.getResources().getColor(R.color.lineColor2)); // Set the color of the line to white
        systemRenderer.setLineWidth(5);
        systemRenderer.setPointStyle(PointStyle.POINT);

        XYSeriesRenderer iowRenderer = new XYSeriesRenderer();
        iowRenderer.setColor(context.getResources().getColor(R.color.lineColor3)); // Set the color of the line to white
        iowRenderer.setLineWidth(5);
        iowRenderer.setPointStyle(PointStyle.POINT);

        XYSeriesRenderer irqRenderer = new XYSeriesRenderer();
        irqRenderer.setColor(context.getResources().getColor(R.color.lineColor4)); // Set the color of the line to white
        irqRenderer.setLineWidth(5);
        irqRenderer.setPointStyle(PointStyle.POINT);

        // add renderers to MultipleSeriesRenderer
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(userRenderer);
        mRenderer.addSeriesRenderer(systemRenderer);
        mRenderer.addSeriesRenderer(iowRenderer);
        mRenderer.addSeriesRenderer(irqRenderer);

        // change mRenderer settings to tweak the graph aesthetic
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(context.getResources().getColor(R.color.backgroundColor));
        mRenderer.setMarginsColor(context.getResources().getColor(R.color.backgroundColor));
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(1);
        mRenderer.setPanEnabled(false); // disable panning the chart
        mRenderer.setZoomEnabled(false, false);

        _view = ChartFactory.getLineChartView(context, dataset, mRenderer);
        return _view;
    }

    /**
     * Update the graph based on the data passed in. We assume the data has the following:
     * -USER CPU Usage
     * -SYSTEM CPU Usage
     * -I/O WAIT (IOW) CPU Usage
     * -SOFTWARE INTERRUPT (IRQ) CPU USAGE
     * @param data with the cpu usage per type
     */
    public void updateGraph(List<MetricUnit> data) {
        float user = (float) data.get(0).getMetricValue();
        float system = (float) data.get(1).getMetricValue();
        float iow = (float) data.get(2).getMetricValue();
        float irq = (float) data.get(3).getMetricValue();

        // since these are the latest values (in terms of time), we can add them to the _userCPUSeries at the highest index.
        if (_userCPUSeries.getItemCount() > TOTAL_POINTS) {
            _userCPUSeries.remove(0);
            _systemCPUSeries.remove(0);
            _iowCPUSeries.remove(0);
            _irqCPUSeries.remove(0);
        }

        _userCPUSeries.add(_count, user);
        _systemCPUSeries.add(_count, system);
        _iowCPUSeries.add(_count, iow);
        _irqCPUSeries.add(_count, irq);
        _view.repaint();

        ++_count; // increment count
    }
}

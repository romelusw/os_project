package com.romelus_tran.cottoncandymonitor.graphs;

import android.content.Context;

import com.romelus_tran.cottoncandymonitor.R;
import com.romelus_tran.cottoncandymonitor.monitor.MetricUnit;
import com.romelus_tran.cottoncandymonitor.utils.FontUtils;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

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
        _userCPUSeries = new TimeSeries(context.getResources().getString(R.string.cpu_usage_user));
        _systemCPUSeries = new TimeSeries(context.getResources().getString(R.string.cpu_usage_system));
        _iowCPUSeries = new TimeSeries(context.getResources().getString(R.string.cpu_usage_iow));
        _irqCPUSeries = new TimeSeries(context.getResources().getString(R.string.cpu_usage_irq));

        // Create dataset and add _userCPUSeries to dataset
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(_userCPUSeries); // add line to dataset
        dataset.addSeries(_systemCPUSeries);
        dataset.addSeries(_iowCPUSeries);
        dataset.addSeries(_irqCPUSeries);

        // create renderer for the _userCPUSeries
        // Change renderer settings to tweak the line aesthetic
        XYSeriesRenderer userRenderer = new XYSeriesRenderer();
        userRenderer.setColor(context.getResources().getColor(R.color.userColor));
        userRenderer.setLineWidth(3);

        XYSeriesRenderer systemRenderer = new XYSeriesRenderer();
        systemRenderer.setColor(context.getResources().getColor(R.color.systemColor));
        systemRenderer.setLineWidth(3);

        XYSeriesRenderer iowRenderer = new XYSeriesRenderer();
        iowRenderer.setColor(context.getResources().getColor(R.color.iowColor));
        iowRenderer.setLineWidth(3);

        XYSeriesRenderer irqRenderer = new XYSeriesRenderer();
        irqRenderer.setColor(context.getResources().getColor(R.color.irqColor));
        irqRenderer.setLineWidth(3);

        // add renderers to MultipleSeriesRenderer
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(userRenderer);
        mRenderer.addSeriesRenderer(systemRenderer);
        mRenderer.addSeriesRenderer(iowRenderer);
        mRenderer.addSeriesRenderer(irqRenderer);

        // change mRenderer settings to tweak the graph aesthetic
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(context.getResources().getColor(R.color.graphBackgroundColor));
        mRenderer.setMarginsColor(context.getResources().getColor(R.color.graphBackgroundColor));
        mRenderer.setXLabels(0);
        mRenderer.setYLabels(0);
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(100);
        mRenderer.setPanEnabled(false); // disable panning the chart
        mRenderer.setZoomEnabled(false, false); // disable zooming
        mRenderer.setShowLegend(true); // show the legend
        mRenderer.setFitLegend(true); // make sure it fits
        mRenderer.setLegendTextSize(18);
        mRenderer.setTextTypeface(FontUtils.loadFontFromAssets(FontUtils.FONT_CAVIAR_DREAMS_BOLD));

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
        int user = Integer.parseInt((String) data.get(0).getMetricValue());
        int system = Integer.parseInt((String) data.get(1).getMetricValue());
        int iow = Integer.parseInt((String) data.get(2).getMetricValue());
        int irq = Integer.parseInt((String) data.get(3).getMetricValue());

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

package com.romelus_tran.cottoncandymonitor.graphs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.romelus_tran.cottoncandymonitor.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

/**
 * Created by Brian on 11/21/13.
 * Create a LineGraph, in this case this will be the CPU display.
 */
public class LineGraph {

    TimeSeries series;

    /**
     * Generates the intent for the line graph to pass into the graph activity.
     * @param context refers to the current activity
     * @return the view
     */
    public GraphicalView getView(Context context) {

        // populate data with x/y positions
        int[] x = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        int[] y = { 30, 34, 45, 57, 77, 89, 5, 111, 123, 145 };

        // generate series based on x/y positions stated above.
        // A series simply requires x and y info.
        // TODO: Dynamically update data. Review the following links:
        // http://stackoverflow.com/questions/12948708/dynamically-updated-charts-with-achartengine
        // https://code.google.com/p/achartengine/source/browse/trunk/achartengine/demo/org/achartengine/chartdemo/demo/chart/XYChartBuilder.java
        series = new TimeSeries("CPU Usage");

        for (int i = 0; i < x.length; i++) {
            series.add(x[i], y[i]);
        }

        // Create dataset and add series to dataset
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series); // add line to dataset

        // create renderer for the series
        // Change renderer settings to tweak the line aesthetic
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(context.getResources().getColor(R.color.lineColor1)); // Set the color of the line to white
        renderer.setLineWidth(5);
        renderer.setPointStyle(PointStyle.POINT);

        // create renderer and add renderer
        // Change mRenderer settings to tweak the graph aesthetic
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(context.getResources().getColor(R.color.backgroundColor));
        mRenderer.setMarginsColor(context.getResources().getColor(R.color.backgroundColor));
        mRenderer.setChartTitle("CPU Usage");

        return ChartFactory.getLineChartView(context, dataset, mRenderer);
    }

    public void setGraph(ArrayList<Point> points) {
        series.clear(); // reset the graph and repopulate

        Point p;
        int len = points.size();
        for (int i = 0; i < len; i++) {
            p = points.get(i);
            series.add(p.getX(), p.getY());
        }

    }
}

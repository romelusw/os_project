package com.romelus_tran.cottoncandymonitor.monitor;

import java.util.Date;

/**
 * An object that represents a numerical measurement providing its relationship with its context.
 *
 * <p/>Since the context is generic you can represent an infinite amount of things.<p/>
 * As an example suppose you wanted to represent your <em>Total Disk Space</em> on your hard drive.
 * It would look like this:
 * <code>
 *     MetricUnit mu = new MetricUnit(new Date(), 1, "TB");
 * </code>
 */
public class MetricUnit {

    private Date metricTimestamp;
    private Long metricValue;
    private String metricAttr;

    /**
     * Default Constructor
     */
    public MetricUnit() {
        this(new Date(System.currentTimeMillis()), Long.valueOf(0), "");
    }

    /**
     * Constructor.
     * @param timestamp the time the measurement was taken
     * @param value the value of the measurement
     * @param attribute an optional attribute associated to the measurement
     */
    public MetricUnit(final Date timestamp, final Long value, final String attribute) {
        metricTimestamp = timestamp;
        metricValue = value;
        metricAttr = attribute;
    }

    /**
     * Getter for the timestamp
     * @return the timestamp
     */
    public Date getMetricTimestamp() {
        return metricTimestamp;
    }

    /**
     * Getter for the metricValue
     * @return the metric value
     */
    public Long getMetricValue() {
        return metricValue;
    }

    /**
     * Getter for the attribute.
     * @return the attribute
     */
    public String getMetricAttr() {
        return metricAttr;
    }
}
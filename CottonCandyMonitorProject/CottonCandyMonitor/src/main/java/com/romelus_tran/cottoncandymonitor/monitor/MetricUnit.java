package com.romelus_tran.cottoncandymonitor.monitor;

import java.util.Date;

/**
 * An object that represents a numerical measurement providing its relationship
 * with its context.
 *
 * <p/>Since the context is generic you can represent an infinite amount of
 * things.<p/>
 * As an example suppose you wanted to represent your <em>Total Disk Space</em>
 * on your hard drive. It would look like this:
 * <code>
 *      MetricUnit mu = new MetricUnit(new Date(), "Total Disk Space", 1, "TB");
 * </code>
 *
 * @author Woody Romelus
 */
public class MetricUnit {

    private Date metricTimestamp;
    private String metricName;
    private Long metricValue;
    private String metricAttr;

    /**
     * Default Constructor
     */
    public MetricUnit() {
        this(new Date(System.currentTimeMillis()), "", (long) 0, "");
    }

    /**
     * Constructor.
     *
     * @param timestamp the time the measurement was taken
     * @param name      the metric identifier
     * @param value     the value of the measurement
     * @param attribute an optional attribute associated to the measurement
     */
    public MetricUnit(final Date timestamp, final String name, final Long value,
                      final String attribute) {
        metricTimestamp = timestamp;
        metricName = name;
        metricValue = value;
        metricAttr = attribute;
    }

    /**
     * Getter for the timestamp.
     *
     * @return the timestamp
     */
    public Date getMetricTimestamp() {
        return metricTimestamp;
    }

    /**
     * Getter for the name.
     *
     * @return the name
     */
    public String getMetricName() {
        return metricName;
    }

    /**
     * Getter for the metricValue.
     *
     * @return the metric value
     */
    public Long getMetricValue() {
        return metricValue;
    }

    /**
     * Getter for the attribute.
     *
     * @return the attribute
     */
    public String getMetricAttr() {
        return metricAttr;
    }
}
package com.romelus_tran.cottoncandymonitor.monitor;

import java.util.Date;

/**
 * An object that represents unit of data which relates to a context.
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
    private String metricId;
    private Object metricValue;
    private String metricAttr;

    /**
     * Default Constructor
     */
    public MetricUnit() {
        this(new Date(System.currentTimeMillis()), "", null, "");
    }

    /**
     * Constructor.
     *
     * @param timestamp the time the measurement was taken
     * @param name      the metric identifier
     * @param value     the value
     * @param attribute an optional attribute associated to the measurement
     */
    public MetricUnit(final Date timestamp, final String name, final Object value,
                      final String attribute) {
        metricTimestamp = timestamp;
        metricId = name;
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
     * Getter for the id.
     *
     * @return the id
     */
    public String getMetricId() {
        return metricId;
    }

    /**
     * Getter for the metricValue.
     *
     * @return the metric value
     */
    public Object getMetricValue() {
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MetricUnit [")
                .append("time: " + getMetricTimestamp() + "\\s")
                .append("id: " + getMetricId() + "\\s")
                .append("val: " + getMetricValue() + "\\s")
                .append("attr: " + getMetricAttr() + "\\s")
                .append("]");
        return sb.toString();
    }
}
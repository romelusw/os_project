package com.romelus_tran.cottoncandymonitor.monitor.listeners;

/**
 * Enumeration of the various responses for a {@code com.romelus_tran.cottoncandymonitor.monitor.listeners.IResultListener}.
 *
 * @author Woody Romelus
 */
public enum ResultListenerResponse {
    SUCCESS(0, "Successfully processed the list of metrics."),
    FAILURE(1, "Failed to process the list of metrics.");

    private int response;
    private String message;

    /**
     * Default Constructor.
     * @param respCode a numeric value indicating the status of the response
     * @param respMsg a detailed message about the status of the response
     */
    ResultListenerResponse(final int respCode, final String respMsg){
        response = respCode;
        message = respMsg;
    }

    /**
     * Getter for the response code numeric value
     * @return the response code
     */
    public int getResponseCode() {
        return response;
    }

    /**
     * Getter for message.
     * @return the response message
     */
    public String getMessage() {
        return message;
    }
}
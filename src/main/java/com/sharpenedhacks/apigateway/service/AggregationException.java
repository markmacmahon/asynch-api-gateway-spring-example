package com.sharpenedhacks.apigateway.service;

/**
 * Exception to indicate if there is a problem aggregating data.
 */
class AggregationException extends RuntimeException {

    public AggregationException(String message) {
        super(message);
    }
}

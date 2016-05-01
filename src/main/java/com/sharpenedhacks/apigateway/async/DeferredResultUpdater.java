package com.sharpenedhacks.apigateway.async;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

/**
 * Given a DeferredResult, some ListenableFutures and a ResponseEntityListConverter, update the DeferredResult when the responses have returned.
 */
public class DeferredResultUpdater<T> implements FutureCallback<List<ResponseEntity>> {

    private static final Logger LOG = LoggerFactory.getLogger(DeferredResultUpdater.class);
    private final DeferredResult<ResponseEntity<T>> deferredResult;
    private final ResponseEntityListConverter<T> responseEntityListConverter;

    /**
     * @param deferredResult              Deferred Result to be updated.
     * @param apiCalls                    Api call ListeneableFutures.
     * @param responseEntityListConverter Aggregator that combines results of each Api Call.
     */
    public DeferredResultUpdater(DeferredResult<ResponseEntity<T>> deferredResult, ListenableFuture<ResponseEntity>[] apiCalls, ResponseEntityListConverter<T> responseEntityListConverter) {
        this.deferredResult = deferredResult;
        this.responseEntityListConverter = responseEntityListConverter;
        //Register as listener to list of ListeneableFutures.
        Futures.addCallback(Futures.allAsList(apiCalls), this);
    }

    /**
     * Success callback for list of all successful API responses. Note: this is not called if there are any failures.
     */
    public void onSuccess(List<ResponseEntity> results) {
        LOG.info("onSuccess callback with {} results to aggregate ", results.size());
        try {
            ResponseEntity<T> converted = responseEntityListConverter.convert(results);
            deferredResult.setResult(converted);
        } catch (Exception ex) {
            LOG.error("onSuccess callback: unexpected error aggregating results ", ex);
            deferredResult.setResult(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
        }
    }

    /**
     * Failure callback - Note: failure for any response will trigger this.
     */
    public void onFailure(Throwable thrown) {
        LOG.error("onFailure callback ", thrown);
        deferredResult.setResult(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
    }
}

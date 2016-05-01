package com.sharpenedhacks.apigateway.service;

import com.google.common.util.concurrent.ListenableFuture;
import com.sharpenedhacks.apigateway.async.DeferredResultUpdater;
import com.sharpenedhacks.apigateway.domain.Post;
import com.sharpenedhacks.apigateway.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import static net.javacrumbs.futureconverter.springguava.FutureConverter.toGuavaListenableFuture;

/**
 * Aggregates two different jsonplaceholder REST endpoints and outputs the results into a merged response.
 *
 * @see <a href="http://jsonplaceholder.typicode.com">http://jsonplaceholder.typicode.com/a>
 */
@Service
public class AggregationService {

    private static final Logger LOG = LoggerFactory.getLogger(AggregationService.class);

    public static final String ENDPOINT = "http://jsonplaceholder.typicode.com";

    private final AsyncRestTemplate restTemplate;

    @Autowired
    public AggregationService(AsyncRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public DeferredResult<ResponseEntity<User>> getUser(String id) {
        LOG.info("Fetching data for user with id {}", id);
        //Using DeferredResult for non-blocking io
        final DeferredResult<ResponseEntity<User>> deferredResult = new DeferredResult<>();
        //The list of API calls to aggregate as ListenableFutures
        final ListenableFuture<ResponseEntity>[] apiCalls = new ListenableFuture[]{
                toGuavaListenableFuture(restTemplate.getForEntity(ENDPOINT + "/users/{id}", User.class, id)),
                toGuavaListenableFuture(restTemplate.getForEntity(ENDPOINT + "/posts?userId={id}", Post[].class, id))
        };
        //DeferredResultUpdater waits for API calls to complete before aggregating and updating response.
        new DeferredResultUpdater(deferredResult, apiCalls, new UserResponseEntityListConverter());
        LOG.info("Api calls dispatched for user with id {}", id);
        return deferredResult;
    }


}

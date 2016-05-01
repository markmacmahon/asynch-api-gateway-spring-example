package com.sharpenedhacks.apigateway;

import com.sharpenedhacks.apigateway.domain.User;
import com.sharpenedhacks.apigateway.service.AggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * REST API for {@link AggregationService}
 */
@RestController
public class ApiController {
    private final AggregationService aggregationService;

    /**
     * @param aggregationService Api wrapper.
     */
    @Autowired
    public ApiController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<User>> getUser(@PathVariable String id) {
        return aggregationService.getUser(id);
    }

}



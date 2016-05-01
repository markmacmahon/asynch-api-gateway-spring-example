package com.sharpenedhacks.apigateway.service;

import com.sharpenedhacks.apigateway.domain.User;
import com.sharpenedhacks.apigateway.test.TestData;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests for {@link AggregationService}
 */
public class AggregationServiceTest {

    private final AsyncRestTemplate restTemplate = new AsyncRestTemplate();
    private final MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    private final AggregationService aggregationService = new AggregationService(restTemplate);


    @Test
    public void shouldGetAggregatedUser() throws Exception {

        // given: user with id 1 has profile and posts
        mockServer.expect(requestTo(AggregationService.ENDPOINT + "/users/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(TestData.USER1_JSON, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(AggregationService.ENDPOINT + "/posts?userId=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(TestData.USER1_POSTS_JSON, MediaType.APPLICATION_JSON));

        //when: aggregated user is fetched
        DeferredResult<ResponseEntity<User>> result = aggregationService.getUser("1");
        @SuppressWarnings("unchecked")
        ResponseEntity<User> userResponse = (ResponseEntity<User>) result.getResult();

        //then: user with posts is returned
        User user = userResponse.getBody();
        assertThat(user, notNullValue());
        assertThat(user.getId(), is("1"));
        assertThat(user.getPosts(), notNullValue());
        assertThat(user.getPosts(), not(Collections.emptyList()));

        mockServer.verify();

    }


}

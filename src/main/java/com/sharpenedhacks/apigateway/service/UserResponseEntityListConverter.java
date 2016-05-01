package com.sharpenedhacks.apigateway.service;

import com.sharpenedhacks.apigateway.async.ResponseEntityListConverter;
import com.sharpenedhacks.apigateway.domain.Post;
import com.sharpenedhacks.apigateway.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

/**
 * Merges getUser & getUserPosts API responses into an aggregated User Object.
 */
public class UserResponseEntityListConverter implements ResponseEntityListConverter<User> {
    private static final Logger LOG = LoggerFactory.getLogger(UserResponseEntityListConverter.class);

    public ResponseEntity<User> convert(List<ResponseEntity> results) {
        if ((results == null) || (results.isEmpty())) {
            throw new AggregationException("No responses to process");
        }
        LOG.info("Aggregating {} results into User object", results.size());
        Post[] posts = null;
        User user = null;
        for (ResponseEntity response : results) {
            if (response.getBody() instanceof User) {
                user = (User) response.getBody();
            } else if (response.getBody() instanceof Post[]) {
                posts = (Post[]) response.getBody();
            }
        }

        if ((user == null) || (posts == null)) {
            LOG.error("Not enough data to aggregate, throwing AggregationException, User {}, Posts {}", user, posts);
            throw new AggregationException("Not enough responses to process");
        }
        //being lazy here, could add copy constructor to User so that new instance is used..
        user.setPosts(Arrays.asList(posts));
        LOG.info("Returning aggregated User {} ", user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}

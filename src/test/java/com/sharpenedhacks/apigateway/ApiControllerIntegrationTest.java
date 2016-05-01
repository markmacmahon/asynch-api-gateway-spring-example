package com.sharpenedhacks.apigateway;

import com.sharpenedhacks.apigateway.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for {@link ApiController}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AppMain.class)
@WebIntegrationTest("server.port:0")
public class ApiControllerIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Test
    public void shouldGetAggregatedUserWithPostsById() throws Exception {
        ResponseEntity<User> response = new RestTemplate().getForEntity("http://localhost:" + this.port + "/users/1", User.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        User user = response.getBody();
        assertThat(user, notNullValue());
        assertThat(user.getId(), is("1"));
        assertThat(user.getPosts(), notNullValue());
        assertThat(user.getPosts(), not(Collections.emptyList()));
    }


}

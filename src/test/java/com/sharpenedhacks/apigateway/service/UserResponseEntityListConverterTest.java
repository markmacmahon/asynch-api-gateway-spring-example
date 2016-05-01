package com.sharpenedhacks.apigateway.service;

import com.sharpenedhacks.apigateway.domain.Post;
import com.sharpenedhacks.apigateway.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link UserResponseEntityListConverter}
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class UserResponseEntityListConverterTest {

    private final UserResponseEntityListConverter aggregator = new UserResponseEntityListConverter();
    private final User testUser1 = new User("testUserId1");
    private final Post testPost1 = new Post("testPostId1");
    private final Post[] testPosts = new Post[]{testPost1};

    @Test
    public void shouldAggregateUserAndPosts() {
        ResponseEntity<User> userResponse = mock(ResponseEntity.class);
        when(userResponse.getBody()).thenReturn(testUser1);
        ResponseEntity<Post[]> postsResponse = mock(ResponseEntity.class);
        when(postsResponse.getBody()).thenReturn(testPosts);
        User updatedUser = aggregator.convert(Arrays.asList(userResponse, postsResponse)).getBody();
        assertThat(updatedUser, notNullValue());
        assertThat(updatedUser.getId(), is(testUser1.getId()));
        assertThat(updatedUser.getPosts(), notNullValue());
        assertThat(updatedUser.getPosts(), not(Collections.emptyList()));
        assertThat(updatedUser.getPosts().size(), is(testPosts.length));
        assertThat(updatedUser.getPosts().contains(testPost1), is(true));
    }

    @Test(expected = AggregationException.class)
    public void shouldThrowExceptionIfMissingUser() {
        ResponseEntity<Post[]> postsResponse = mock(ResponseEntity.class);
        when(postsResponse.getBody()).thenReturn(new Post[]{testPost1});
        aggregator.convert(Collections.singletonList(postsResponse));
    }

    @Test(expected = AggregationException.class)
    public void shouldThrowExceptionIfMissingPosts() {
        ResponseEntity<User> userResponse = mock(ResponseEntity.class);
        when(userResponse.getBody()).thenReturn(testUser1);
        ResponseEntity<Post[]> postsResponse = mock(ResponseEntity.class);
        when(postsResponse.getBody()).thenReturn(testPosts);
        aggregator.convert(Collections.singletonList(userResponse));
    }

    @Test(expected = AggregationException.class)
    public void shouldThrowExceptionForNullResponses() {
        aggregator.convert(null);
    }

    @Test(expected = AggregationException.class)
    public void shouldThrowExceptionForEmptyResponses() {
        aggregator.convert(new ArrayList<>());
    }

}

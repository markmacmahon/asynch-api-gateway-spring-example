package com.sharpenedhacks.apigateway.async;

import com.google.common.util.concurrent.ListenableFuture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DeferredResultUpdater}
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class DeferredResultUpdaterTest {

    @Mock
    private ListenableFuture<ResponseEntity> mockFuture;

    @Before
    public void setUp() {
        //This has the effect of making sure the success or failure callback is never called, making testing more predictable.
        when(mockFuture.isDone()).thenReturn(false);
    }

    @Test
    public void shouldUpdateResultWithHttp503OnExceptionCallback() {

        // given: a DeferredResult and DeferredResultUpdater
        DeferredResult<ResponseEntity<String>> deferredResult = mock(DeferredResult.class);
        DeferredResultUpdater deferredResultUpdater = new DeferredResultUpdater(deferredResult, new ListenableFuture[]{mockFuture}, mock(ResponseEntityListConverter.class));

        // When failure callback is invoked
        deferredResultUpdater.onFailure(new RuntimeException("Random Exception"));

        // then: HTTP 503 status code is returned in response
        ArgumentCaptor<ResponseEntity> argument = ArgumentCaptor.forClass(ResponseEntity.class);

        // and: DeferredResult is updated
        verify(deferredResult).setResult(argument.capture());
        assertThat(argument.getValue().getStatusCode(), is(HttpStatus.SERVICE_UNAVAILABLE));
    }


    @Test
    public void shouldUpdateResultOnSuccessCallback() {
        // given: a list of input ResponseEntities and a ResponseEntityListConverter
        ResponseEntity<String> inputResponse1 = mock(ResponseEntity.class);
        when(inputResponse1.getBody()).thenReturn("inputResponse1");
        when(inputResponse1.getStatusCode()).thenReturn(HttpStatus.OK);
        List<ResponseEntity> inputResponses = Collections.singletonList(inputResponse1);

        // Response output for ResponseEntityListConverter
        ResponseEntity<String> outputResponse = mock(ResponseEntity.class);
        when(outputResponse.getBody()).thenReturn("outputResponse");
        when(outputResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        ResponseEntityListConverter<String> responseEntityListConverter = mock(ResponseEntityListConverter.class);
        when(responseEntityListConverter.convert(eq(inputResponses))).thenReturn(outputResponse);

        // when: DeferredResultUpdater onSuccess callback is invoked
        DeferredResult<ResponseEntity<String>> deferredResult = mock(DeferredResult.class);
        DeferredResultUpdater deferredResultUpdater = new DeferredResultUpdater(deferredResult, new ListenableFuture[]{mockFuture}, responseEntityListConverter);
        deferredResultUpdater.onSuccess(inputResponses);

        // then: ResponseEntityListConverter converts responses
        verify(responseEntityListConverter).convert(eq(inputResponses));

        // and: DeferredResult is updated
        verify(deferredResult).setResult(eq(outputResponse));

    }


    @Test
    public void shouldUpdateResultWithHttp503OnSuccessCallbackException() {
        // given: a list of input ResponseEntities and a ResponseEntityListConverter that always throws execeptions
        ResponseEntity<String> inputResponse1 = mock(ResponseEntity.class);
        when(inputResponse1.getBody()).thenReturn("inputResponse1");
        when(inputResponse1.getStatusCode()).thenReturn(HttpStatus.OK);
        ResponseEntityListConverter<String> responseEntityListConverter = mock(ResponseEntityListConverter.class);
        when(responseEntityListConverter.convert(anyListOf(ResponseEntity.class))).thenThrow(new RuntimeException("Random Exception"));

        // when: DeferredResultUpdater onSuccess callback is invoked
        DeferredResult<ResponseEntity<String>> deferredResult = mock(DeferredResult.class);
        DeferredResultUpdater deferredResultUpdater = new DeferredResultUpdater(deferredResult, new ListenableFuture[]{mockFuture}, responseEntityListConverter);
        deferredResultUpdater.onSuccess(Collections.singletonList(inputResponse1));

        // then: HTTP 503 status code is returned in response
        ArgumentCaptor<ResponseEntity> argument = ArgumentCaptor.forClass(ResponseEntity.class);

        // and: DeferredResult is updated
        verify(deferredResult).setResult(argument.capture());
        assertThat(argument.getValue().getStatusCode(), is(HttpStatus.SERVICE_UNAVAILABLE));

    }


}

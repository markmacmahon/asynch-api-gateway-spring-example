package com.sharpenedhacks.apigateway.async;

import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Given a list of ResponseEntities, convert them into another ResponseEntity.
 */
public interface ResponseEntityListConverter<T> {
    ResponseEntity<T> convert(List<ResponseEntity> results);
}

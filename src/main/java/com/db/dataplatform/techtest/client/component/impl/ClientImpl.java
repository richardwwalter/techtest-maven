package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.ServiceConfiguration;
import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.retry.support.RetrySynchronizationManager.getContext;
/**
 * Client code does not require any test coverage
 *
 * In a production system would be invoking via a service gateway(dynamic load balancer) such as Zuul
 * Backed by a service registry such as Eureka
 * Would also be using https and adding authentication
 * Would prefer to use WebClient instead of legacy RestTemplate if it was available
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {

    private static final ParameterizedTypeReference<List<DataEnvelope>> LIST_DATA_TYPE = new ParameterizedTypeReference<List<DataEnvelope>>(){};

    private final ServiceConfiguration config;
    private final RestTemplate restTemplate;

    /**
     * Posts a single DataEnvelope to the server
     * Will retry a number of times on failure - Would be preferable to use a circuit breaker here in preference to Spring Retry
     * @param dataEnvelope - data to push
     * @return boolean to indicate if validation and saving to the server was successful
     */
    @Override
    @Retryable( value = RestClientException.class,  maxAttempts = 3, backoff = @Backoff(delay = 100,multiplier = 2))
    public boolean pushData(DataEnvelope dataEnvelope) {
        int retryCount = getContext().getRetryCount();
        if ( retryCount > 0){
            log.warn("Retry {} Pushing data {} to {}", retryCount, dataEnvelope.getDataHeader().getName(), config.getURI_PUSHDATA());
        }else{
            log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), config.getURI_PUSHDATA());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("MD5_CHECKSUM", DigestUtils.md5Hex(dataEnvelope.getDataBody().getDataBody()) );

        HttpEntity<DataEnvelope> requestEntity = new HttpEntity<>(dataEnvelope, headers);
        ResponseEntity<Boolean> response = restTemplate.postForEntity( config.getURI_PUSHDATA(), requestEntity, Boolean.class);

        if( response.getStatusCode().equals(CONFLICT) ){
            log.error("Server returned checkSum failed response");
            return false;
        }
        if( response.getStatusCode().equals(CREATED) ){
            log.info("New data saved URI: {} ", response.getHeaders().getLocation());
        }
        return true;
    }

    /**
     * Get a list of data from the server for a particular blockType
     * @param blockType - block type to filter on
     * @return - list of matching data envelopes
     */
    @Override
    @Retryable( value = RestClientException.class,  maxAttempts = 3, backoff = @Backoff(delay = 100,multiplier = 2))
    public List<DataEnvelope> getData(String blockType) {

        int retryCount = getContext().getRetryCount();
        if ( retryCount > 0){
            log.info("Retry {} Query for data with header block type {}",retryCount, blockType);
        }else{
            log.info("Query for data with header block type {}", blockType);
        }

        ResponseEntity<List<DataEnvelope>> response = restTemplate.exchange(config.getURI_GETDATA().expand(blockType),
                        HttpMethod.GET, null, LIST_DATA_TYPE);

        List<DataEnvelope> data = response.hasBody() ? response.getBody() : Collections.emptyList();
        log.info("Data query returned {} records", data.size());
        return data;
    }

    /**
     * Update the blockType for a particular block identified by its name
     * @param blockName - blockName to update
     * @param newBlockType - new block type
     * @return boolean to indicate if update was successful
     */
    @Override
    @Retryable( value = RestClientException.class,  maxAttempts = 3, backoff = @Backoff(delay = 100,multiplier = 2))
    public boolean updateData(String blockName, String newBlockType) {

        int retryCount = getContext().getRetryCount();
        if ( retryCount > 0){
            log.info("Retry {} Updating block type to {} for block with name {}", retryCount, newBlockType, blockName);
        }else{
            log.info("Updating block type to {} for block with name {}", newBlockType, blockName);
        }

        ResponseEntity<HttpStatus> response = restTemplate.exchange(config.getURI_PATCHDATA().expand(blockName, newBlockType),
                        HttpMethod.PATCH, null, HttpStatus.class);
        if( response.getStatusCode().equals(HttpStatus.NO_CONTENT)){
            return false;
        }
        log.info("Successfully updated data block name {} ",blockName);
        return true;
    }

}

package com.db.dataplatform.techtest.server.component.impl.hadoop;

import com.db.dataplatform.techtest.ServiceConfiguration;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.springframework.retry.support.RetrySynchronizationManager.getContext;

/**
 * Publish to the hadoop rest service within a circuit breaker context that will retry on failure
 * And attempt calls again based on the configured timeouts
 */
@Component
@Slf4j
@AllArgsConstructor
public class HadoopWithCircuitBreakerAndRetryPublisher {

    private final ServiceConfiguration config;
    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;

    @CircuitBreaker(value={HadoopClientException.class}, maxAttempts = 3, openTimeout = 30_000, resetTimeout = 60_000, label="CB:HADOOP_PUBLISHING")
    public void publishWithRetry( DataEnvelope data)  throws HadoopClientException {
        retryTemplate.execute((RetryCallback<HttpStatus, HadoopClientException>) context -> {
            int retryCount = getContext().getRetryCount();
            if (retryCount > 0) {
                log.info("Retry {} Pushing to hadoop",retryCount);
            } else {
                log.info("Pushing to hadoop");
            }
            try {
                ResponseEntity<HttpStatus> response = restTemplate.postForEntity(config.getURI_HADOOP_PUSHDATA(), data, HttpStatus.class);
                log.info("Successfully published to hadoop on attempt: {} response: {}", retryCount + 1, response.getStatusCode());
                return response.getStatusCode();
            } catch (RestClientException ex) {
                    throw new HadoopClientException("Failed to publish to hadoop id " + data.getDataBody().getId() + " Recvd: "
                            + ((ex instanceof HttpServerErrorException) ? ((HttpServerErrorException)ex).getStatusCode() : ex.getMessage()), ex);
            }
        });
    }

}

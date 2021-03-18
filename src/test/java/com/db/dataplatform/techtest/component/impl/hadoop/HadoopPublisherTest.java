package com.db.dataplatform.techtest.component.impl.hadoop;

import com.db.dataplatform.techtest.ServiceConfiguration;
import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.component.impl.SaveAndPublishedStatus;
import com.db.dataplatform.techtest.server.component.impl.hadoop.HadoopWithCircuitBreakerAndRetryPublisher;
import com.db.dataplatform.techtest.server.component.impl.hadoop.TransactionAwareHadoopPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataHeader;
import static com.db.dataplatform.techtest.server.component.impl.SaveAndPublishedStatus.SaveAndPublishStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class HadoopPublisherTest {

    @Mock
    private  ServiceConfiguration configMock;

    @Mock
    private RestTemplate restTemplateMock;

    private static RetryTemplate retryTemplate;

    private DataEnvelope testEnvelope;

    private static final String testHadoopUURI = "http:/localhost/test";

    @BeforeEach
    public void setup(){

        retryTemplate = new RetryTemplate();
        BackOffPolicy backOffPolicy = new NoBackOffPolicy();
        retryTemplate.setBackOffPolicy(backOffPolicy);
        RetryPolicy retryPolicy = new NeverRetryPolicy();
        retryTemplate.setRetryPolicy(retryPolicy);

        DataHeader testDataHeader = createTestDataHeader(Instant.now());
        testEnvelope = TestDataHelper.createTestDataEnvelope(testDataHeader);
        testEnvelope.getDataBody().setId(1L);
        testEnvelope.getDataHeader().setId(1L);

        Mockito.when(configMock.getURI_HADOOP_PUSHDATA()).thenReturn(testHadoopUURI);
    }

    @Test
    public void testHadoopPublishing(){

        Mockito.when(restTemplateMock.postForEntity(testHadoopUURI,testEnvelope,HttpStatus.class)).thenReturn(new ResponseEntity<>(HttpStatus.OK) );

        HadoopWithCircuitBreakerAndRetryPublisher cbPublisher = new HadoopWithCircuitBreakerAndRetryPublisher(configMock, restTemplateMock, retryTemplate);
        TransactionAwareHadoopPublisher publisher = new TransactionAwareHadoopPublisher(cbPublisher);

        SaveAndPublishedStatus<DataEnvelope> event = new SaveAndPublishedStatus<>(testEnvelope,SAVED);
        publisher.onDataBlockCreate(event);
        assertThat( event.getStatus()).isEqualTo(SAVED_AND_PUBLISHED);
    }

    @Test
    public void testHadoopPublishingWithGatewayFailure(){

        Mockito.when(restTemplateMock.postForEntity(testHadoopUURI,testEnvelope,HttpStatus.class)).thenThrow(new RestClientException("",new HttpServerErrorException(HttpStatus.BAD_GATEWAY)));

        HadoopWithCircuitBreakerAndRetryPublisher cbPublisher = new HadoopWithCircuitBreakerAndRetryPublisher(configMock, restTemplateMock, retryTemplate);
        TransactionAwareHadoopPublisher publisher = new TransactionAwareHadoopPublisher(cbPublisher);

        SaveAndPublishedStatus<DataEnvelope> event = new SaveAndPublishedStatus<>(testEnvelope,SAVED);
        publisher.onDataBlockCreate(event);
        assertThat( event.getStatus()).isEqualTo(SAVED_NOT_PUBLISHED);
    }

}

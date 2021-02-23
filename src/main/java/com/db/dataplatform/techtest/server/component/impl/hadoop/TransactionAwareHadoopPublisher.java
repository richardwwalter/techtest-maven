package com.db.dataplatform.techtest.server.component.impl.hadoop;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.impl.SaveAndPublishedStatus;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.db.dataplatform.techtest.server.component.impl.SaveAndPublishedStatus.SaveAndPublishStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionAwareHadoopPublisher {

    private final HadoopWithCircuitBreakerAndRetryPublisher publisher;

    /**
     * Listens for committed database create transactions
     * Will be called after teh database transaction has committed but before the @Transactional method has returned
     * Publishes the data to hadoop
     * This is done as hadoop publishing can take a long time and we don't want to keep the db locked
     * This publishes to hadoop using a service that will retry several times
     * If the publish fails after repeated times we log the error
     * In practice it would better to use a persistent message queue to publish to hadoop - and publish within the transaction
     * @param data - the event data
     */
    @EventListener(SaveAndPublishedStatus.class)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDataBlockCreate(SaveAndPublishedStatus<DataEnvelope> data)  {
        try{
            publisher.publishWithRetry(data.getEntity());
            data.setStatus(SAVED_AND_PUBLISHED);
        }catch( HadoopClientException | ExhaustedRetryException ex ){
            log.error("Hadoop publishing failed",ex);
            data.setStatus(SAVED_NOT_PUBLISHED);

            /*
             At present if the hadoop service is unavailable or repeatedly fails
             this implementation will discard the hadoop update - but ensure the data is saved in teh db
             if all data must get to hadoop then some additional state will be required here
             to store the failed hadoop updates
             best done by replacing with a message queue
             but could be done using the database
             and adding necessary recovery code either realtime or batch based to feed the missing data to hadoop
            */

        }
    }

}

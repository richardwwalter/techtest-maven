package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.db.dataplatform.techtest.server.component.impl.SaveAndPublishedStatus.SaveAndPublishStatus.SAVED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private final DataBodyService dataBodyPersistenceService;
    private final DataHeaderService dataHeaderPersistenceService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * @param envelope - the envelope to save
     * @return SaveAndPublishedStatus which indicates the status of saving and hadoop publishing
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SaveAndPublishedStatus<DataEnvelope> saveDataEnvelope(DataEnvelope envelope) {

        DataEnvelope saved = persist(envelope);

        return registerHadoopTransactionPublisher( saved );
    }

    /**
     * Register for publishing to hadoop
     * this will delegate hadoop publishing until after the database transaction commits
     * This ensures that we are minimising database locking time during the transaction
     * Would prefer to publish to Hadoop using a persistent queue
     */
    private SaveAndPublishedStatus<DataEnvelope> registerHadoopTransactionPublisher(DataEnvelope data){
        SaveAndPublishedStatus<DataEnvelope> status = new SaveAndPublishedStatus<>(data, SAVED);
        eventPublisher.publishEvent(status);
        return status;
    }

    @Override
    public Optional<DataEnvelope> findBlockById(long id){
        return dataBodyPersistenceService.getDataByBlockId(id);
    }

    @Override
    public Optional<DataEnvelope> findBlockByName(String blockName){
         return dataBodyPersistenceService.getDataByBlockName(blockName);
    }

    @Override
    public List<DataEnvelope> findAllBlocksForType(BlockTypeEnum blockType) {
        return dataBodyPersistenceService.getDataByBlockType(blockType);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean updateBlockType(String blockName, BlockTypeEnum newBlockType)
    {
        return dataHeaderPersistenceService.updateBlockType(blockName,newBlockType);
    }

    private DataEnvelope persist(DataEnvelope envelope){
        return dataBodyPersistenceService.save(envelope);
    }

}

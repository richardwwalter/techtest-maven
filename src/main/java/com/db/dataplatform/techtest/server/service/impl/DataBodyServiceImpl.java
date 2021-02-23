package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.retry.support.RetrySynchronizationManager.getContext;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataBodyServiceImpl implements DataBodyService {

    private final DataStoreRepository dataStoreRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @Retryable( value = {RecoverableDataAccessException.class, TransientDataAccessException.class},  maxAttempts = 3, backoff = @Backoff(delay = 100,multiplier = 2))
    public DataEnvelope save(DataEnvelope envelope) {
        int retryCount = getContext().getRetryCount();
        if ( retryCount > 0){
            log.info("Retry {} Persisting data with attribute name: {}", retryCount, envelope.getDataHeader().getName());
        }else{
            log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
        }
        return mapToDomain(dataStoreRepository.save(mapToJpaEntity(envelope)));
    }

    @Override
    public List<DataEnvelope> getDataByBlockType(BlockTypeEnum blockType) {
        return dataStoreRepository.findByBlockType(blockType)
            .stream()
            .map(this::mapToDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<DataEnvelope> getDataByBlockName(String blockName) {
        Optional<DataBodyEntity> d = dataStoreRepository.findByBlockName(blockName);
        return d.map(this::mapToDomain);
    }

    @Override
    public Optional<DataEnvelope> getDataByBlockId(Long id) {
        Optional<DataBodyEntity> d = dataStoreRepository.findByDataStoreId(id);
        return d.map(this::mapToDomain);
    }

    private DataEnvelope mapToDomain(DataBodyEntity d){
        DataHeaderEntity dh = d.getDataHeaderEntity();
        DataHeader h = new DataHeader(dh.getName(),dh.getBlockType(),d.getDataStoreId(),dh.getCreatedTimestamp(),dh.getDataChecksum());
        DataBody b = new DataBody(d.getDataBody(),d.getDataStoreId(),d.getCreatedTimestamp());
        return new DataEnvelope(h,b);
    }

    private DataBodyEntity mapToJpaEntity(DataEnvelope e){
        DataHeaderEntity dataHeaderEntity = modelMapper.map(e.getDataHeader(), DataHeaderEntity.class);
        DataBodyEntity dataBodyEntity = modelMapper.map(e.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
        return dataBodyEntity;
    }
}


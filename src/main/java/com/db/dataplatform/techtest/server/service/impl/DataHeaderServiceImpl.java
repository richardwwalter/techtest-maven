package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
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

import java.util.Optional;

import static org.springframework.retry.support.RetrySynchronizationManager.getContext;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataHeaderServiceImpl implements DataHeaderService {

    private final DataHeaderRepository dataHeaderRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @Retryable( value = {RecoverableDataAccessException.class, TransientDataAccessException.class},  maxAttempts = 3, backoff = @Backoff(delay = 100,multiplier = 2))
    public DataHeader save(DataHeader header) {
        int retryCount = getContext().getRetryCount();
        if ( retryCount > 0){
            log.info("Retry {} Persisting header with attribute name: {}", retryCount,header.getName());
        }else{
            log.info("Persisting header with attribute name: {}", header.getName());
        }
        return mapToDomain(dataHeaderRepository.save(mapToJpaEntity(header)));
    }

    @Override
    public Optional<DataHeader> getByName(String name){
        Optional<DataHeaderEntity> h = dataHeaderRepository.findByName(name);
        return h.map(this::mapToDomain);
    }

    @Override
    @Retryable( value = {RecoverableDataAccessException.class, TransientDataAccessException.class},  maxAttempts = 3, backoff = @Backoff(delay = 100,multiplier = 2))
    public boolean updateBlockType(String name, BlockTypeEnum newBlockType){
        return dataHeaderRepository.updateBlockType(name,newBlockType) == 1;
    }

    private DataHeader mapToDomain(DataHeaderEntity d){
        return new DataHeader(d.getName(),d.getBlockType(),d.getDataHeaderId(),d.getCreatedTimestamp(),d.getDataChecksum());
    }

    private DataHeaderEntity mapToJpaEntity(DataHeader e){
        return modelMapper.map(e, DataHeaderEntity.class);
    }

}

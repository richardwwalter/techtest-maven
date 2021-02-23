package com.db.dataplatform.techtest.server.service;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

import java.util.List;
import java.util.Optional;

public interface DataBodyService {
    DataEnvelope save(DataEnvelope dataEnvelope);
    List<DataEnvelope> getDataByBlockType(BlockTypeEnum blockType);
    Optional<DataEnvelope> getDataByBlockName(String blockName);
    Optional<DataEnvelope> getDataByBlockId(Long id);
}

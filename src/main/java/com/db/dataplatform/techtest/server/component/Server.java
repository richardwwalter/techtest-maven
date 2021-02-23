package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.impl.SaveAndPublishedStatus;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

import java.util.List;
import java.util.Optional;

public interface Server {
    SaveAndPublishedStatus<DataEnvelope> saveDataEnvelope(DataEnvelope envelope);
    List<DataEnvelope> findAllBlocksForType(BlockTypeEnum blockType);
    boolean updateBlockType(String blockName, BlockTypeEnum newBlockType);
    Optional<DataEnvelope> findBlockByName(String name);
    Optional<DataEnvelope> findBlockById(long id);

}

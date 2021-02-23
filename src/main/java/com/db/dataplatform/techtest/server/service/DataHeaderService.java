package com.db.dataplatform.techtest.server.service;

import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

import java.util.Optional;

public interface DataHeaderService {
    DataHeader save(DataHeader entity);
    Optional<DataHeader> getByName(String name);


    boolean updateBlockType(String name, BlockTypeEnum newBlockType);
}

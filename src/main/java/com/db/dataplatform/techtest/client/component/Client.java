package com.db.dataplatform.techtest.client.component;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;

import java.util.List;

public interface Client {
    boolean pushData(DataEnvelope dataEnvelope);
    List<DataEnvelope> getData(String blockType);
    boolean updateData(String blockName, String newBlockType);
}

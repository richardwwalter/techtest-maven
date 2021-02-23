package com.db.dataplatform.techtest;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

import java.time.Instant;

import static com.db.dataplatform.techtest.Constant.DUMMY_DATA;

public class TestDataHelper {

    public static final String TEST_NAME = "Test";
    public static final String TEST_NAME_EMPTY = "";


    public static DataHeader createTestDataHeader(Instant timestamp) {
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA );
        dataHeader.setCreatedTimestamp(timestamp);
        return dataHeader;
    }

    public static DataHeaderEntity createTestDataHeaderEntity(Instant expectedTimestamp) {
        DataHeaderEntity dataHeaderEntity = new DataHeaderEntity();
        dataHeaderEntity.setName(TEST_NAME);
        dataHeaderEntity.setBlockType(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity.setCreatedTimestamp(expectedTimestamp);
        return dataHeaderEntity;
    }

    public static DataEnvelope createTestDataEnvelope(DataHeader dataHeader) {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        dataBody.setCreatedTimestamp(dataHeader.getCreatedTimestamp());
        return new DataEnvelope(dataHeader, dataBody );
    }

    public static DataBodyEntity createTestDataBodyEntity(DataHeaderEntity dataHeaderEntity) {
        DataBodyEntity dataBodyEntity = new DataBodyEntity();
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
        dataBodyEntity.setDataBody(DUMMY_DATA);
        dataBodyEntity.setCreatedTimestamp(dataHeaderEntity.getCreatedTimestamp());
        return dataBodyEntity;
    }

    public static DataEnvelope createTestDataEnvelopeApiObject() {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA);
        return new DataEnvelope(dataHeader, dataBody);
    }

    public static DataEnvelope createTestDataEnvelopeApiObjectWithEmptyName() {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME_EMPTY, BlockTypeEnum.BLOCKTYPEA);
        return new DataEnvelope(dataHeader, dataBody);
    }
}

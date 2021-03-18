package com.db.dataplatform.techtest.api.model;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static com.db.dataplatform.techtest.Constant.DUMMY_DATA;
import static com.db.dataplatform.techtest.TestDataHelper.TEST_NAME;
import static com.db.dataplatform.techtest.api.model.DataHeaderTests.TEST_CSUM;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DataEnvelopeTests {

    @Test
    public void assignDataFieldsShouldWorkAsExpected() {
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA);
        dataHeader.setDataChecksum(TEST_CSUM);
        DataBody dataBody = new DataBody(DUMMY_DATA);

        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);

        assertThat(dataEnvelope).isNotNull();
        assertThat(dataEnvelope.getDataHeader()).isNotNull();
        assertThat(dataEnvelope.getDataBody()).isNotNull();
        assertThat(dataEnvelope.getDataHeader()).isEqualTo(dataHeader);
        assertThat(dataEnvelope.getDataBody()).isEqualTo(dataBody);
        assertThat(dataEnvelope.getDataBody().getDataBody()).isEqualTo(DUMMY_DATA);
        assertThat(dataEnvelope.getDataHeader().getDataChecksum()).isEqualTo(TEST_CSUM);
    }

    @Test
    public void checkTwoDataEnvelopesAreEqualAsExpected() {

        DataHeader dataHeader = new DataHeader("bob",BlockTypeEnum.BLOCKTYPEA);
        dataHeader.setDataChecksum(TEST_CSUM);
        dataHeader.setCreatedTimestamp(Instant.now());
        dataHeader.setId(1L);

        DataBody dataBody = new DataBody("TEST-PAYLOAD");
        dataBody.setCreatedTimestamp(Instant.now());
        dataBody.setId(1L);

        DataEnvelope envelope = new DataEnvelope(dataHeader,dataBody);

        DataHeader dataHeader2 = new DataHeader("bob",BlockTypeEnum.BLOCKTYPEA);
        dataHeader2.setDataChecksum(TEST_CSUM);
        dataHeader2.setCreatedTimestamp(Instant.now());
        dataHeader2.setId(1L);

        DataBody dataBody2 = new DataBody("TEST-PAYLOAD");
        dataBody2.setCreatedTimestamp(Instant.now());
        dataBody2.setId(1L);

        DataEnvelope envelope2 = new DataEnvelope(dataHeader2,dataBody2);

        assertThat(envelope).isEqualTo(envelope2);
        envelope.getDataHeader().setId(-999L);
        assertThat(envelope).isNotEqualTo(envelope2);
    }

}

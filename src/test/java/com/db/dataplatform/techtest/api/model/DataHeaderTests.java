package com.db.dataplatform.techtest.api.model;

import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static com.db.dataplatform.techtest.TestDataHelper.TEST_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DataHeaderTests {

    public static final String TEST_CSUM = "cecfd3953783df706878aaec2c22aa70";

    @Test
    public void assignDataHeaderFieldsShouldWorkAsExpected() {
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA);
        dataHeader.setDataChecksum(TEST_CSUM);

        assertThat(dataHeader.getName()).isEqualTo(TEST_NAME);
        assertThat(dataHeader.getBlockType()).isEqualTo(BlockTypeEnum.BLOCKTYPEA);
        assertThat(dataHeader.getDataChecksum()).isEqualTo(TEST_CSUM);
    }

    @Test
    public void checkTwoDataHeadersAreEqualAsExpected() {

        DataHeader dataHeader = new DataHeader("bob",BlockTypeEnum.BLOCKTYPEA);
        dataHeader.setDataChecksum(TEST_CSUM);
        dataHeader.setCreatedTimestamp(Instant.now());
        dataHeader.setId(1L);

        DataHeader dataHeader2 = new DataHeader("bob",BlockTypeEnum.BLOCKTYPEA);
        dataHeader2.setDataChecksum(TEST_CSUM);
        dataHeader2.setCreatedTimestamp(Instant.now());
        dataHeader2.setId(1L);

        assertThat(dataHeader).isEqualTo(dataHeader2);
        dataHeader.setId(-999L);
        assertThat(dataHeader).isNotEqualTo(dataHeader2);
    }

}

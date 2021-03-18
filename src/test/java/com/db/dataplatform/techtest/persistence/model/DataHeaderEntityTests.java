package com.db.dataplatform.techtest.persistence.model;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static com.db.dataplatform.techtest.TestDataHelper.TEST_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DataHeaderEntityTests {

    @Test
    public void assignDataHeaderEntityFieldsShouldWorkAsExpected() {
        Instant expectedTimestamp = Instant.now();

        DataHeaderEntity dataHeaderEntity = TestDataHelper.createTestDataHeaderEntity(expectedTimestamp);

        assertThat(dataHeaderEntity.getName()).isEqualTo(TEST_NAME);
        assertThat(dataHeaderEntity.getBlockType()).isEqualTo(BlockTypeEnum.BLOCKTYPEA);
        assertThat(dataHeaderEntity.getCreatedTimestamp()).isEqualTo(expectedTimestamp);
    }

    @Test
    public void checkTwoDataHeadersAreEqualAsExpected() {

        DataHeaderEntity dataHeaderEntity1 = new DataHeaderEntity();
        dataHeaderEntity1.setName(TEST_NAME);
        dataHeaderEntity1.setBlockType(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity1.setCreatedTimestamp(Instant.now());

        DataHeaderEntity dataHeaderEntity2 = new DataHeaderEntity();
        dataHeaderEntity2.setName(TEST_NAME);
        dataHeaderEntity2.setBlockType(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity2.setCreatedTimestamp(Instant.now().plusSeconds(100L));

        assertThat(dataHeaderEntity1).isEqualTo(dataHeaderEntity2);
        dataHeaderEntity1.setDataHeaderId(-999L);
        assertThat(dataHeaderEntity1).isNotEqualTo(dataHeaderEntity2);
    }


}

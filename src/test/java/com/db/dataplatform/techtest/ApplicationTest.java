package com.db.dataplatform.techtest;

import com.db.dataplatform.techtest.client.component.Client;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.SocketUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TechTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApplicationTest {

    static{
        // find a free port to run test service on
        System.setProperty("server.port",String.valueOf(SocketUtils.findAvailableTcpPort()));
    }

    @Autowired
    private Server server;

    @Autowired
    private Client client;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testDataIsCorrect() {

        // this will run the configured push query and update on context initialisation
        List<DataEnvelope> blockBData = server.findAllBlocksForType(BlockTypeEnum.BLOCKTYPEB);
        assertThat(blockBData).isNotNull();
        assertThat(blockBData).isNotEmpty();
        assertThat(blockBData.size()).isEqualTo(1);
    }

    @Test
    public void testPush() {

        boolean pushResult = client.pushData( createTestClientEnvelopeApiObject() );
        assertThat(pushResult).isTrue();

        List<DataEnvelope> blockBData = server.findAllBlocksForType(BlockTypeEnum.BLOCKTYPEA);
        assertThat(blockBData).isNotNull();
        assertThat(blockBData).isNotEmpty();
        assertThat(blockBData.size()).isEqualTo(1);
        assertThat(blockBData.get(0).getDataHeader().getBlockType()).isEqualTo(BlockTypeEnum.BLOCKTYPEA);
    }

    private static com.db.dataplatform.techtest.client.api.model.DataEnvelope createTestClientEnvelopeApiObject() {
        com.db.dataplatform.techtest.client.api.model.DataBody dataBody = new com.db.dataplatform.techtest.client.api.model.DataBody("TEST-DATA");
        com.db.dataplatform.techtest.client.api.model.DataHeader dataHeader = new com.db.dataplatform.techtest.client.api.model.DataHeader("TEST_NAME", BlockTypeEnum.BLOCKTYPEA);
        return new com.db.dataplatform.techtest.client.api.model.DataEnvelope(dataHeader, dataBody);
    }

}

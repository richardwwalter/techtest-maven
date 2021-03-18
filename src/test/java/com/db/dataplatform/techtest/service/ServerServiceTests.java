package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.impl.SaveAndPublishedStatus;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObject;
import static com.db.dataplatform.techtest.server.component.impl.SaveAndPublishedStatus.SaveAndPublishStatus.SAVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;
    @Mock
    private DataHeaderService dataHeaderServiceImplMock;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private DataEnvelope testDataEnvelope;

    private Server server;

    @BeforeEach
    public void setup() {
        testDataEnvelope = createTestDataEnvelopeApiObject();
        server = new ServerImpl(dataBodyServiceImplMock, dataHeaderServiceImplMock, applicationEventPublisher);
        when(dataBodyServiceImplMock.save(testDataEnvelope)).thenReturn(testDataEnvelope);
    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() {
        SaveAndPublishedStatus<DataEnvelope> status = server.saveDataEnvelope(testDataEnvelope);
        assertThat(status).isNotNull();
        assertThat(status.getStatus()).isEqualTo(SAVED);
        verify(dataBodyServiceImplMock, times(1)).save(testDataEnvelope);
    }
}

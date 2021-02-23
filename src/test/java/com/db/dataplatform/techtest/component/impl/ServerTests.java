package com.db.dataplatform.techtest.component.impl;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.SaveAndPublishedStatus;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.db.dataplatform.techtest.TestDataHelper.*;
import static com.db.dataplatform.techtest.server.persistence.BlockTypeEnum.BLOCKTYPEA;
import static com.db.dataplatform.techtest.server.persistence.BlockTypeEnum.BLOCKTYPEB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ServerTests {

    private Server server;

    private DataEnvelope testEnvelope;

    @Mock
    private DataBodyService dataBodyPersistenceService;
    @Mock
    private DataHeaderService dataHeaderPersistenceService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Before
    public void setup(){
        server = new ServerImpl(dataBodyPersistenceService,dataHeaderPersistenceService,eventPublisher);
        testEnvelope = createTestDataEnvelope(createTestDataHeader(Instant.now()));

        when(dataBodyPersistenceService.save(testEnvelope)).then(i->{
            DataEnvelope e = i.getArgument(0);
            e.getDataBody().setId(1L);
            e.getDataHeader().setId(1L);
            return e;
        });

        when(dataBodyPersistenceService.getDataByBlockType(BLOCKTYPEA)).thenReturn(Arrays.asList(testEnvelope));
        when(dataBodyPersistenceService.getDataByBlockId(1L)).thenReturn(Optional.of(testEnvelope));
        when(dataBodyPersistenceService.getDataByBlockName(TEST_NAME)).thenReturn(Optional.of(testEnvelope));
        when(dataHeaderPersistenceService.updateBlockType(TEST_NAME,BLOCKTYPEB)).thenReturn(true);
    }

    @Test
    public void testSaveAndPublish(){
        SaveAndPublishedStatus<DataEnvelope> saved = server.saveDataEnvelope(testEnvelope);
        assertThat( saved ).isNotNull();
        verify(dataBodyPersistenceService, times(1)).save(testEnvelope);
        verify(eventPublisher, times(1)).publishEvent(any(SaveAndPublishedStatus.class));
    }

    @Test
    public void testUpdateBlockType(){
        boolean updated = server.updateBlockType(TEST_NAME, BLOCKTYPEB);
        assertThat( updated ).isTrue();
        verify(dataHeaderPersistenceService, times(1)).updateBlockType(TEST_NAME, BLOCKTYPEB);
    }

    @Test
    public void testFindByBlockType(){
        List<DataEnvelope> envelopes = server.findAllBlocksForType(BLOCKTYPEA);
        assertThat( envelopes ).isNotNull();
        assertThat( envelopes.isEmpty()).isFalse();
        assertThat( envelopes.size()).isEqualTo(1);
        verify(dataBodyPersistenceService, times(1)).getDataByBlockType(BLOCKTYPEA);
    }

    @Test
    public void testFindBlockById(){
        Optional<DataEnvelope> envelope = server.findBlockById(1L);
        assertThat( envelope ).isNotNull();
        assertThat(envelope.isPresent());
        verify(dataBodyPersistenceService, times(1)).getDataByBlockId(1L);
    }

    @Test
    public void testFindBlockByName(){
        Optional<DataEnvelope> envelope =  server.findBlockByName(TEST_NAME);
        assertThat( envelope ).isNotNull();
        assertThat(envelope.isPresent());
        verify(dataBodyPersistenceService, times(1)).getDataByBlockName(TEST_NAME);
    }


}

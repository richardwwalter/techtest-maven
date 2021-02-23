package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.RetryTestHelper;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.service.impl.DataBodyServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.Optional;

import static com.db.dataplatform.techtest.TestDataHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataBodyServiceTests {

    public static final String TEST_NAME_NO_RESULT = "TestNoResult";

    @Mock
    private DataStoreRepository dataStoreRepositoryMock;

    private DataBodyService dataBodyService;
    private DataBodyEntity expectedDataBodyEntity;
    private DataEnvelope testEnvelope;

    private ModelMapper modelMapper;

    private final Instant now = Instant.now();

    @Before
    public void setup() {
        DataHeaderEntity expectedDataHeaderEntity = createTestDataHeaderEntity(now);
        expectedDataBodyEntity = createTestDataBodyEntity(expectedDataHeaderEntity);
        DataHeader testDataHeader = createTestDataHeader(now);
        testEnvelope = createTestDataEnvelope(testDataHeader);
        modelMapper = new ServerMapperConfiguration().createModelMapperBean();
        dataBodyService = new DataBodyServiceImpl(dataStoreRepositoryMock,modelMapper);

        when(dataStoreRepositoryMock.save(expectedDataBodyEntity)).then(i->{
            DataBodyEntity b = i.getArgument(0);
            DataHeaderEntity hWithIds = new DataHeaderEntity();
            hWithIds.setName(b.getDataHeaderEntity().getName());
            hWithIds.setBlockType(b.getDataHeaderEntity().getBlockType());
            hWithIds.setCreatedTimestamp(b.getDataHeaderEntity().getCreatedTimestamp());
            hWithIds.setDataHeaderId(1L);
            DataBodyEntity withIds = new DataBodyEntity();
            withIds.setDataHeaderEntity(hWithIds);
            withIds.setDataBody(b.getDataBody());
            withIds.setCreatedTimestamp(b.getCreatedTimestamp());
            withIds.setDataStoreId(1L);
            return withIds;
        });

        when(dataStoreRepositoryMock.findByBlockName(TEST_NAME)).thenReturn(Optional.of(expectedDataBodyEntity));
        when(dataStoreRepositoryMock.findByDataStoreId(1L)).thenReturn(Optional.of(expectedDataBodyEntity));
    }

    @Test
    public void shouldSaveDataBodyEntityAsExpected(){
        try {
            RetryTestHelper.register();

            DataEnvelope withIds = dataBodyService.save(testEnvelope);

            assertThat(withIds).isNotNull();
            assertThat(withIds.getDataBody().getId()).isNotNull();
            assertThat(withIds.getDataHeader().getId()).isNotNull();

            verify(dataStoreRepositoryMock, times(1))
                    .save(eq(expectedDataBodyEntity));
        }finally{
            RetryTestHelper.clear();
        }
    }

    @Test
    public void testFindByName(){
        Optional<DataEnvelope> envelope = dataBodyService.getDataByBlockName(TEST_NAME);
        assertThat(envelope).isNotNull();
        assertThat(envelope.isPresent()).isTrue();
        verify(dataStoreRepositoryMock, times(1))
                .findByBlockName(eq(TEST_NAME));
    }

    @Test
    public void testFindByBlockId(){
        Optional<DataEnvelope> envelope = dataBodyService.getDataByBlockId(1L);
        assertThat(envelope).isNotNull();
        assertThat(envelope.isPresent()).isTrue();
        verify(dataStoreRepositoryMock, times(1))
                .findByDataStoreId(eq(1L));
    }

}

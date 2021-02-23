package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.RetryTestHelper;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import com.db.dataplatform.techtest.server.service.impl.DataHeaderServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.Optional;

import static com.db.dataplatform.techtest.TestDataHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DataHeaderServiceTests {

    @Mock
    private DataHeaderRepository dataHeaderRepositoryMock;

    private DataHeaderService dataHeaderService;
    private DataHeaderEntity expectedDataHeaderEntity;
    private DataHeader expectedDataHeader;

    private final Instant now = Instant.now();

    @Before
    public void setup() {

        expectedDataHeaderEntity = createTestDataHeaderEntity(now);
        expectedDataHeader = createTestDataHeader(now);
        ModelMapper modelMapper = new ServerMapperConfiguration().createModelMapperBean();
        dataHeaderService = new DataHeaderServiceImpl(dataHeaderRepositoryMock, modelMapper);

        Mockito.when(dataHeaderRepositoryMock.save(expectedDataHeaderEntity))
                .then(i->{
                    DataHeaderEntity b = i.getArgument(0);
                    DataHeaderEntity withId = new DataHeaderEntity();
                    withId.setName(b.getName());
                    withId.setBlockType(b.getBlockType());
                    withId.setCreatedTimestamp(b.getCreatedTimestamp());
                    withId.setDataHeaderId(1L);
                    return withId;
                });
        Mockito.when(dataHeaderRepositoryMock.findByName(TEST_NAME)).thenReturn(Optional.of(expectedDataHeaderEntity));
    }

    @Test
    public void shouldSaveDataHeaderEntityAsExpected(){
        try {
            RetryTestHelper.register();
            DataHeader withId = dataHeaderService.save(expectedDataHeader);
            assertThat(withId).isNotNull();
            assertThat(withId.getId()).isNotNull();
            verify(dataHeaderRepositoryMock, times(1))
                    .save(eq(expectedDataHeaderEntity));
        }finally{
            RetryTestHelper.clear();
        }
    }

    @Test
    public void testFindByName(){
        Optional<DataHeader> header = dataHeaderService.getByName(TEST_NAME);
        assertThat(header).isNotNull();
        assertThat(header.isPresent()).isTrue();
        verify(dataHeaderRepositoryMock, times(1))
                .findByName(eq(TEST_NAME));
    }

}

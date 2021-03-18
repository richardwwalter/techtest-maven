package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.ServiceConfiguration;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.component.impl.SaveAndPublishedStatus;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObject;
import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObjectWithEmptyName;
import static com.db.dataplatform.techtest.server.component.impl.SaveAndPublishedStatus.SaveAndPublishStatus.SAVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ServerControllerComponentTest {

	public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
	public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
	public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");

	@Mock(lenient = true)
	private Server serverMock;

	@Mock(lenient = true)
	private ServiceConfiguration configMock;

	private DataEnvelope testDataEnvelope;
	private ObjectMapper objectMapper;
	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {
			ServerController serverController = new ServerController(configMock, serverMock);
			mockMvc = standaloneSetup(serverController).build();
			objectMapper = Jackson2ObjectMapperBuilder
					.json()
					.build();

			testDataEnvelope = createTestDataEnvelopeApiObject();
			when(serverMock.saveDataEnvelope(any(DataEnvelope.class))).then(i -> {
				DataEnvelope b = i.getArgument(0);
				DataBody db = b.getDataBody();
				db.setCreatedTimestamp(Instant.now());
				db.setId(1L);
				b.setDataChecksum(DigestUtils.md5Hex(db.getDataBody()));
				DataHeader dh = b.getDataHeader();
				dh.setId(1L);
				dh.setCreatedTimestamp(Instant.now());
				return new SaveAndPublishedStatus<>(b, SAVED);
			});

			when(serverMock.findAllBlocksForType(any(BlockTypeEnum.class))).then(i -> {
				List<DataEnvelope> result = new ArrayList<>();
				DataEnvelope e = createTestDataEnvelopeApiObject();
				e.getDataHeader().setBlockType(i.getArgument(0));
				result.add(e);
				return result;
			});

			when(serverMock.updateBlockType("TEST_NAME", BlockTypeEnum.BLOCKTYPEB)).then(i -> true);
			when(serverMock.updateBlockType("BAD_NAME", BlockTypeEnum.BLOCKTYPEB)).then(i -> false);

			when(configMock.getURI_GETBYID()).thenReturn(new UriTemplate("http;//localhost:8090/dataserver/data/id/{id}"));
	}

	@Test
	public void a_testPushDataPostCallWorksAsExpected() throws Exception {

		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA)
				.content(testDataEnvelopeJson)
				.header("MD5_CHECKSUM", DigestUtils.md5Hex(testDataEnvelope.getDataBody().getDataBody()))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isCreated())
				.andReturn();

		boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
		assertThat(checksumPass).isTrue();
	}

	@Test
	public void b_testPushDataPostCallWithBadChecksum() throws Exception {

		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA)
				.content(testDataEnvelopeJson)
				.header("MD5_CHECKSUM", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isConflict())
				.andReturn();
	}

	@Test
	public void c_testPushDataPostCallWithHeaderMissingName() throws Exception {

		String testDataEnvelopeJson = objectMapper.writeValueAsString(createTestDataEnvelopeApiObjectWithEmptyName());

		MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA)
				.content(testDataEnvelopeJson)
				.header("MD5_CHECKSUM",  DigestUtils.md5Hex(testDataEnvelope.getDataBody().getDataBody()))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isBadRequest())
				.andReturn();

	}

	@Test
	public void d_testQueryByBlockType() throws Exception {

		MvcResult mvcResult = mockMvc.perform(get(URI_GETDATA.expand("BLOCKTYPEA"))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		assertThat(mvcResult.getResponse()).isNotNull();
		List<DataEnvelope> read = objectMapper.readValue( mvcResult.getResponse().getContentAsString() , new TypeReference<List<DataEnvelope>>(){});
		assertThat(read).isNotNull();
		assertThat(read).isNotEmpty();
		assertThat(read.size()).isEqualTo(1);
		assertThat(read.get(0).getDataHeader().getBlockType()).isEqualTo(BlockTypeEnum.BLOCKTYPEA);
	}

	@Test
	public void e_testUpdate() throws Exception {
		MvcResult mvcResult = mockMvc.perform(patch(URI_PATCHDATA.expand("TEST_NAME","BLOCKTYPEB"))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();
		assertThat(mvcResult.getResponse()).isNotNull();
		assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

		mvcResult = mockMvc.perform(get(URI_GETDATA.expand("BLOCKTYPEB"))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		assertThat(mvcResult.getResponse()).isNotNull();
		List<DataEnvelope> read = objectMapper.readValue( mvcResult.getResponse().getContentAsString() , new TypeReference<List<DataEnvelope>>(){});
		assertThat(read).isNotNull();
		assertThat(read).isNotEmpty();
		assertThat(read.size()).isEqualTo(1);
		assertThat(read.get(0).getDataHeader().getBlockType()).isEqualTo(BlockTypeEnum.BLOCKTYPEB);
	}

	@Test
	public void f_testUpdateForNonExistentName() throws Exception {
		MvcResult mvcResult = mockMvc.perform(patch(URI_PATCHDATA.expand("BAD_NAME","BLOCKTYPEB"))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNoContent())
				.andReturn();
		assertThat(mvcResult.getResponse()).isNotNull();
		assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@Test
	public void g_testUpdateForBadBlockType() throws Exception {
		MvcResult mvcResult = mockMvc.perform(patch(URI_PATCHDATA.expand("","BLOCKTYPEC"))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound())
				.andReturn();
		assertThat(mvcResult.getResponse()).isNotNull();
		assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

}

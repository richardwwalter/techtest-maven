package com.db.dataplatform.techtest;

import com.db.dataplatform.techtest.client.api.model.DataBody;
import com.db.dataplatform.techtest.client.api.model.DataHeader;
import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

import static com.db.dataplatform.techtest.Constant.DUMMY_DATA;
import static com.db.dataplatform.techtest.Constant.HEADER_NAME;

@SpringBootApplication
@EnableRetry
@EnableTransactionManagement
@Slf4j
public class TechTestApplication {

	@Autowired
	private Client client;

	public static void main(String[] args) {
		SpringApplication.run(TechTestApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initiatePushDataFlow() {
		pushData();
		queryData();
		updateData();
	}

	private void pushData() {
		DataBody dataBody = new DataBody(DUMMY_DATA);
		DataHeader dataHeader = new DataHeader(HEADER_NAME, BlockTypeEnum.BLOCKTYPEA);
		DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);

		try{
			if( !client.pushData(dataEnvelope)) {
				log.warn("Server reporting bad checksum for {}", dataEnvelope);
			}else{
				log.warn("Server reporting checksum verified and data saved");
			}
		}catch(Exception e){
			log.error("Error pushing data", e);
		}
	}

	private void queryData() {
		try {
			List<DataEnvelope> data = client.getData(BlockTypeEnum.BLOCKTYPEA.name());
			log.info("Query by block type returned {}", data);
		}catch(Exception ex){
			log.error("Error querying data", ex);
		}
	}

	private void updateData()  {
		try {
			boolean success = client.updateData(HEADER_NAME, BlockTypeEnum.BLOCKTYPEB.name());
			if (success) {
				log.info("Block name {} updated to {}", HEADER_NAME, BlockTypeEnum.BLOCKTYPEB);
			} else {
				log.warn("Failed to update block name {} updated to {}", HEADER_NAME, BlockTypeEnum.BLOCKTYPEB);
			}
		}catch(Exception ex){
			log.error("Error updating data", ex);
		}
	}


}

package com.db.dataplatform.techtest.bdd;

import com.db.dataplatform.techtest.TechTestApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.util.SocketUtils;

import java.nio.charset.StandardCharsets;

@CucumberContextConfiguration
@SpringBootTest(classes = TechTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SpringITBase {

    static ResponseResults latestResponse = null;

    static{
        // find a free port to run test service on
        System.setProperty("server.port",String.valueOf(SocketUtils.findAvailableTcpPort()));
    }

    public void executeGet(String url) throws Throwable {
        latestResponse = new ResponseResults(new MockClientHttpResponse("1.0".getBytes(StandardCharsets.UTF_8), HttpStatus.OK));
    }
}

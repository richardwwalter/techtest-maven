package com.db.dataplatform.techtest;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriTemplate;

import javax.annotation.PostConstruct;

@Configuration
@Getter
public class ServiceConfiguration {

    private String URI_PUSHDATA;
    private UriTemplate URI_GETDATA;
    private UriTemplate URI_PATCHDATA;
    private UriTemplate URI_GETBYNAME;
    private UriTemplate URI_GETBYID;

    private String URI_HADOOP_PUSHDATA;

    @Value("${server.host:localhost}")
    private String host;

    @Value("${server.port:8090}")
    private int port;

    @PostConstruct
    private void init(){
        String BASE_PATH = "http://" + host + ":" + port;
        String BASE_DATA_PATH = BASE_PATH + "/dataserver";

        URI_PUSHDATA    = BASE_DATA_PATH + "/pushdata";
        URI_GETDATA     = new UriTemplate(BASE_DATA_PATH +"/data/{blockType}");
        URI_PATCHDATA   = new UriTemplate(BASE_DATA_PATH + "/update/{name}/{newBlockType}");
        URI_GETBYNAME   = new UriTemplate(BASE_DATA_PATH + "/data/name/{name}");
        URI_GETBYID     = new UriTemplate(BASE_DATA_PATH + "/data/id/{id}");

        URI_HADOOP_PUSHDATA = BASE_PATH + "/hadoopserver/pushbigdata";
    }

}

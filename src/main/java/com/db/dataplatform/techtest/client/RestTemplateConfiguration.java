package com.db.dataplatform.techtest.client;

import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate createRestTemplate( MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter,
                                            StringHttpMessageConverter stringHttpMessageConverter){

        return new RestTemplateBuilder()
                .messageConverters( mappingJackson2HttpMessageConverter, stringHttpMessageConverter)
                .requestFactory(()->new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()))
                .build();
    }

}

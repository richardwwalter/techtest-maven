package com.db.dataplatform.techtest.api.model;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DataBodyTests {

    public static final String DUMMY_DATA = "AKCp5fU4WNWKBVvhXsbNhqk33tawri9iJUkA5o4A6YqpwvAoYjajVw8xdEw6r9796h1wEp29D";

    @Test
    public void assignDataBodyFieldsShouldWorkAsExpected() {
        DataBody dataBody = new DataBody(DUMMY_DATA);

        assertThat(dataBody).isNotNull();
        assertThat(dataBody.getDataBody()).isEqualTo(DUMMY_DATA);
    }

    @Test
    public void checkTwoDataBodiesAreEqualAsExpected() {

        DataBody dataBody = new DataBody("TEST-PAYLOAD");
        dataBody.setCreatedTimestamp(Instant.now());
        dataBody.setId(1L);

        DataBody dataBody2 = new DataBody("TEST-PAYLOAD");
        dataBody2.setCreatedTimestamp(Instant.now());
        dataBody2.setId(1L);

        assertThat(dataBody).isEqualTo(dataBody2);
        dataBody.setId(-999L);
        assertThat(dataBody).isNotEqualTo(dataBody2);
    }
}

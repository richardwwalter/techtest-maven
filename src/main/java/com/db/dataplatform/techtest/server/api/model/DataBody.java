package com.db.dataplatform.techtest.server.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@JsonSerialize(as = DataBody.class)
@JsonDeserialize(as = DataBody.class)
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class DataBody {

    @NotNull
    @NotBlank
    @NonNull
    private String dataBody;

    @Setter
    private Long id;

    @Setter
    @EqualsAndHashCode.Exclude
    private Instant createdTimestamp;

}



package com.db.dataplatform.techtest.server.api.model;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@JsonSerialize(as = DataHeader.class)
@JsonDeserialize(as = DataHeader.class)
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class DataHeader {

    @NotNull
    @NonNull
    @NotBlank
    private String name;

    @NotNull
    @NonNull
    @Setter
    private BlockTypeEnum blockType;

    @Setter
    private Long id;

    @Setter
    @EqualsAndHashCode.Exclude
    private Instant createdTimestamp;

    @Setter
    private String dataChecksum;

}
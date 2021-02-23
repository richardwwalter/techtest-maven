package com.db.dataplatform.techtest.server.api.controller;

import com.db.dataplatform.techtest.ServiceConfiguration;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.SaveAndPublishedStatus;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.*;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final ServiceConfiguration config;
    private final Server server;

    @PostMapping(value = "/pushdata", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> pushData(@NotNull @NotBlank @Size(min = 32, max=32) @RequestHeader("MD5_CHECKSUM") String md5Checksum ,
                                             @Valid @RequestBody DataEnvelope dataEnvelope) {

        log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());

        if(!verifyChecksum(dataEnvelope, md5Checksum)){
            log.error("Checksum validation failed");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(false);
        }
        dataEnvelope.setDataChecksum(md5Checksum);
        SaveAndPublishedStatus<DataEnvelope> saved = server.saveDataEnvelope(dataEnvelope);

        long id = saved.getEntity().getDataBody().getId();
        log.info("Data envelope pushed name:{} id:{} status:{}", dataEnvelope.getDataHeader().getName(), id, saved.getStatus());
        return ResponseEntity.created(config.getURI_GETBYID().expand(id)).body(true);
    }

    @GetMapping(value = "/data/{blockType}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<DataEnvelope>> findBlocksForType(@NotNull @PathVariable("blockType") BlockTypeEnum blockType) {
        log.info("Finding all blocks for type {}",blockType);
        return ResponseEntity.ok(server.findAllBlocksForType(blockType));
    }

    @GetMapping(value = "/data/id/{id}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<DataEnvelope> findBlockById(@NotNull @PathVariable("id") Long id) {

        Optional<DataEnvelope> data = server.findBlockById(id);
        return data.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/data/name/{name}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<DataEnvelope> findBlockByName(@NotNull @NotBlank @PathVariable("name") String name) {

        Optional<DataEnvelope> data = server.findBlockByName(name);
        return data.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping(value = "/update/{name}/{newBlockType}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<HttpStatus> updateBlock(@NotNull @NotBlank @Valid @Size(min = 5, max=30) @PathVariable("name") String blockName,
                                  @NotNull @Valid @PathVariable("newBlockType") BlockTypeEnum blockType) {

        log.info("Changing block name {}to type {}",blockName,blockType);

        if(!server.updateBlockType(blockName, blockType) ){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private boolean verifyChecksum(DataEnvelope data, String md5Checksum){
        return md5Checksum.equals (DigestUtils.md5Hex(data.getDataBody().getDataBody()));
    }

}

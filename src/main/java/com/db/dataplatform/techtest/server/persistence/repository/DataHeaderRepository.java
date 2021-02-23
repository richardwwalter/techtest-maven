package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataHeaderRepository extends JpaRepository<DataHeaderEntity, Long> {

    Optional<DataHeaderEntity> findByName(String blockName);

    @Modifying
    @Query("UPDATE DataHeaderEntity dh SET dh.blockType = :blockType WHERE dh.name = :name")
    int updateBlockType(@Param(value = "name") String name, @Param(value = "blockType") BlockTypeEnum blockType);

}

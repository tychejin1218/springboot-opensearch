package com.example.opensearch.sample.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.opensearch.sample.service.dto.SampleDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch.core.DeleteResponse;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class SampleServiceTest {

  @Autowired
  SampleService sampleService;

  @Autowired
  ObjectMapper objectMapper;

  @Order(1)
  @DisplayName("createIndex_인덱스 생성")
  @Test
  void testCreateIndex() {

    // Given
    String index = "sample-index";

    // When
    CreateIndexResponse createIndexResponse = sampleService.createIndex(index);
    log.debug("createIndexResponse.index : [{}], createIndexResponse.acknowledged : [{}]"
        , createIndexResponse.index(), createIndexResponse.acknowledged());

    // Then
    assertAll(
        () -> assertTrue(ObjectUtils.isNotEmpty(createIndexResponse)),
        () -> assertTrue(ObjectUtils.isNotEmpty(createIndexResponse.index())),
        () -> assertTrue(createIndexResponse.acknowledged())
    );
  }

  @Order(2)
  @DisplayName("deleteIndex_인덱스 삭제")
  @Test
  void testDeleteIndex() {

    // Given
    String index = "sample-index";

    // When
    DeleteIndexResponse deleteIndexResponse = sampleService.deleteIndex(index);
    log.debug("deleteIndexResponse.acknowledged : [{}]", deleteIndexResponse.acknowledged());

    // Then
    assertAll(
        () -> assertTrue(ObjectUtils.isNotEmpty(deleteIndexResponse)),
        () -> assertTrue(deleteIndexResponse.acknowledged())
    );
  }

  @Order(3)
  @DisplayName("insertDocument_Document 저장")
  @Test
  void testInsertDocument() {

    // Given
    String indexName = "sample-index";
    SampleDto.Document sampleDocument = SampleDto.Document.builder()
        .id("01")
        .firstName("FirstName Test")
        .lastName("LastName Test")
        .build();

    // When
    IndexResponse indexResponse = sampleService.insertDocument(
        indexName, sampleDocument);
    log.debug("indexResponse.result : [{}]", indexResponse.result());

    // Then
    assertAll(
        () -> assertTrue(ObjectUtils.isNotEmpty(indexResponse)),
        () -> assertEquals(Result.Created, indexResponse.result())
    );
  }

  @Order(4)
  @DisplayName("deleteDocument_Document 삭제")
  @Test
  void testDeleteDocument() {

    // Given
    String indexName = "sample-index";
    SampleDto.Document sampleDocument = SampleDto.Document.builder()
        .id("01")
        .build();

    // When
    DeleteResponse deleteResponse = sampleService.deleteDocument(
        indexName, sampleDocument);
    log.debug("deleteResponse.result : [{}]", deleteResponse.result());

    // Then
    assertAll(
        () -> assertTrue(ObjectUtils.isNotEmpty(deleteResponse)),
        () -> assertEquals(Result.Deleted, deleteResponse.result())
    );
  }
}

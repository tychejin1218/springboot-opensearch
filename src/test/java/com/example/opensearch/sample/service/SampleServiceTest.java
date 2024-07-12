package com.example.opensearch.sample.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.opensearch.sample.service.dto.SampleDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch.core.DeleteResponse;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.UpdateByQueryResponse;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.opensearch.client.transport.endpoints.BooleanResponse;
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

  @DisplayName("Index 샘플 테스트")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @Nested
  class TestIndex {

    static String indexName;

    @BeforeAll
    static void setUp() {
      indexName = "sample-index";
    }

    @Order(1)
    @DisplayName("createIndex_인덱스 생성")
    @Test
    void testCreateIndex() {

      // Given & When
      CreateIndexResponse createIndexResponse = sampleService.createIndex(indexName);
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
    @DisplayName("existIndex_인덱스 존재 여부 조회")
    @Test
    void testExistIndex() {

      // Given & When
      BooleanResponse booleanResponse = sampleService.existIndex(indexName);
      log.debug("booleanResponse : [{}]", booleanResponse);

      // Then
      assertAll(
          () -> assertTrue(ObjectUtils.isNotEmpty(booleanResponse)),
          () -> assertTrue(booleanResponse.value())
      );
    }

    @Order(3)
    @DisplayName("deleteIndex_인덱스 삭제")
    @Test
    void testDeleteIndex() {

      // Given & When
      DeleteIndexResponse deleteIndexResponse = sampleService.deleteIndex(indexName);
      log.debug("deleteIndexResponse.acknowledged : [{}]", deleteIndexResponse.acknowledged());

      // Then
      assertAll(
          () -> assertTrue(ObjectUtils.isNotEmpty(deleteIndexResponse)),
          () -> assertTrue(deleteIndexResponse.acknowledged())
      );
    }
  }

  @DisplayName("Index 및 Document 샘플 테스트")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @Nested
  class TestIndexAndDocument {

    static String indexName;
    static String id;
    static String originalFirstName;
    static String originalLastName;
    static String updatedFirstName;
    static String updatedLastName;

    @BeforeAll
    static void setUpAll() {
      indexName = "sample-index";
      id = "01";
      originalFirstName = "Original FirstName";
      originalLastName = "Original LastName";
      updatedFirstName = "Updated FirstName";
      updatedLastName = "Updated LastName";
    }

    @Order(1)
    @DisplayName("createIndex_인덱스 생성")
    @Test
    void testCreateIndex() {

      // Given & When
      CreateIndexResponse createIndexResponse = sampleService.createIndex(indexName);
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
    @DisplayName("insertDocument_Document 저장")
    @Test
    void testInsertDocument() throws Exception {

      // Given
      SampleDto.Document sampleDocument = SampleDto.Document.builder()
          .id(id)
          .firstName(originalFirstName)
          .lastName(originalFirstName)
          .build();

      // When
      IndexResponse indexResponse = sampleService.insertDocument(
          indexName, sampleDocument);
      log.debug("indexResponse.result : [{}]", indexResponse.result());

      Thread.sleep(1000);

      // Then
      assertAll(
          () -> assertTrue(ObjectUtils.isNotEmpty(indexResponse)),
          () -> assertEquals(Result.Created, indexResponse.result())
      );
    }

    @Order(3)
    @DisplayName("searchDocument_Document 조회")
    @Test
    void testSearchDocument() {

      // Given & When
      SearchResponse<SampleDto.Document> searchResponse = sampleService.searchDocument(indexName);
      log.debug("searchResponse.hits().hits().size() : [{}]", searchResponse.hits().hits().size());

      // Then
      assertAll(
          () -> assertTrue(ObjectUtils.isNotEmpty(searchResponse)),
          () -> assertTrue(ObjectUtils.isNotEmpty(searchResponse.hits().hits().size() > 0)),
          () -> assertEquals(id, searchResponse.hits().hits().get(0).id())
      );
    }

    @Order(4)
    @DisplayName("updateDocument_Document 수정")
    @Test
    void testUpdateDocument() {

      // Given
      SampleDto.Document sampleDocument = SampleDto.Document.builder()
          .id(id)
          .firstName(updatedFirstName)
          .lastName(updatedLastName)
          .build();

      // When
      UpdateByQueryResponse updateByQueryResponse = sampleService.updateDocument(
          indexName, sampleDocument);
      log.debug("updateByQueryResponse : [{}]", updateByQueryResponse);

      // Then
      assertAll(
          () -> assertTrue(ObjectUtils.isNotEmpty(updateByQueryResponse)),
          () -> assertEquals(1L, updateByQueryResponse.updated())
      );
    }

    @Order(5)
    @DisplayName("deleteDocument_Document 삭제")
    @Test
    void testDeleteDocument() {

      // Given
      SampleDto.Document sampleDocument = SampleDto.Document.builder()
          .id(id)
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

    @Order(6)
    @DisplayName("deleteIndex_인덱스 삭제")
    @Test
    void testDeleteIndex() {

      // Given & When
      DeleteIndexResponse deleteIndexResponse = sampleService.deleteIndex(indexName);
      log.debug("deleteIndexResponse.acknowledged : [{}]", deleteIndexResponse.acknowledged());

      // Then
      assertAll(
          () -> assertTrue(ObjectUtils.isNotEmpty(deleteIndexResponse)),
          () -> assertTrue(deleteIndexResponse.acknowledged())
      );
    }
  }
}

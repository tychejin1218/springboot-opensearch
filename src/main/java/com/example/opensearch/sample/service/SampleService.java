package com.example.opensearch.sample.service;

import com.example.opensearch.sample.service.dto.SampleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.DeleteResponse;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexRequest.Builder;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SampleService {

  private final OpenSearchClient openSearchClient;

  /**
   * Index 생성
   *
   * @param indexName 생성할 인덱스명
   * @return CreateIndexResponse Index 생성에 대한 응답을 담고 있는 객체
   */
  public CreateIndexResponse createIndex(String indexName) {

    CreateIndexResponse createIndexResponse = null;

    try {

      CreateIndexRequest createIndexRequest = new Builder()
          .index(indexName).build();
      createIndexResponse = openSearchClient.indices().create(createIndexRequest);

    } catch (Exception e) {
      log.error("createIndex indexName : [{}]", indexName, e);
    }

    return createIndexResponse;
  }

  /**
   * Index 삭제
   *
   * @param indexName 삭제할 인덱스명
   * @return DeleteIndexResponse Index 삭제에 대한 응답을 담고 있는 객체
   */
  public DeleteIndexResponse deleteIndex(String indexName) {

    DeleteIndexResponse deleteIndexResponse = null;

    try {
      DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder()
          .index(indexName).build();
      deleteIndexResponse = openSearchClient.indices().delete(deleteIndexRequest);
    } catch (Exception e) {
      log.error("deleteIndex indexName : [{}]", indexName, e);
    }

    return deleteIndexResponse;
  }

  /**
   * Document 저장
   *
   * @param indexName      Document를 저장할 인덱스명
   * @param sampleDocument 저장할 Document 정보를 담고 있는 SampleDto.Document 객체
   * @return IndexResponse Document 저장에 대한 결과를 담고 있는 객체
   */
  public IndexResponse insertDocument(String indexName, SampleDto.Document sampleDocument) {

    IndexResponse indexResponse = null;

    try {
      IndexRequest<SampleDto.Document> indexRequest = new IndexRequest.Builder<SampleDto.Document>()
          .index(indexName)
          .id(sampleDocument.getId())
          .document(sampleDocument).build();
      indexResponse = openSearchClient.index(indexRequest);
    } catch (Exception e) {
      log.error("insertDocument indexName : [{}], sampleDocument : [{}]"
          , indexName, sampleDocument.toString(), e);
    }

    return indexResponse;
  }

  /**
   * Document 삭제
   *
   * @param indexName      삭제할 Document가 있는 인덱스명
   * @param sampleDocument 삭제할 Document 정보를 담고 있는 SampleDto.Document 객체
   * @return DeleteResponse Document 삭제에 대한 결과를 담고 있는 객체
   */
  public DeleteResponse deleteDocument(String indexName, SampleDto.Document sampleDocument) {

    DeleteResponse deleteResponse = null;

    try {
      deleteResponse = openSearchClient
          .delete(d -> d.index(indexName).id(sampleDocument.getId()));
    } catch (Exception e) {
      log.error("deleteDocument indexName : [{}], sampleDocument : [{}]"
          , sampleDocument.toString(), e);
    }

    return deleteResponse;
  }
}

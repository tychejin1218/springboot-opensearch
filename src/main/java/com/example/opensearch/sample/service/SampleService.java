package com.example.opensearch.sample.service;

import com.example.opensearch.sample.service.dto.SampleDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.InlineScript;
import org.opensearch.client.opensearch.core.DeleteResponse;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.UpdateByQueryRequest;
import org.opensearch.client.opensearch.core.UpdateByQueryResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexRequest.Builder;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.opensearch.client.transport.endpoints.BooleanResponse;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SampleService {

  private final OpenSearchClient openSearchClient;

  /**
   * Index 생성
   *
   * <p>Request Body</p>
   * <p>curl -X PUT http://localhost:9200/sample-index</p>
   *
   * <p>Response Body</p>
   * <p>{"acknowledged":true,"shards_acknowledged":true,"index":"sample-index"}</p>
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
   * Index 존재 여부 조회
   *
   * <p>Request Body</p>
   * <p>curl -HEAD http://localhost:9200/sample-index</p>
   *
   * <p>Response Body</p>
   * <p>{"sample-index":{"aliases":{},"mappings":{},"settings":{...}}}}</p>
   *
   * @param indexName 조회할 인덱스명
   * @return BooleanResponse 인덱스 여부 응답을 담고 있는 객체
   */
  public BooleanResponse existIndex(String indexName) {

    BooleanResponse booleanResponse = null;
    try {
      ExistsRequest existsRequest = ExistsRequest.of(
          r -> r.index(indexName)
      );
      booleanResponse = openSearchClient.indices().exists(existsRequest);
    } catch (Exception e) {
      log.error("existIndex indexName : [{}]", indexName, e);
    }

    return booleanResponse;
  }

  /**
   * Index 삭제
   *
   * <p>Request Body</p>
   * <p>curl -X DELETE http://localhost:9200/sample-index</p>
   *
   * <p>Response Body</p>
   * <p>{"acknowledged":true}</p>
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
   * @param indexName      저장할 Document의 인덱스명
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
   * Document 조회
   *
   * @param indexName 조회할 Document의 인덱스명
   * @return Document 조회에 대한 결과를 담고 있는 객체
   */
  public SearchResponse<SampleDto.Document> searchDocument(String indexName) {
    SearchResponse<SampleDto.Document> searchResponse = null;
    try {
      SearchRequest searchRequest = SearchRequest.of(r -> r
          .index(indexName)
      );

      searchResponse =
          openSearchClient.search(searchRequest, SampleDto.Document.class);


    } catch (Exception e) {
      log.error("searchDocument indexName : [{}]", indexName, e);
    }
    return searchResponse;
  }

  /**
   * Document 수정
   *
   * @param indexName      수정할 Document의 인덱스명
   * @param sampleDocument 수정할 Document 정보를 담고 있는 SampleDto.Document 객체
   * @return UpdateByQueryResponse Document 수정에 대한 결과를 담고 있는 객체
   */
  public UpdateByQueryResponse updateDocument(String indexName, SampleDto.Document sampleDocument) {

    UpdateByQueryResponse updateByQueryResponse = null;

    try {
      UpdateByQueryRequest updateByQueryRequest = UpdateByQueryRequest.of(r -> r
          .index(indexName)
          // Document의 수정 조건 id
          .query(q -> q
              .terms(t -> t
                  .field("id")
                  .terms(v -> v
                      .value(List.of(FieldValue.of(sampleDocument.getId()))))
              ))
          // Document에서 수정할 내용 firstName, lastName
          .script(s -> s
              .inline(InlineScript.of(is -> is
                  .lang("painless")
                  .source(
                      "ctx._source.firstName = '" + sampleDocument.getFirstName() + "'; "
                          + "ctx._source.lastName = '" + sampleDocument.getLastName() + "';"))
              )
          )
      );

      updateByQueryResponse = openSearchClient.updateByQuery(updateByQueryRequest);

    } catch (Exception e) {
      log.error("updateDocument indexName : [{}], sampleDocument : [{}]",
          indexName, sampleDocument.toString(), e);
    }

    return updateByQueryResponse;
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

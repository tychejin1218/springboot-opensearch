package com.example.opensearch.sample.service;

import com.example.opensearch.sample.service.dto.ProblemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.aggregations.Aggregation;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexRequest.Builder;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProblemService {

  private final String INDEX_NAME = "problem-records";

  private final OpenSearchClient openSearchClient;

  /**
   * Index 생성
   *
   * <p>Request Body</p>
   * <p>curl -X PUT http://localhost:9200/problem-records</p>
   *
   * @return CreateIndexResponse Index 생성에 대한 응답을 담고 있는 객체
   */
  public CreateIndexResponse createIndex() {

    CreateIndexResponse createIndexResponse = null;

    try {

      CreateIndexRequest createIndexRequest = new Builder()
          .index(INDEX_NAME).build();
      createIndexResponse = openSearchClient.indices().create(createIndexRequest);

    } catch (Exception e) {
      log.error("createIndex indexName : [{}]", INDEX_NAME, e);
    }

    return createIndexResponse;
  }

  /**
   * Index 삭제
   *
   * <p>Request Body</p>
   * <p>curl -X DELETE http://localhost:9200/problem-records</p>
   *
   * @return DeleteIndexResponse Index 삭제에 대한 응답을 담고 있는 객체
   */
  public DeleteIndexResponse deleteIndex() {

    DeleteIndexResponse deleteIndexResponse = null;

    try {
      DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder()
          .index(INDEX_NAME).build();
      deleteIndexResponse = openSearchClient.indices().delete(deleteIndexRequest);
    } catch (Exception e) {
      log.error("deleteIndex indexName : [{}]", INDEX_NAME, e);
    }

    return deleteIndexResponse;
  }

  /**
   * 문항 풀이 내용 저장
   *
   * @param answerRequest 저장할 Document 정보를 담고 있는 ProblemDto.Answer 객체
   * @return IndexResponse Document 저장에 대한 결과를 담고 있는 객체
   */
  public IndexResponse insertProblemAnswer(ProblemDto.Answer answerRequest) {

    IndexResponse indexResponse = null;

    try {
      IndexRequest<ProblemDto.Answer> indexRequest = new IndexRequest.Builder<ProblemDto.Answer>()
          .index(INDEX_NAME)
          .document(answerRequest).build();
      indexResponse = openSearchClient.index(indexRequest);
    } catch (Exception e) {
      log.error("insertProblemAnswer indexName : [{}], answerRequest : [{}]"
          , INDEX_NAME, answerRequest.toString(), e);
    }

    return indexResponse;
  }

  /**
   * 배점 평균를 조회
   *
   * @return 배점 평균
   */
  public double getAvgDsscValue() {

    double avgDsscValue = 0L;

    SearchRequest searchRequest = SearchRequest.of(req -> req
        .index(INDEX_NAME)
        .aggregations(
            "avg_dsscValue",
            Aggregation.of(aggr -> aggr
                .avg(avg -> avg
                    .field("dsscValue"))
            )
        )
        .size(0)
    );

    try {

      SearchResponse<ProblemDto.Aggregation> searchResponse =
          openSearchClient.search(searchRequest, ProblemDto.Aggregation.class);
      log.debug("searchResponse : {}", searchResponse);

      if (ObjectUtils.isNotEmpty(searchResponse) &&
          !searchResponse.aggregations().isEmpty()) {
        avgDsscValue = searchResponse.aggregations().get("avg_dsscValue").avg().value();
        log.debug("avgDsscValue : {}", avgDsscValue);
      }

    } catch (Exception e) {
      log.error("getAvgDsscValue searchRequest : {}", searchRequest, e);
    }

    return avgDsscValue;
  }

  /**
   * 정답률를 조회
   *
   * @param studentId 학생 아이디
   * @return 정답률
   */
  public double getCorrectTrueRate(String studentId) {

    double correctRate = 0.0;
    double correctTotalCount;
    double correctTrueCount;

    SearchRequest searchRequest = SearchRequest.of(req -> req
        .index(INDEX_NAME)
        .query(q -> q
            .bool(b -> b
                .must(m -> m
                    .match(ma -> ma
                        .field("studentId").query(FieldValue.of(studentId))
                    )
                )
            )
        )
        .aggregations(
            "correct_total_count",
            Aggregation.of(a -> a
                .valueCount(vc -> vc
                    .field("correctYn"))

            )
        )
        .aggregations(
            "correct_true_count",
            Aggregation.of(a -> a
                .filter(f -> f
                    .term(t -> t
                        .field("correctYn").value(FieldValue.TRUE)
                    )
                )
            )
        )
        .size(0)
    );

    try {

      SearchResponse<ProblemDto.Aggregation> searchResponse =
          openSearchClient.search(searchRequest, ProblemDto.Aggregation.class);
      log.debug("searchResponse : {}", searchResponse);

      if (ObjectUtils.isNotEmpty(searchResponse) &&
          !searchResponse.aggregations().isEmpty()) {
        correctTotalCount = searchResponse.aggregations().get("correct_total_count")
            .valueCount().value();
        correctTrueCount = searchResponse.aggregations().get("correct_true_count").filter()
            .docCount();
        correctRate = (correctTrueCount / correctTotalCount) * 100;
        log.debug("correctTotalCount : [{}], correctTrueCount : [{}], correctRate : [{}]",
            correctTotalCount, correctTrueCount, correctRate);
      }

    } catch (Exception e) {
      log.error("getCorrectTrueRate searchRequest : {}", searchRequest, e);
    }

    return correctRate;
  }
}

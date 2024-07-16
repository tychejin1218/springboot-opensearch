package com.example.opensearch.sample.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.opensearch.sample.service.dto.ProblemDto.Answer;
import com.example.opensearch.sample.service.dto.ProblemDto.StudentAnswerSheet;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class ProblemServiceTest {

  @Autowired
  ProblemService problemService;

  @Autowired
  ObjectMapper objectMapper;

  @DisplayName("Index 샘플 테스트")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @Nested
  class TestIndex {

    static String indexName;

    @Order(1)
    @DisplayName("createIndex_인덱스 생성")
    @Test
    void testCreateIndex() {

      // Given & When
      CreateIndexResponse createIndexResponse = problemService.createIndex();
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

      // Given & When
      DeleteIndexResponse deleteIndexResponse = problemService.deleteIndex();
      log.debug("deleteIndexResponse.acknowledged : [{}]", deleteIndexResponse.acknowledged());

      // Then
      assertAll(
          () -> assertTrue(ObjectUtils.isNotEmpty(deleteIndexResponse)),
          () -> assertTrue(deleteIndexResponse.acknowledged())
      );
    }
  }

  @DisplayName("문항 풀이 내용 테스트")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @Nested
  class TestProblemAnswer {

    @Order(1)
    @DisplayName("insertDocument_문항 풀이 내용 샘플 저장")
    @Test
    void testInsertDocument() throws Exception {

      // Given
      AtomicInteger insertCount = new AtomicInteger();
      String indexName = "problem-records";

      List<Long> studyIds = List.of(1L, 2L, 3L, 4L);
      Long studyStructureId = 100L;
      List<String> studentIds = List.of("TEST0001", "TEST0002", "TEST0003", "TEST0003");

      List<Long> problemNo1 = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
      List<Boolean> correctYn1 = List.of(true, true, true, true, false, true, true, true, true,
          false);
      Instant startInstant1 = Instant.parse("2024-07-16T10:00:00.00Z");
      Duration duration1 = Duration.ofSeconds(20);

      List<Long> problemNo2 = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
      List<Boolean> correctYn2 = List.of(true, true, true, true, false, true, true, true, true,
          true);
      Instant startInstant2 = Instant.parse("2024-07-16T10:00:00.00Z");
      Duration duration2 = Duration.ofSeconds(25);

      List<Long> problemNo3 = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
      List<Boolean> correctYn3 = List.of(false, false, false, false, false, false, true, true,
          false,
          false);
      Instant startInstant3 = Instant.parse("2024-07-16T10:00:00.00Z");
      Duration duration3 = Duration.ofSeconds(30);

      List<Long> problemNo4 = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
      List<Boolean> correctYn4 = List.of(true, true, true, true, false, true, true, true, true,
          true);
      Instant startInstant4 = Instant.parse("2024-07-16T11:00:00.00Z");
      Duration duration4 = Duration.ofSeconds(20);

      Map<String, StudentAnswerSheet> studyDataMap = new HashMap<>();
      studyDataMap.put(studentIds.get(0) + "_1", new StudentAnswerSheet(
          studyIds.get(0), studyStructureId, problemNo1, correctYn1, startInstant1, duration1));
      studyDataMap.put(studentIds.get(1) + "_1", new StudentAnswerSheet(
          studyIds.get(1), studyStructureId, problemNo2, correctYn2, startInstant2, duration2));
      studyDataMap.put(studentIds.get(2) + "_1", new StudentAnswerSheet(
          studyIds.get(2), studyStructureId, problemNo3, correctYn3, startInstant3, duration3));
      studyDataMap.put(studentIds.get(3) + "_2", new StudentAnswerSheet(
          studyIds.get(3), studyStructureId, problemNo4, correctYn4, startInstant4, duration4));

      // When
      for (Entry<String, StudentAnswerSheet> entry : studyDataMap.entrySet()) {

        String studentId = entry.getKey();

        StudentAnswerSheet studentAnswerSheet = entry.getValue();
        Instant startInstant = studentAnswerSheet.getStartInstant();
        Duration duration = studentAnswerSheet.getDuration();

        List<Long> problemNos = studentAnswerSheet.getProblemNos();
        for (int i = 0; i < problemNos.size(); i++) {
          Answer answerReqeust = Answer.builder()
              .studyId(studentAnswerSheet.getStudyId())
              .studyStructureId(studentAnswerSheet.getStudyStructureId())
              .studentId(studentId.split("_")[0])
              .problemNo(problemNos.get(i))
              .correctYn(studentAnswerSheet.getCorrectYns().get(i))
              .problemStartDtm(startInstant.toString())
              .problemEndDtm(startInstant.plus(duration).toString())
              .dsscValue(studentAnswerSheet.getCorrectYns().get(i) ? 10L : 0L)
              .build();

          IndexResponse indexResponse = problemService.insertProblemAnswer(answerReqeust);
          if (indexResponse != null) {
            startInstant = startInstant.plus(duration);
            insertCount.incrementAndGet();
          }
        }
      }

      Thread.sleep(1000);

      // Then
      assertAll(
          () -> assertEquals(40, insertCount.get())
      );
    }

    @Order(2)
    @DisplayName("getAvgDsscValue_배점 평균를 평균 조회")
    @Test
    void testGetAvgDsscValue() {

      // Given & When
      double avgDsscValue = problemService.getAvgDsscValue();

      // Then
      assertAll(
          () -> assertNotEquals(0L, avgDsscValue)
      );
    }

    @Order(3)
    @DisplayName("getCorrectTrueRate_정답률를 조회")
    @Test
    void testGetCorrectTrueRate는() {

      // Given
      String studentId = "TEST0001";

      // When
      double avgDsscValue = problemService.getCorrectTrueRate(studentId);

      // Then
      assertAll(
          () -> assertNotEquals(0.0, avgDsscValue)
      );
    }
  }
}

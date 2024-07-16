package com.example.opensearch.sample.service.dto;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class ProblemDto {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  public static class Answer {

    private Long studyId;
    private Long studyStructureId;
    private String studentId;
    private Long problemNo;
    private Boolean correctYn;
    private String problemStartDtm;
    private String problemEndDtm;
    private Long dsscValue;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  public static class StudentAnswerSheet {

    private Long studyId;
    private Long studyStructureId;
    private List<Long> problemNos;
    private List<Boolean> correctYns;
    private Instant startInstant;
    private Duration duration;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  public static class Aggregation {

    private Long avgDsscValue;

    private int correctTotal;
    private int correctTrue;
  }
}

package com.example.opensearch.sample.service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


public class SampleDto {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  public static class Document {

    private String id;
    private String firstName;
    private String lastName;
  }
}

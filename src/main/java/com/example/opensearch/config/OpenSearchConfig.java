package com.example.opensearch.config;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchConfig {

  private static final String SCHEME = "http";
  private static final String HOST = "localhost";
  private static final int PORT = 9200;
  private static final String USERNAME = "admin";
  private static final String PASSWORD = "admin";

  /**
   * OpenSearchClient Bean 설정
   *
   * @return OpenSearchClient
   */
  @Bean
  public OpenSearchClient openSearchClient() {

    final HttpHost httpHost = new HttpHost(SCHEME, HOST, PORT);

    // 인증 정보를 설정
    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(new AuthScope(httpHost),
        new UsernamePasswordCredentials(USERNAME, PASSWORD.toCharArray()));

    // OpenSearch와 통신하기 위한 OpenSearchTransport 객체를 생성
    final OpenSearchTransport transport =
        ApacheHttpClient5TransportBuilder.builder(httpHost)
            .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                .setDefaultCredentialsProvider(credentialsProvider)).build();

    return new OpenSearchClient(transport);
  }
}

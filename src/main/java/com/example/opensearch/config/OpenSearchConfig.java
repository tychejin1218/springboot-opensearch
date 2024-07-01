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

  private static final String SCHEME = "https";
  private static final String HOST = "";
  private static final int PORT = 443;
  private static final String USERNAME = "";
  private static final String PASSWORD = "";

  /**
   * OpenSearchClient Bean 설정
   *
   * @return OpenSearchClient
   */
  @Bean
  public OpenSearchClient openSearchClient() {

    final HttpHost httpHost = new HttpHost(SCHEME, HOST, PORT);

    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(new AuthScope(httpHost),
        new UsernamePasswordCredentials(USERNAME, PASSWORD.toCharArray()));

    final OpenSearchTransport transport =
        ApacheHttpClient5TransportBuilder.builder(httpHost)
            .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                .setDefaultCredentialsProvider(credentialsProvider)).build();

    return new OpenSearchClient(transport);
  }
}

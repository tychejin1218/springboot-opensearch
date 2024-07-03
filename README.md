## [Spring Boot] Spring Boot와 OpenSearch Client 연동
- #### 개요
  - #### Spring Boot와 OpenSearch Client를 연동하고, OpenSearch 서버와 통신하여 데이터를 저장, 삭제, 조회하는 등의 개발 환경을 구축

- #### 개발 환경
  - #### IDE(IntelliJ IDEA 2023.3.4)
  - #### JDK 17
  - #### Spring Boot 3.3.1
  - #### Gradle 8.8

- #### 참고
  - #### Java Client
    - #### https://opensearch.org/docs/latest/clients/java/

- #### 1.Docker를 사용하여 OpenSearch 설치 및 실행
  - #### 1_1. OpenSearch 설치
    - #### Docker를 사용하여 로컬에 Opensearch를 설치하세요.
    - #### docker pull opensearchproject/opensearch:1.3.17
    - #### docker pull opensearchproject/opensearch-dashboards:1.3.17
  - #### 1_2. OpenSearch 실행
    - #### OpenSearch Docker 이미지를 다음의 명령어를 실행하여 로컬에서 실행하세요.
    - #### docker run -p 9200:9200 -p 9600:9600 -e "discovery.type=single-node" -e DISABLE_SECURITY_PLUGIN=true opensearchproject/opensearch:1.3.17
  - #### 1_3. OpenSearch 실행 상태 확인
    - #### OpenSearch가 정상적으로 실행되고 있는지 확인할 수 있습니다. 아래의 명령어를 실행하여 OpenSearch 인스턴스, 노드 및 플러그인 상태를 확인하세요.
    - #### curl -XGET http://localhost:9200 -u 'admin:admin' --insecure
    - #### curl -X GET http://localhost:9200/_cat/nodes?v -u 'admin:admin' --insecure
    - #### curl -XGET http://localhost:9200/_cat/plugins?v -u 'admin:admin' --insecure

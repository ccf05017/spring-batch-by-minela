# 05. Spring Batch Meta Data
## 왜 Meta data를 사용할까?
- `상태 관리`를 통한 배치 잡 `오류 처리`
    - 해피 패스를 다루는 건 아주 간단하고, 굳이 프레임워크가 필요없는 경우가 많다.
    - 오류를 잘 다루는 건 복잡하고, 이를 위해 상태관리는 필수적이다.
    - 스프링 배치의 meta data를 통해 이를 좀 더 쉽게 달성할 수 있다.
- 배치 잡 `모니터링`

## JobRepository?
- JobRepository 인터페이스 그 자체를 의미한다.
- 혹은 JobRepository 인터페이스 구현체로 배치 잡 메타데이터 데이터 저장소를 의미하기도 한다.
- 상황에 따라 RDB에 구성할 수도 있고 인메모리 DB(보통 테스트용)에 구성할 수도 있다.
- JobInstance, JobExecutionContext, Param, StepExecutionContext 등의 모든 메타 데이터가 저장되고 관리된다.

## 배치 인프라 구성하기
- @EnableBatchProcessing 어노테이션으로 모든 JobRepository의 요소를 자동으로 구성할 수 있다.
- 하지만 늘 그렇듯 세밀하게 상황에 맞게 조정해야 되는 경우가 있다. 스프링 배치는 이를 위한 방안도 제공한다.
- BatchConfigurer 인터페이스
  - @EnableBatchProcessing 어노테이션이 활성화 되면, 스프링은 BatchConfigurer 인터페이스를 통해 배치 빈을 생성하고 이를 스프링 빈에 등록한다.
  - 즉, BatchConfigurer 인터페이스를 상속받으면 JobRepository 설정에 관여할 수 있다.
  - BatchConfigurer 인터페이스는 다음과 같은 메서드들을 제공한다.
    - getJobRepository, getJobLauncher: 배치 실행에 필요한 JobRepository, JobLauncher를 불러온다.
    - getTransactionManager: 배치에서 사용할 플랫폼 트랜잭션 매니저를 불러온다.
    - getJobExplorer: JobRepository의 데이터를 읽기 전용으로 다룰 탐색기를 불러온다.
  - 일반적으로 모든 메서드를 구현하지는 않고, 적당히 몇가지의 설정만 오버라이딩해서 사용한다.
    - `DefaultBatchConfigurer`를 상속해서 설정을 오버라이딩하는 게 일반적인 사용방법이다.
- JobRepository 커스터마이징
  - JobRepository는 JobRepositoryFactoryBean을 통해 생성된다.
  - 이를 이용해서 DefaultBatchConfigurer를 상속받고 createJobRepository를 오버라이딩해서 원하는 JobRepository 설정을 적용할 수 있다.
  - 적용 가능한 속성은 다음과 같다
    - Sql Clob, ExecutionContext Serialization 방식, oracle lob, 시스템 메시지 최대 길이, datasource, 데이터베이스 타입, 테이블 prefix 등
    - 필요한 부분을 때에 따라 찾아서 설정하자.
- TransactionManager 커스터마이징
  - DefaultBatchConfigurer에서 getTransactionManager를 오버라이딩해서 사용할 수 있다.
  - 일반적으로 배치 어플리케이션은 TransactionManager가 없으면, datasource 설정과정에서 datasource에 맞는 트랜잭션 매니저를 생성한다.
- JobExplorer 커스터마이징
  - JobRepository의 데이터를 읽기 전용으로만 접근할 수 있는 탐색기 설정을 따로 할 수 있다.
  - 읽기 전용 여부만 다를 뿐 JobRepository와 동일한 데이터소스를 사용하기 때문에 JobRepository를 오버라이딩 했다면 무조건 같이 오버라이딩 해주는 게 좋다.
- 데이터베이스 구성
  - 일반적으로 스프링 설정파일로 설정한다.
  - spring.datasource 속성에 다른 일반적인 데이터소스처럼 설정하면 배치 잡도 해당 데이터소스를 사용한다.
  - batch.initialize-schema 속성을 통해 배치 잡 실행 시 메타 데이터 DDL을 어떻게 다룰지 결정할 수 있다.
  
## 잡 메타데이터 사용하기
- 메타 데이터 사용은 가능한 JobExplorer 사용을 권장한다.(훨씬 안전하다)
- 또한 JobExplorer는 JobRepository를 거치지 않고 바로 DB에 접근한다.
  - 내부적으로 사용하는 DAO는 같다.
- 또한 JobExplorer는 편의성을 위해 여러가지 조회 메서드를 제공한다.
  - 모두 이름에 맞게 배치 메타 데이터를 조회한다.
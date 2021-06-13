# 스프링배치 완벽 가이드 실습
## 개요
- 2021년 기준 스프링배치 프로젝트 리더인 마이클 미넬라의 '스프링배치 완벽 가이드' 번역본을 읽고 실습한 자료

## `simple-batch` 브랜치
- Tasklet으로 구성된 가장 간단한 배치잡 실행
- Tasklet?
    - `RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)` 인터페이스를 갖고 있다.
      - StepContribution: 아직 커밋되지 않은 현재 트랜잭션에 대한 정보(쓰기수, 읽기수 등)을 갖고 있다.
      - ChunkContext: 실행 시점의 Job 정보를 나타내며, 처리 중인 Chunk 정보도 갖고 있다.
    - Step의 최소 실행 단위로 execute 메서드를 반복해서 실행한다.

## `user-stroy` 브랜치
- 유저 스토리 관점에서 향후 예제 작성이 진행될 예정이다
  - 자세한 유저스토리 내용들은 별도의 학습이 필요한 부분으로 생략
- 예제 어플리케이션의 목표는 은행 정산 처리
- 예제 어플리케이션 잡의 흐름
  1. 고객 데이터 가져오기
     - 플랫 파일에서 고객 데이터를 읽어온다.
     - 레토드 유형에 따라 알맞은 데이터 갱신을 진행한다.
  2. 거래 정보 데이터 가져오기
     - XML 파일로 데이터를 읽은 뒤 DB에 기록한다.
  3. 현재 잔액 계산하기
     - 드라이빙 쿼리 패턴(?)을 사용해서 잔액을 계산한다. 
  4. 월별 고객 거래명세서 작성하기
     - 여기서도 드라이빙 쿼리 패턴을 사용한다.
     - 고객의 거래명세서 인쇄용 파일을 계좌당 하나씩 생성한다.
- 예제 어플리케이션은 네개의 엔티티(테이블)로 구성된다.
  - Customer: 고객 개인정보 
  - Account: 고객 게좌정보
  - CustomerAccount: 연관관계 매핑 테이블(조인 테이블)
  - Transaction: 계좌에서 발생한 거래 정보

## `job_and_step` 브랜치
- Job, Step의 전반적인 내용을 설명하고 실습한다.
- Job의 실행 방법
  - 아래 두가지 구현체로 실행할 수 있다.
    - CommandLineJobRunner: 일반적인 커맨드 라인 툴처럼 실행한다.
    - JobRegistryBackgroundJobRunner: Spring 어플리케이션 백그라운드에서 대기하다가 Quartz, 스프링 스케줄러 등으로부터 명령을 받고 실행된다.
  - 스프링부트에서는 별도로 JobLauncherCommandLineRunner를 사용한다.
    - 별도 설정이 없다면 해당 어플리케이션 컨텍스트에 빈으로 등록된 모든 Job을 동시에 실행시킨다.
  - 두가지 Runner의 실행이 같더라도 JobRunner 인터페이스를 별도로 갖고 있지 않다.
  - 실제 단일 실행점은 JobLauncher로 두가지 Runner 모두 내부적으로 이 구현체를 사용한다.
    - 추가로 JobLauncher는 스프링의 TaskExecutor를 사용해서 배치를 실행시킨다.
- Job
  - 배치에서 스텝을 어떻게 구성하고 어떤 순서로 구성할지 정한다.
  - 일종의 청사진. 클래스와 같은 역할을 수행한다.
- Job Instance
  - Job의 논리적 실행을 나타낸다.
  - JobRepository가 관리하는 JOB_INSTANCE 테이블에서 관리돤디.
  - Instance의 식별자 = Job Name + Job Param
    - 실제로 JOB_INSTANCE 테이블의 key는 Job 이름과 Job Param의 해쉬값이다.
  - 한번 성공적으로 실행된 Job Instance는 절대 다시 실행할 수 없다.
- Job Execution
    - 실제 Job의 실행 시도를 나타낸다.
    - BATCH_JOB_EXECUTION 테이블에서 관리된다.
    - 실패한 Job Instance를 재실행하면 Job Execution이 새로 생긴다.
    - JobExecution 실행 동안의 상태 정보는 BATCH_JOB_EXECUTION_CONTEXT 테이블에서 관리된다.
- 어플리케이션 설정
    - 설정파일 참조
    - @EnableBatchProcessing 어노테이션 선언 시 배치에 필요한 인프라 코드들이 컨텍스트에 추가된다.
        - 해당 어토테이션은 자동으로 구성 어노테이션으로 판단한다(=@Configuration 어노테이션 필요 없다.)
- 잡 파라미터
    - 잡에 필요한 정보 전달, Job의 구분자 구성요소 두가지 역할을 수행한다.
    - BATCH_JOB_EXECUTION_PARAMS 테이블에서 관리된다.
    - 커맨드라인에서 잡 파라미터 전달 시 ${key}=${value} 형태로 전달한다.
        - ex) java -jar demo.jar name=poppo
        - 스프링 배치 프로퍼티 전달하듯이 `-` 붙이면 안된다.
        - 배치 어플리케이션의 `-`는 구분자로 사용하지 않는 잡 파라미터를 의미한다. (순수값 전달 목적)
      - 코드 상에서 실제 값은 Map<T, JobParameter> 타입으로 전달된다.
        - Key, Value 형태는 맞지만 Value가 JobParameter로 한번 더 감싸져있다.
        - 이 덕분에 커맨드라인에서 전달할 때 값의 타입을 명시할 수 있다.
            - ex) java -jar executionDate(date)=2020/12/27
            - 배치에서 지원하는 타입 종류는 공식 문서 참조
    - 접근 방법?
        - Tasklet의 경우 ChunkContext 내의 StepContext를 타고 접근할 수 있다.
        - 좀 더 일반적인 방법은 스코프를 통해 Spring의 Lazy Binding을 사용하는 방안이다.
            - @Value 어노테이션을 활용한다.
              - 사용예: @Value("#{jobParameters['${사용할 파라미터 key명}']}") ${타입} ${변수명}
            - 사용시 @Scope 어노테이션 선언이 병행되야 한다.
              - Scope 어노테이션은 해당 Scope의 실행 범위 전까지 빈 생성을 지연시킨다.
              - Job, Custom(Step 포함)을 범위로 지정할 수 있다.
              - 테스트 시 Scope 어노테이션을 제대로 잡아주지 않으면 테스트 데이터가 들어가기 전에 배치가 실행되는 비극이 발생할 수 있다.
            - 예제에서는 이 방법을 사용한다.
    - 유효성 검증
        - JobParametersValidator 인터페이스으로 유효성 검증 기능을 구현할 수 있다.
        - 기본적인 유효성 검증 수단으로 DefaultJobParametersValidator 구현체를 제공한다.
            - 단순히 JobParameter가 전달됐는지만 확인한다.
            - Optional 하게 전달받을 수 있는 JobParameter도 지정할 수 있다.
        - 유효성 검증은 사용하고자 하는 JobParametersValidator 구현체를 Bean으로 등록해서 사용하면 된다.
          - 당연히 사용할 Job에도 세팅해줘야 한다.
        - 여러개의 유효성 검증을 사용하고자 한다면, CompositeJobParametersValidator를 Bean으로 등록하면 된다. 
    - 자동으로 생성되는 잡 파라미터
        - 잡 파라미터는 식별자로도 사용되기 때문에 매 실행마다 바꿔주는 귀찮은 작업이 필수적이다.
        - 이 불편함을 해소하기 위해 배치 잡이 실행될 때마다 새로운 값을 잡 파라미터로 전달해주는 기능을 구현할 수 있다.
        - JobParametersIncrementer 인터페이스를 구현해서 원하는 증가 로직을 직접 구현할 수 있다.
        - 물론 간단하게 RunIdIncrementer 구현체를 사용해도 된다.
        - 주의사항
            - 유효성 검증 기능을 사용할 경우 자동으로 생성되는 파라미터도 유효성 검증 범위에 추가해야 한다.
            - 기존에 사용중이던 배치 잡 파라미터를 바꾸면 Job Instance 식별자 판단에서 오류가 생길 수 있다.
                - 잡 파라미터를 바꿨다면 꼭 실행 여부를 확인하고 배포하자
    

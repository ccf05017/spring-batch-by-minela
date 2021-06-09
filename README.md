# 스프링배치 완벽 가이드 실습
## 개요
- 2021년 기준 스프링배치 프로젝트 리더인 마이클 미넬라의 '스프링배치 완벽 가이드' 번역본을 읽고 실습한 자료

## `simple-batch` 브랜치
- Tasklet으로 구성된 가장 간단한 배치잡 실행
- Tasklet?
    - `RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)` 인터페이스를 갖고 있다.
    - Step의 최소 실행 단위로 execute 메서드를 반복해서 실행한다.

# 오늘 하루를 그려줘 - 백엔드 서버 ( 프로그라피 8기 4팀 ) 🛠

## 0. 현재 상태

현재 MVP 버전을 개발하고 있습니다.

## 1. 주요 스택

- spring boot:2.7.6
- jdk:11
- mysql:8.0

## 2. 서버 아키텍처 및 배포 파이프라인

> 첨부 예정

## 3. 커밋 메시지

커밋 메시지 형식은 아래와 같습니다. `subject`까지만 작성해도 괜찮습니다.

```
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

## 3.1 커밋 타입(`<Type>`)

- `feat` : (feature)
- `fix` : (bug fix)
- `docs`: (documentation)
- `style` : (formatting, missing semi colons, …)
- `refactor`
- `test` : (when adding missing tests)
- `chore` : (maintain)

## 4. 패키지 구조

- `draw_my_today`

  - `domain`
    - `도메인명`
      - `adapter`
        - `in.web` : 웹 어댑터.
        - `out` : 예외 등
      - `application` :
        - `port`
          - `in` : Query, DTO(Request, Response), UseCase
          - `out`
        - `service`
        - `domain`
  - `global`
    - `common`
    - `config`
    - `util`

- Web Adapter
  - 컨트롤러 등이 속한다.
  - HTTP 요청을 자바 객체로 매핑한다.
  - 권한을 검사한다.
  - 입력에 대해 유효성 검사를 한다.
  - UseCase를 호출한다.
  - HTTP의 응답을 반환한다.
- Persistence Adapter
  - out port의 구현체, Repository 등이 속한다.
  - 데이터베이스에 쿼리를 날리고 쿼리 결과를 받아온다.
  - DB의 출력을 어플리케이션 포맷으로 매핑한다.
  - 출력을 반환한다.
  - 포트 인터페이스를 통해 입력을 받을 수 있다.
- UseCase
  - 비즈니스 규칙을 검증한다.
  - 모델의 상태를 조작한다.
  - 출력을 반환한다.
  - 웹으로부터 입력을 전달받는다.
  - 인터페이스로, Service가 이를 구현한다.
- Query
  - UseCase와 수행하는 역할은 동일하나, 조회 전용 작업은 Query로 분류한다.
  - UseCase는 쓰기도 가능한 일반적인 작업이다.
- Port
  - 내부 비즈니스 영역을 외부 영역에 노출한 API
  - 인바운드 포트 : 내부 영역 사용을 위해 노출된 API. 인터페이스이며, Service가 이를 구현한다.
  - 아웃바운드 포트 : 내부 영역이 외부 영역을 사용하기 위한 API. 인터페이스이며, Persistence Adapter가 이를 구현한다.
- Adapter
  - 외부 세계와 포트 간 교환을 조정한다.
  - 인바운드 어댑터 : 외부 애플리케이션/서비스와 내부 비즈니스 영역(인바운드 포트) 간 데이터 교환을 조정한다.
  - 아웃바운드 어댑터 : 내부 비즈니스 영역(아웃바운드 포트)과 외부 애플리케이션/서비스 간 데이터 교환을 조정한다.

## 5. 인텔리제이 자동 재시작 설정

1. 설정 > 빌드, 실행, 배포 > 컴파일러 > 프로젝트 자동 빌드 체크
2. 고급 설정 > 컴파일러 > 개발된 애플리케이션이 현재 실행 중인 경우에도 auto-make가 시작되도록 허용 체크

## 6. 실행

### 6.1 데이터베이스 실행 :

```shell
# mysql 실행
$ cd docker
$ docker-compose up --build
```

```shell
# mysql 종료
$ cd docker
$ docker-compose down
```

### 6.2 profile 환경변수 설정 :

1. 인텔리제이 메뉴 Run > Edit Configurations 설정 접속 혹은 스크린 샷과 같이 접속
   <img width="863" alt="스크린샷 2023-01-07 오후 4 06 05" src="https://user-images.githubusercontent.com/42285463/211137975-87d0e79c-7f8b-4640-9eae-0ad03d68fef5.png">
2. Active profiles에 develop 입력 ( 개발용의 경우 develop, production의 경우 prod 입력 )
   <img width="1042" alt="스크린샷 2023-01-07 오후 4 07 21" src="https://user-images.githubusercontent.com/42285463/211138359-e071c6ff-6fa5-432e-87e0-101e759b6037.png">

### 6.3 인텔리제이에 환경변수 추가 :

1. 인텔리제이 메뉴 Run > Edit Configurations 설정 접속 ( profile 환경변수 설정 섹션 참고 )
2. Modify Options > Environment Variables 체크
3. 해당하는 환경변수 추가

### 6.4 테스트 시 환경변수를 템플릿으로 설정 :

테스트를 수행할 때마다, 환경변수를 설정하는 작업을, 인텔리제이에서 템플릿으로 설정하면 테스트마다 설정하지 않아도 됩니다.

1. 인텔리제이 메뉴 Run > Edit Configurations 설정 접속 혹은 스크린 샷과 같이 접속
   <img width="863" alt="스크린샷 2023-01-07 오후 4 06 05" src="https://user-images.githubusercontent.com/42285463/211137975-87d0e79c-7f8b-4640-9eae-0ad03d68fef5.png">
2. 좌측 하단 Edit configuration templates 클릭
   ![스크린샷 2023-02-08 오후 4 34 00](https://user-images.githubusercontent.com/42285463/217463831-b6ff6405-ffd6-4783-83c4-30db0bdfa8f0.png)
3. JUnit을 선택한 후, 테스트 실행시와 동일한 설정값을 입력하고, 환경변수 입력한다. 그리고 테스트 설정을 설정할 범위를 'All in directory'에 명세한다.
   ![스크린샷 2023-02-08 오후 4 34 47](https://user-images.githubusercontent.com/42285463/217464007-55927e00-94db-41bb-b1f7-56de17f3358e.png)

## 7. 배포

## 8. 기여자

| Avatar                                                                                         | Name               | Team               |
| ---------------------------------------------------------------------------------------------- | ------------------ | ------------------ |
| <img src="https://avatars.githubusercontent.com/u/42285463?v=4" width="100px" height="100px"/> | 마민지(@akalswl14) | 프로그라피 8기 4팀 |
| <img src="https://avatars.githubusercontent.com/u/39454230?v=4" width="100px" height="100px"/> | 최혁(@choihuk)     | 프로그라피 8기 4팀 |

## 9. 참고사항

EC2가 아닌 로컬에서 실행할 때, `com.amazonaws.SdkClientException: Failed to connect to service endpoint:` 해결법

1. 해당 구문은 EC2 메타데이터를 읽다가 이슈가 발생한 것으로 EC2 인스턴스가 아닌 환경에서 실행할 때에는 의미 없는 에러임
2. 인텔리제이 메뉴 Run > Edit Configurations 설정 접속 ( profile 환경변수 설정 섹션 참고 )
3. Modify Options > VM Options 체크
4. VM Options 섹션에 `-Dcom.amazonaws.sdk.disableEc2Metadata=true` 추가
5. 설정을 완료한 뒤, Springboot을 실행하면 `EC2 Instance Metadata Service is disabled` 라는 구문이 뜨면서 EC2 메타데이터 서비스를
   제외하고 실행할 수 있음

# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* 요청을 일이리 파싱하는 것은 고된 작업이다.

### 요구사항 2 - get 방식으로 회원가입
* get으로 회원가입을?

### 요구사항 3 - post 방식으로 회원가입
* 여윽시 회원가입은 post방식으로 해야지.
* 그런데 왜 requestbody 읽을 때, 기존방식으로 읽으면 안되는건가..
* readLine이 아니라, read로 읽어야하는 걸까.
* request body는 line이 없나.
* 즉, read() 범위로 값을 꺼내와야 하는 듯.
* HTTP 완벽 가이드. 일어볼까함.

### 요구사항 4 - redirect 방식으로 이동
* http status code 이용
* status code 302와 location header로 화면 이동.
* 실은 재요청.

### 요구사항 5 - cookie
* setCookie
* path=/ 추가할 것.
* path 하위로 다 먹기때문에, logined같은 경우는 /로 전체에 적용

### 요구사항 6 - stylesheet 적용
* css 요청 여부 파악하려다보니
* request header 추출하는 로직을 조금 손보게 됨.

### heroku 서버에 배포 후
* 

### slf4j
* log message에서 불필요한 + 연산을 제거하기 위해서 나온것이 {}
* ex) log.debug("log test {}, test2 {}", getId(), getName());
* 로그레벨에 따라 메세지 + 연산을 수행 여부를 판단한다.
* 즉, 이렇게 사용하자.


### get과 put
* get은 서버에 데이터를 조회, 데이터의 상태를 변경하지 않는다.
* post는 데이터의 상태를 변경하는 작업
*
* 그러면, 데이터 조회시 조회 수 증가의 경우는????
* 한번 더 호출해야 하는가?

### headers를 미리 setting 하지 말고 사용할 때 쓰자.
* content-Length는 post 요청에서만 사용하면 되고,

### refactoring...
* httpRequest와 httpResponse class 생성해서 기능 정리중
* test code 추가
* commit history 확인중
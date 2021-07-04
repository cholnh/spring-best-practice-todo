# 마이크로 서비스 내부 아키텍처 코드 스타일

<br/><br/>


## :speech_balloon: 개요

해당 프로젝트는 상위 프로젝트([배달 서비스 플랫폼 API 서버 가이드](https://github.com/cholnh/delivery-platform-server-guide#배달-서비스-플랫폼-api-서버-가이드))
에서 다루는 내용의 예시이며, 마이크로 서비스를 구성하는 여러 서비스 중 자바로 구현된 프로젝트의 내부 아키텍처 코드 스타일에 대해 다룹니다.

<br/><br/>


## :memo: Table of Contents

- [아키텍처 및 Directory 스타일 가이드](https://github.com/cholnh/spring-best-practice-todo/blob/master/contents/guide-directory.md#아키텍처-및-directory-스타일-가이드)
- [Domain Model 및 범위 가이드](https://github.com/cholnh/spring-best-practice-todo/blob/master/contents/guide-domain.md#domain-model-및-범위-가이드)
- [Service 스타일 가이드](https://github.com/cholnh/spring-best-practice-todo/blob/master/contents/guide-service.md#service-스타일-가이드)
- [DAO 스타일 가이드](https://github.com/cholnh/spring-best-practice-todo/blob/master/contents/guide-dao.md#dao-스타일-가이드)
- [Test 스타일 가이드](https://github.com/cholnh/spring-best-practice-todo/blob/master/contents/guide-test.md#test-스타일-가이드)
- [Exception 스타일 가이드](https://github.com/cholnh/spring-best-practice-todo/blob/master/contents/guide-exception.md#exception-스타일-가이드)

<br/><br/>

## :monocle_face: 참고

- [cheese10yun/spring-guide](https://github.com/cheese10yun/spring-guide) 참고
- 도메인 주도 설계로 시작하는 마이크로서비스 개발 (한정헌, 유해식, 최은정, 이주영 저)
- 테스트 주도 개발로 배우는 객체 지향 설계와 실천 (Steve Freeman, Nat Pryce 저)
- Clean Code (Robert C. Martin 저)
- Mastering Spring 5.0 (Ranga Rao Karanam 저)
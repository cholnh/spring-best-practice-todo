# Test 스타일 가이드
<br/><br/>



## :speech_balloon: 개요

해당 프로젝트에서 사용된 Test 스타일에 대해 설명합니다.

<br/><br/>


## 테스트 전략


<br/><br/>



## 메서드 네이밍

직관적인 테스트 메서드 네이밍은 테스트를 한 눈에 이해할 수 있게 합니다.  
cf) `@DisplayName` 어노테이션을 통해 메서드 네이밍을 통해 전달되지 못한 부족한 설명을 첨언할 수도 있습니다.

<br/>

테스트 이름에는 테스트 대상의 요구사항과 입력 값(시나리오), 그리고 상태에 대한 예상 결과가 포함되어야 합니다.

<br/>

**\[ MethodName_StateUnderTest_ExpectedBehavior \]**  

`Public void Sum_NegativeNumberAs1stParam_ExceptionThrown ()`

`Public void Sum_NegativeNumberAs2ndParam_ExceptionThrown ()`

`Public void Sum_simpleValues_Calculated ()`

`Public void Parse_OnEmptyString_ExceptionThrown ()`

`Public void Parse_SingleToken_ReturnsEqualToeknValue ()`
# jwp-shopping-order

## 할 일
- 로그 저장 파일 따로 지정하기
- 배포 스크립트 파일 어떻게 관리할지 생각해보기
  - 보안에 민감한 정보가 존재하지 않기 때문에 git에서 관리
- API 문서화
  - RestDocs 적용

## 2단계 기능 목록

### 주문
- [ ] 장바구니 내 상품을 선택해서 주문할 수 있다.
- [ ] 장바구니의 상품 중 주문 완료된 상품은 장바구니에서 제거한다.
- [x] 주문 금액에 따라 할인이 적용된다.
  - 5만원 이상 주문 시 2000원 할인
  - 10만원 이상 주문 시 5000원 할인
  - 20만원 이상 주문 시 12000원 할인
- [ ] 주문 내역을 조회한다.
  - [ ] 상세 정보 조회
    - 주문 번호
    - 각 품목 정보 - 품목명, 수량, 가격
    - 주문 날짜
    - 할인 전 금액
    - 전체 할인 금액
    - 할인 후 금액
  - [ ] 전체 목록 조회
    - 주문 번호
    - 총 주문 금액
    - 품목명
    - 주문 날짜

### Order 도메인
- [ ] 한 주문에는 여러 장바구니 상품이 포함될 수 있다.
  - [ ] 어떠한 상품도 존재하지 않는다면 주문이 생성될 수 없다.
- [ ] 총 주문 금액을 계산한다.
  - [ ] 할인 적용

- 장바구니 상품을 통해 총 금액을 계산하는 역할의 객체가 있다면 어떨까?

### 인프라
- [ ] AWS EC2 - DB 서버 인스턴스 생성
- [ ] 프로덕트 DB - MySQL 적용

### API
- [ ] 주문 생성
- [ ] 주문 내역 전체 조회
- [ ] 주문 내역 단건 조회
- [ ] 장바구니 단건 조회
- [ ] 페이지네이션
  - 출력할 데이터 개수, 페이지 수로 요청받은 뒤 그에 맞는 데이터를 응답한다.
  - ex) 개수: 20, 페이지: 3 -> id 41~60에 해당하는 데이터로 응답
  - [ ] 장바구니
  - [ ] 상품

## 궁금한 내용
- Member가 CartItem을 가지도록 설계된 이유가 무엇일까?
  - 한 Member는 여러개의 CartItem을 가질 수 있다. 반대로, 한 CartItem은 여러 Member를 가질 수 없다. 즉, 1:N 관게이다.
  - Member가 List<CartItem>을 가지고 있을 때의 장단점은 무엇이 있을까?
    - 장점
      - Member가 다른 객체를 거치지 않고도 본인의 CartItem을 관리(추가/제거 등)할 수 있다. == 캡슐화
      - 개인적인 의견으로는 이 구조가 더 자연스럽게 보인다. (장점이라기보단 개인적인 의견.. 그래서 의문이 생긴듯)
    - 단점
      - Member를 조회할 때 CartItem까지 모두 조회해야 한다. == 결합도 증가 -> 객체 생성과 소멸의 복잡성이 올라감
  - CartItem이 Member를 가지고 있을 때의 장단점은?
    - 장점
      - CartItem은 도메인 특성상 무조건 Member에게 포함된다. 그러므로 Member와 함께 조회되는 과정 자체는 자연스럽다고 생각한다.
      - CartItem에게 있어서 Member 객체와의 의존성이 좀 더 낮아지며, 유연성이 높아진다.(CartItem을 가지는 Member가 변경되면 그냥 객체를 갈아끼워주면 됨)
        - 이 장점은 현재 도메인에서 무의미해보인다. 장바구니 상품이 다른 유저에게 이동될 일은 없을 것 같다.
    - 단점
      - Member가 매우 많은 정보(나이, 성별, 생일, 프로필 사진 등등...)를 가지고 있다고 가정하자. 모든 CartItem이 자신의 Member를 가지고 있는 것은, 불필요하게 많은 정보를 가지고 있는 것이 아닐까? -> 정보의 중복, 불필요한 의존성
      - CartItem을 수정하거나 삭제할 때 Member 객체가 이를 알도록 해야 하는데 이때의 관리가 복잡해진다.
  
- 1:N 관계에서 N이 1을 알고 있을 때, 문제가 발생할 수 있는 경우는 다음 상황인 것 같다.
  - Member가 많은 정보를 가지고 있다면 CartItem이 Member를 가지고 있는 것은 불필요한 의존성이 생기는 것이다.
    - 현재는 Member가 많은 정보를 갖고 있지 않고, 인증을 위해 필요한 정보만 가지고 있다. 그리고 CartItem은 무조건 인증이 필요하기 때문에 지금은 문제가 없는 것 같다. 만약 Member가 많은 정보를 갖게 된다면 그땐 인증만을 위한 새로운 객체를 분리하게 될 것 같다.
  - CartItem의 수정/삭제 발생을 Member는 알 수 없다. 별도의 메서드를 통해 관리해야 한다.
    - 이 문제가 발생한다는 것은 A라는 CartItem 객체가 제거됐는데, Member 객체는 이를 인지하지 못하고 A를 관리하고 있다고 착각하는 상황인 것 같다. 이는CartItem이 삭제되고 Member에 반영되기까지의 사이에 발생할 수 있는 것 같다. 생각해보면 이건 1:N 관계에서만 발생할 수 있는 문제가 아닌 것 같은데 멀티스레드 환경에서 이런 걸 고려해야 되는 걸까??.. 근데 Transaction을 잘 활용하면 이 문제는 해결할 수 있을 것 같다. 아무튼 당장은 크게 문제 삼지 않아도 될 것 같다.

# MES-Lite (경량 제조실행시스템)
> **"제조 현장 운영 흐름을 직접 설계하고 구현한 경량 MES 포트폴리오"**

---

### 기술 스택
* **Language:** Java 17
* **Framework:** Spring Boot 3.2.3
* **Database:** MySQL 8.0, JPA (Hibernate)
* **View:** Thymeleaf, Chart.js (KPI 시각화)

---

### 개발 기간
* **2026.03 (약 2주)**

---

### 핵심 구현 기능

* **역할 기반 접근 제어 (RBAC)**
  * ADMIN / OPERATOR / QC 권한별 메뉴 노출 분기 처리
  * UI 버튼 숨김뿐 아니라 **Controller 레벨에서도 권한 검증** 로직 구현
* **생산 프로세스 관리**
  * 작업지시 생성 및 상태(Open/Progress/Done) 관리
  * **State Machine** 방식을 도입한 LOT 추적 및 이력 자동 기록
* **실적 및 품질 관리**
  * 생산 실적 입력 (수량 검증 로직 및 LOT 자동 완료 처리)
  * 불량 원인 분류 및 처리 방법(폐기/재작업) 기록 기능
* **대시보드 및 KPI 리포트**
  * Chart.js를 활용한 수율, 불량률, UPH 시각화
  * 현황 요약 및 불량률 5% 초과 시 경고 표시

---

### 향후 개발 예정 (Future Roadmap)

* **ON_HOLD LOT 재개 기능:** 공정 중 일시 정지된 LOT의 생산 재개 로직
* **사용자 관리 화면:** 관리자 전용 계정 생성 및 권한 변경 UI
* **데이터 활용도 제고:** KPI 데이터 CSV 내보내기 기능
* **시스템 확장:** 외부 ERP 연동을 위한 REST API 설계
* **실시간성 강화:** **WebSocket**을 적용한 대시보드 실시간 무인 모니터링 체계 구축
---

### 실행 방법

1. MySQL에서 `schema.sql` 실행
2. `src/main/resources/application.properties` 에 DB 비밀번호 입력
```properties
   spring.datasource.password=본인비밀번호
```
3. Spring Boot 실행
4. http://localhost:8080 접속
| **qc1** | 1234 | 품질검사 | 불량 상세 내역 기록 |

---

### 시연 시나리오

| 단계 | 계정 | 행동 |
|---|---|---|
| 1 | admin / 1234 | 작업지시 생성 (제품, 공정, 수량 입력) |
| 2 | admin / 1234 | LOT 목록에서 WAITING → IN_PROGRESS 상태 변경 |
| 3 | operator1 / 1234 | LOT 목록에서 실적입력 버튼 클릭 |
| 4 | operator1 / 1234 | 양품 90 / 불량 10 입력 후 제출 |
| 5 | qc1 / 1234 | 불량 목록에서 원인 분류 + 처리 방법 선택 |
| 6 | admin / 1234 | 대시보드에서 불량률 5% 초과 경고 확인 |
| 7 | admin / 1234 | KPI 리포트에서 수율 / 불량률 / 차트 확인 |

> URL 직접 접근 테스트: operator1 로그인 후 `/work-orders/new` 입력 시 로그인 페이지로 강제 이동





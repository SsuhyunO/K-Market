# K-Market API 요약

K-Market 화면에서 사용하는 주요 HTTP API를 기능별로 정리한 문서입니다. 모든 경로 앞에는 애플리케이션 컨텍스트 경로 `/K_Market`이 붙습니다.

예: `GET /product/api/list` → `GET /K_Market/product/api/list`

> 외부 공개 API 명세가 아니라 포트폴리오와 유지보수를 위한 요약입니다. 세부 요청 필드와 응답 모델은 각 DTO와 컨트롤러를 기준으로 합니다.

## 공통 규칙

- 인증은 서버 세션을 사용합니다.
- JSON 요청은 `Content-Type: application/json`을 사용합니다.
- 화면 폼 요청은 `application/x-www-form-urlencoded` 또는 `multipart/form-data`를 사용합니다.
- 일반적인 성공 응답은 `200 OK`, 요청 실패는 `400`, 권한 부족은 `403`, 대상 없음은 `404`를 사용합니다.

## 회원·인증

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `GET` | `/api/member/check-uid` | 아이디 중복 확인 |
| `GET` | `/api/member/check-email` | 이메일 중복 확인 |
| `POST` | `/api/member/signup` | 일반회원 가입 및 가입 쿠폰 지급 |
| `POST` | `/api/seller/signup` | 기업회원 가입 |
| `GET` | `/api/seller/check-bizregno` | 사업자등록번호 중복 확인 |
| `POST` | `/api/member/login` | 세션 로그인 |
| `POST` | `/api/member/logout` | 로그아웃 |
| `GET` | `/api/member/me` | 현재 로그인 회원 조회 |
| `POST` | `/api/member/email/send-code` | 이메일 인증번호 발송 |
| `POST` | `/api/member/email/verify-code` | 이메일 인증번호 확인 |
| `POST` | `/api/member/find-uid` | 아이디 찾기 |
| `POST` | `/api/member/find-password` | 비밀번호 재설정 인증 요청 |
| `POST` | `/api/member/reset-password` | 비밀번호 재설정 |
| `POST` | `/api/member/mypage/password/verify` | 현재 비밀번호 확인 |
| `POST` | `/api/member/mypage/password/change` | 비밀번호 변경 |
| `POST` | `/api/member/mypage/update` | 회원정보 수정 |
| `POST` | `/api/member/withdraw` | 회원 탈퇴 |

Google OAuth2 로그인 시작 경로는 `/oauth2/authorization/google`, 콜백 경로는 `/login/oauth2/code/google`입니다.

## 상품·장바구니·주문

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `GET` | `/product/api/list` | 카테고리·정렬 조건 상품 목록 |
| `GET` | `/product/api/search` | 키워드 상품 검색 |
| `POST` | `/cart/api` | 장바구니 상품 추가 |
| `DELETE` | `/cart/api` | 장바구니 상품 삭제 |
| `POST` | `/product/api/order` | 주문 생성, 쿠폰·포인트·재고 반영 |
| `GET` | `/my/order/api/list` | 내 주문 목록 |
| `GET` | `/my/order/api/orders/{orderNo}/items` | 주문별 상품 조회 |
| `POST` | `/my/order/api/{orderItemNo}/confirm` | 구매확정 및 기업 쿠폰 지급 |
| `POST` | `/my/order/api/{orderItemNo}/claim` | 주문상품 클레임 신청 |
| `POST` | `/my/order/api/{orderItemNo}/claims` | 주문상품 클레임 신청 |
| `POST` | `/my/order/api/orders/{orderNo}/cancel` | 주문 전체 취소 |

## 리뷰·포인트

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `GET` | `/review/api/list` | 내 리뷰 목록 |
| `GET` | `/review/api/product/{prodNo}/list` | 상품 리뷰 목록 |
| `POST` | `/review/api/write` | 구매확정 상품 리뷰 작성 |
| `GET` | `/point/api/list` | 내 포인트 내역 조회 |

## 기업회원·관리자 상품

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `GET` | `/admin/product/api/list` | 관리 상품 목록 |
| `GET` | `/admin/product/api/{prodNo}` | 상품 상세 조회 |
| `DELETE` | `/admin/product/api` | 상품 삭제 |
| `POST` | `/admin/product/register` | 상품·이미지·옵션 등록 |
| `POST` | `/admin/product/edit` | 상품 수정 |
| `POST` | `/admin/product/remove` | 상품 삭제 화면 요청 |

기업회원은 자신의 상품만, 관리자는 전체 상품을 대상으로 합니다.

## 주문·배송·클레임 관리

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `GET` | `/admin/order/api/list` | 주문 목록 |
| `GET` | `/admin/order/api/{orderNo}` | 주문 상세 |
| `GET` | `/admin/order/api/{orderNo}/shippable-items` | 배송 등록 가능 상품 조회 |
| `POST` | `/admin/order/api/shipments` | 운송장·배송 상품 등록 |
| `GET` | `/admin/order/api/deliveries` | 배송 목록 |
| `GET` | `/admin/order/api/deliveries/{shipmentNo}` | 배송 상세 |
| `GET` | `/admin/order/api/claims` | 클레임 목록 |
| `GET` | `/admin/order/api/claims/{claimNo}` | 클레임 상세 |
| `POST` | `/admin/order/api/claims/{claimNo}/approve` | 클레임 승인 |
| `POST` | `/admin/order/api/claims/{claimNo}/reject` | 클레임 거절 |
| `POST` | `/admin/order/api/claims/{claimNo}/reship` | 교환상품 재배송 |

## 쿠폰

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `POST` | `/admin/coupon/register` | 관리자·기업회원 쿠폰 생성 |
| `PATCH` | `/admin/coupon/{couponNo}/end` | 쿠폰 발급 종료 |
| `PATCH` | `/admin/coupon/issue/{issueNo}/stop` | 발급된 쿠폰 사용 중지 |

- 관리자는 전체 주문 할인 또는 배송비 무료 쿠폰을 만듭니다.
- 기업회원은 자사 상품 할인 또는 배송비 무료 쿠폰을 만듭니다.
- 기업회원의 목록·발급 내역은 로그인한 판매자 범위로 제한합니다.

## 사이트 운영

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `POST` | `/admin/banner/register` | 배너 등록 |
| `GET` | `/admin/banner/toggle` | 배너 노출 상태 변경 |
| `POST` | `/admin/banner/modify` | 배너 수정 |
| `POST` | `/admin/banner/delete` | 배너 삭제 |
| `POST` | `/admin/site-settings/modify-site-settings` | 사이트 기본정보 수정 |
| `POST` | `/admin/site-settings/modify-site-logo` | 로고·파비콘 수정 |
| `POST` | `/admin/site-settings/modify-corporate-info` | 회사정보 수정 |
| `POST` | `/admin/site-settings/modify-customer-support-info` | 고객센터 정보 수정 |
| `POST` | `/admin/site-settings/modify-copyright` | 카피라이트 수정 |
| `POST` | `/admin/terms-management` | 약관 수정 |
| `POST` | `/admin/version-management/register` | 버전 이력 등록 |
| `POST` | `/admin/version-management/update` | 버전 이력 수정 |
| `POST` | `/admin/version-management/delete` | 버전 이력 삭제 |

## 파일 조회

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `GET` | `/files/{id}` | DB 파일 ID에 해당하는 업로드 파일 응답 |
| `GET` | `/favicon.ico` | 관리자 설정 파비콘 응답 |

운영 업로드 경로는 환경변수 `FILE_UPLOAD_PATH`로 지정하며 현재 배포 환경에서는 `/opt/kmarket/uploads`를 사용합니다.

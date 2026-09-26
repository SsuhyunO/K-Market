# K-Market 데이터베이스

K-Market의 로컬·운영 데이터베이스는 **MySQL 8.0**을 기준으로 합니다. `schema-mysql.sql`은 빈 환경에서 `k_market` 데이터베이스와 애플리케이션 테이블을 생성하기 위한 DDL입니다.

## 파일 구성

| 파일 | 용도 |
| --- | --- |
| `schema-mysql.sql` | 데이터베이스와 전체 테이블·인덱스·외래키 생성 |
| `seed-admin.sql` | 초기 관리자 계정 입력 예시 |
| `migration-20260927-coupon-issue-unique.sql` | 기존 DB에 쿠폰 중복 발급 방지 제약 추가 |

> 실제 비밀번호, 이메일, 운영 DB 주소는 SQL 파일에 저장하지 않습니다.

## 스키마 적용

명령 프롬프트에서 프로젝트 루트로 이동한 뒤 실행합니다.

```powershell
mysql --default-character-set=utf8mb4 -u root -p < .\database\schema-mysql.sql
```

PowerShell에서는 다음 방법을 사용합니다.

```powershell
Get-Content -Raw .\database\schema-mysql.sql |
    mysql --default-character-set=utf8mb4 -u root -p
```

MySQL Workbench에서는 `File → Open SQL Script`로 `schema-mysql.sql`을 열고 전체 실행하면 됩니다.

## 애플리케이션 계정 생성 예시

`root` 대신 K-Market 전용 계정을 사용하는 것을 권장합니다.

```sql
CREATE USER IF NOT EXISTS 'kmarket_app'@'localhost'
IDENTIFIED BY '변경할-비밀번호';

GRANT SELECT, INSERT, UPDATE, DELETE
ON k_market.* TO 'kmarket_app'@'localhost';

FLUSH PRIVILEGES;
```

스키마를 해당 계정으로 직접 생성해야 한다면 초기 구성 중에만 `CREATE`, `ALTER`, `INDEX`, `REFERENCES` 권한을 추가하고 구성이 끝난 뒤 회수합니다.

## 로컬 환경변수

IntelliJ 실행 구성 또는 운영체제 환경변수에 다음 값을 설정합니다.

```text
DB_URL=jdbc:mysql://localhost:3306/k_market?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
DB_USERNAME=kmarket_app
DB_PASSWORD=로컬-비밀번호
```

운영 환경에서는 GitHub Actions Secrets의 `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`가 서버 실행 환경 파일로 전달됩니다.

## 주요 테이블 영역

| 영역 | 테이블 |
| --- | --- |
| 회원·판매자 | `member`, `seller` |
| 상품·카테고리 | `category`, `product`, `product_option_group`, `product_option_item`, `product_variant`, `product_variant_item`, `product_notice_value` |
| 장바구니·주문 | `cart`, `order`, `order_item` |
| 배송·클레임 | `shipment`, `shipment_item`, `claim` |
| 혜택 | `coupon`, `coupon_issue`, `point` |
| 후기·고객센터 | `review`, `board`, `board_reply`, `qna`, `recruit` |
| 사이트 운영 | `file`, `banner`, `adminConfig`, `policy`, `version` |

## DDL 관리 원칙

- 문자셋은 `utf8mb4`, 정렬 규칙은 `utf8mb4_unicode_ci`를 사용합니다.
- `schema-mysql.sql`은 기존 테이블을 삭제하지 않으며 신규 환경 생성을 목적으로 합니다.
- 운영 DB 변경은 먼저 별도 마이그레이션 SQL로 적용한 후 기준 DDL에도 동일하게 반영합니다.
- JPA 운영 프로필은 `ddl-auto=none`이므로 배포 과정에서 스키마가 자동 변경되지 않습니다.
- `coupon_issue`는 `(couponNo, memberUid)` 유니크 키로 동일 쿠폰의 중복 지급을 방지합니다.
- 배송 조회에 사용하는 `shipment.orderNo`와 `shipment_item` 연결 컬럼에는 인덱스를 유지합니다.

## 초기 관리자 계정

`seed-admin.sql`을 사용하기 전에 비밀번호가 BCrypt 해시인지 확인하고 개인정보를 데모 값으로 변경합니다.

```powershell
Get-Content -Raw .\database\seed-admin.sql |
    mysql --default-character-set=utf8mb4 -u kmarket_app -p k_market
```

공개 저장소에는 실제 운영 관리자 비밀번호를 절대 기록하지 않습니다.

## 기존 DB에 중복 발급 제약 적용

먼저 같은 회원에게 동일 쿠폰이 중복 발급된 데이터가 있는지 확인합니다.

```sql
SELECT couponNo, memberUid, COUNT(*) AS duplicateCount
FROM coupon_issue
GROUP BY couponNo, memberUid
HAVING COUNT(*) > 1;
```

결과가 없을 때 다음 마이그레이션을 적용합니다.

```powershell
Get-Content -Raw .\database\migration-20260927-coupon-issue-unique.sql |
    mysql --default-character-set=utf8mb4 -u kmarket_app -p k_market
```

중복 결과가 있다면 어떤 발급 건을 유지할지 먼저 확인해야 하므로 마이그레이션을 바로 실행하지 않습니다.

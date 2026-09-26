-- 동일 회원에게 동일 쿠폰이 중복 발급되는 것을 DB 수준에서 방지합니다.
-- 적용 전 database/README.md의 중복 데이터 확인 쿼리를 먼저 실행하세요.

ALTER TABLE `coupon_issue`
  ADD CONSTRAINT `uk_coupon_issue_coupon_member`
  UNIQUE (`couponNo`, `memberUid`);

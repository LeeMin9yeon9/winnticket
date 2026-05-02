# winnticket BE

Spring Boot 3 + MyBatis + PostgreSQL + Redis.

## 환경변수

운영/개발 서버 systemd unit 또는 셸 환경에 다음을 설정해야 합니다.
미설정 시 해당 기능이 비활성되거나 부팅이 실패합니다.

| 변수명 | 필수 여부 | 용도 | 기본/예시 |
|---|---|---|---|
| `JWT_SECRET` | 필수 | JWT 서명 키 (Base64) | 32바이트 이상 랜덤값 |
| `LSCOMPANY_TOKEN` | 필수(prod) | LS컴퍼니 API 토큰 | LS컴퍼니 발급 |
| `PAYLETTER_PAYMENT_API_KEY` | 필수(prod) | PayLetter 결제 API 키 | PayLetter 발급 |
| `PAYLETTER_SEARCH_API_KEY` | 필수(prod) | PayLetter 조회 API 키 | PayLetter 발급 |
| `BATCH_SECRET` | 선택 | `/api/benepia/batch/**` 호출 인증 헤더값 | 32자 이상 랜덤 문자열 |

### `BATCH_SECRET`

베네피아 배치 엔드포인트(`/api/benepia/batch/ticket/run`)를 외부에서 트리거할 때
HTTP 헤더 `X-Batch-Secret` 값과 일치해야 통과합니다.

호출 예시:
```bash
curl -X POST \
  -H "X-Batch-Secret: <BATCH_SECRET 값>" \
  https://www.winnticket.co.kr/api/benepia/batch/ticket/run
```

응답:
- `200` — 배치 실행 OK
- `401` — 헤더 누락/불일치
- `503` — 서버에 `BATCH_SECRET` 미설정 (엔드포인트 비활성)

### systemd unit 적용 (운영서버)

```bash
# 1. drop-in 디렉토리 보장
sudo mkdir -p /etc/systemd/system/app.service.d

# 2. 환경변수 파일 작성
sudo tee /etc/systemd/system/app.service.d/env.conf > /dev/null <<'EOF'
[Service]
Environment=BATCH_SECRET=<32자 이상 랜덤 문자열>
EOF

# 3. 적용
sudo systemctl daemon-reload
sudo systemctl restart app

# 4. 확인
sudo systemctl show app | grep BATCH_SECRET
```

이 drop-in 파일은 Jenkins 배포(JAR 교체)에 영향을 받지 않습니다.

## 빌드 / 실행

```bash
./gradlew clean build -x test
java -jar -Dspring.profiles.active=dev build/libs/*-SNAPSHOT.jar
```

## 배포

`main` 브랜치 푸쉬 → GitHub webhook → Jenkins `dev-deploy`
→ 빌드된 JAR을 `13.209.91.167:/home/ubuntu/app/app.jar` 로 SCP
→ `sudo systemctl restart app`

## 주요 도메인

- `auth` — 로그인/JWT/세션
- `order` — 주문/결제/취소 (admin + shop)
- `integration/payletter` — PG 카드결제
- `integration/benepia` — 베네피아 SSO + KCP 포인트
- `integration/{plusn,coreworks,woongjin,spavis,playstory,smartinfini,aquaplanet,lscompany,mair}` — 파트너 발권 연동
- `ticketCoupon` — 티켓 쿠폰 발행/사용
- `partners` — 파트너/현장관리자
- `product` / `channels` / `community` / `guide` — 상품/채널/게시판/가이드

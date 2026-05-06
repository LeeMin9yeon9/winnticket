# Winnticket BE

## 브랜치 전략

### main
- 개발 최신 브랜치
- 토스 결제 포함
- 모든 신규 기능 먼저 머지

### production
- 실제 운영 배포용 브랜치
- **토스 결제 미포함** (아직 운영 미적용)
- 로컬 빌드는 production 브랜치 기준으로 수행
- 빌드 결과물을 운영 서버에 별도 업로드하는 방식

### 작업 규칙
- 토스를 제외한 모든 기능 변경은 **main + production 둘 다 푸쉬**
- 토스 관련 작업은 main에만 머지, production에는 넣지 않음
- 로컬 빌드 시 production 브랜치로 체크아웃 후 빌드

```bash
# 운영 빌드 방법
git checkout production
./gradlew build -x test
```

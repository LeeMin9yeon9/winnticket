//package kr.co.winnticket.auth.jwt;
//
//import org.springframework.stereotype.Component;
//
//@Component
//public class JwtUtil {
//
//    private final Key key;
//    private final long expireMs = 1000L * 60 * 60; // 1시간
//
//    public JwtUtil() {
//        this.key = Keys.hmacShaKeyFor("winnticket-secret-key-for-jwt-2024-very-secret".getBytes());
//    }
//
//    // 토큰 생성
//    public String createToken(Long userId, Long partnerId, String role) {
//        return Jwts.builder()
//                .claim("userId", userId)
//                .claim("partnerId", partnerId)
//                .claim("role", role)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expireMs))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    // 토큰에서 값 꺼내기
//    public Claims getClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    // 토큰 유효성 검사
//    public boolean validate(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}
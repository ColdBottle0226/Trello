package pjt.trello.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import pjt.trello.common.Message;
import pjt.trello.common.StatusEnum;
import pjt.trello.jwt.JwtTokenProvider;
import pjt.trello.jwt.request.UserRequestDto;
import pjt.trello.jwt.response.UserResponseDto;
import pjt.trello.member.model.UserVo;
import pjt.trello.member.repository.MemberDao;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService{
    @Autowired
    MemberDao memberDao;
    @Autowired
    RedisTemplate<String,String> stringRedisTemplate;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private PasswordEncoder pwEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    //스프링 시큐리티의 인증에 대한 지원을 설정하는 몇 가지 메소드를 가짐
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    Message message = new Message();
    HttpHeaders headers= new HttpHeaders();

    @Override
    public void memberJoin(UserVo userVo) {
        // 시크릿 키 생성
        UUID secretKey = UUID.randomUUID();
        String rawPw = "";
        String encodePw = "";

        // 전달받은 비밀번호 = rawPw
        rawPw = userVo.getPassword();
        // pwEcoder로 rawPw 인코딩
        encodePw = pwEncoder.encode(rawPw);
        userVo.setPassword(encodePw);
        // secretKey String형으로 변환 후 할당
        userVo.setSecretKey(secretKey.toString());

        /** Redis 저장 */
        // 현재시각 String 변환
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formatDateTime = now.format(formatter);

        // linkedHashMap : 순서 보장 => 필수 아님
        Map<String, String> userData = new LinkedHashMap<>();
        userData.put("userId", userVo.getUserId());
        userData.put("userName", userVo.getUserName());
        userData.put("userEmail", userVo.getUserEmail());
        userData.put("password", userVo.getPassword());
        userData.put("userStatus", "00");
        userData.put("delYn", "N");
        userData.put("loginFailCnt", "0");
        userData.put("loginDt", "");
        userData.put("creDt", formatDateTime);
        userData.put("udtDt", "");
        userData.put("secretKey", userVo.getSecretKey());

        // Redis 저장
        String key = "tb_user:" + userVo.getUserId();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(key, userData);
        // 일반 DB 저장
        memberDao.memberJoin(userVo);
    }

    @Override
    public ResponseEntity<Message> memberIdCheck(String userId) {
        /** Redis 사용 */
        final String key = "tb_user:" + userId;
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String userIdValue = hashOperations.get(key, "userId");
        if (userIdValue != null) {
            Message message = new Message(StatusEnum.BAD_REQUEST, "이미 사용중인 아이디입니다.");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        } else {
            Message message = new Message(StatusEnum.OK, "사용 가능한 아이디입니다.");
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Message> memberEmailCheck(String userEmail) {
        int emailCheck = memberDao.memberEmailCheck(userEmail);
        if (emailCheck > 0) {
            Message message = new Message(StatusEnum.BAD_REQUEST, "이미 사용중인 이메일입니다.");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }else{
            Message message = new Message(StatusEnum.OK, "사용 가능한 이메일입니다.");
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Message> memberLogin(UserVo userVo, HttpServletResponse response) {
        String rawPw = "";
        String encodePw = "";

        /** Redis 사용 */
        final String key = "tb_user:" + userVo.getUserId();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String password = hashOperations.get(key, "password");

        if (password != null) { //일치하는 아이디 존재시
            rawPw = userVo.getPassword(); // 사용자가 제출한 비밀번호
            // 로그인 정보 일치할 경우
            if (pwEncoder.matches(rawPw, password)) { // 비밀번호 일치(Redis)
                // 로그인 시간 변경
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                String formatDateTime = now.format(formatter);
                hashOperations.put(key, "loginDt", formatDateTime);

                // 1. Login ID/PW 를 기반으로 Authentication 객체 생성(검증 X, principal:로그인 시도 아이디, credentials:로그인 시도 비밀번호, authenticated:검증여부)
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userVo.getUserId(), userVo.getPassword());

                // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분(AuthenticationManager에게 인증과정 위임)
                // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행(UserDetailsService를 구현했기때문)
                Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
                // 3. 인증 정보를 기반으로 JWT 토큰 생성
                UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

                // refreshToken
                String refreshToken = tokenInfo.getRefreshToken();

                // redis에 refreshToken 저장(id)
                final String refreshTokenKey = "refresh_token:" + userVo.getUserId();
                ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
                valueOperations.set(refreshTokenKey, refreshToken, jwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

                Message message = new Message(StatusEnum.OK, "로그인 되었습니다.");
                message.addData("accessToken", tokenInfo.getAccessToken());

                Cookie refreshTokenCookie = new Cookie("refreshToken", tokenInfo.getRefreshToken());
                refreshTokenCookie.setHttpOnly(true);
                refreshTokenCookie.setMaxAge((int) jwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME / 1000);
                refreshTokenCookie.setPath("/");
                response.addCookie(refreshTokenCookie);

                log.info("token정보:{}", tokenInfo);
                return new ResponseEntity<>(message, HttpStatus.OK);
            } else { // 비밀번호 일치하지 않음
                Message message = new Message(StatusEnum.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }
        } else { // 아이디 존재하지 않을 경우
            Message message = new Message(StatusEnum.BAD_REQUEST, "해당하는 아이디는 없습니다.");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public UserVo getMemberInfo(String token) {
        // token 정보 복호화
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        // userId 얻기
        String userId = authentication.getName();
        
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        // 찾은 id로 회원정보 얻기
        String key = "tb_user:" + userId;
        Map<String, String> userData = hashOperations.entries(key);

        // Map정보 UserVo객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        UserVo userVo = objectMapper.convertValue(userData, UserVo.class);

        return userVo;
        // return memberDao.getMemberInfo(secretKey);
    }

    @Override
    public void updateSecretKey(UserVo userVo) {
        memberDao.updateSecretKey(userVo);
    }

    @Override
    public void updateMemberInfo(UserVo userVo) {
        String rawPw = "";
        String encodePw = "";
        rawPw = userVo.getPassword();

        final String key = "tb_user:" + userVo.getUserId();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        // 비밀번호가 들어왔을 경우
        if (rawPw != null && !rawPw.isEmpty()) {
            // pwEcoder로 rawPw 인코딩
            encodePw = pwEncoder.encode(rawPw);
            userVo.setPassword(encodePw);

            hashOperations.put(key, "password", userVo.getPassword());
        }else{
            hashOperations.put(key, "userEmail", userVo.getUserEmail());
        }

        memberDao.updateMemberInfo(userVo);
    }

    @Override
    public void updateMemberDelYn(UserVo userVo) {
        log.info("탈퇴할 회원 객체:{}",userVo);

        final String key = "tb_user:" + userVo.getUserId();
        redisTemplate.delete(key);

//        memberDao.updateMemberDelYn(userVo);
    }
    @Override
    public ResponseEntity<?> logout(UserRequestDto.Logout logout, HttpServletResponse response) {
        // 1. Access Token 검증
        if (!jwtTokenProvider.validateToken(logout.getAccessToken())) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }

        // 2. Access Token 에서 User email 을 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(logout.getAccessToken());

        // 3. Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제
        if (stringRedisTemplate.opsForValue().get("refresh_token:" + authentication.getName()) != null) {
            // Refresh Token 삭제
            stringRedisTemplate.delete("refresh_token:" + authentication.getName());
            // Cookie에서 Refresh Token 삭제
            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setMaxAge(0);
            refreshTokenCookie.setPath("/");
            response.addCookie(refreshTokenCookie);
        }

        Message message = new Message(StatusEnum.OK, "로그아웃 되었습니다");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> reissue(UserRequestDto.Reissue reissue, String refreshToken, HttpServletResponse response) {
        // 1. Refresh Token 검증
        log.info("refresh토큰:{}",refreshToken);
        log.info("access토큰:{}",reissue.getAccessToken());
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.badRequest().body("Refresh Token 정보가 유효하지 않습니다.");
        }

        // 2. Access Token 에서 User ID 를 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());

        // 3. Redis 에서 User ID 을 기반으로 저장된 Refresh Token 값을 가져오기
        String refreshTokenKey = "refresh_token:" + authentication.getName();
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        String storedRefreshToken = valueOperations.get(refreshTokenKey);
        log.info("저장된ref토큰:{}", storedRefreshToken);

        if (!refreshToken.equals(storedRefreshToken)) {
            log.info("Refresh Token 정보가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body("Refresh Token 정보가 일치하지 않습니다.");
        }

        // 4. 새로운 토큰 생성
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // 5. RefreshToken Redis 업데이트
        valueOperations.set(refreshTokenKey, tokenInfo.getRefreshToken(), jwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        // 6. Set-Cookie 헤더에 새로운 Refresh Token 값을 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenInfo.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge((int) jwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME / 1000);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

        // 7. 새로운 AccessToken과 RefreshToken 값을 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenInfo.getAccessToken())
                .body(tokenInfo);
    }

}

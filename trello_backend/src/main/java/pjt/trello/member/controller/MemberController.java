package pjt.trello.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pjt.trello.common.Message;
import pjt.trello.jwt.JwtTokenProvider;
import pjt.trello.jwt.request.UserRequestDto;
import pjt.trello.jwt.response.UserResponseDto;
import pjt.trello.member.model.UserVo;
import pjt.trello.member.service.MemberService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/member")
@Slf4j
public class MemberController {
    Message message = new Message();
    HttpHeaders headers = new HttpHeaders();

    @Autowired
    MemberService memberService;
    @Autowired
    private PasswordEncoder pwEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/memberJoin")
    public void memberJoin(@Valid @RequestBody UserVo userVo, BindingResult bindingResult) {
        log.info("회원가입:{}", userVo);
        // valdiation 에러 발생시        
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            StringBuilder sb = new StringBuilder();
            // 에러들 여러개
            for (FieldError error : errors) {
                // getDefaultMessage : defaultError메시지
                sb.append(error.getDefaultMessage() + ", ");
            }
            log.info("Validation errors: {}", sb.toString());
            // 에러메시지 반환
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, sb.toString());
        } else {
            memberService.memberJoin(userVo);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Message> getMember(@RequestBody UserVo userVo, HttpServletResponse response) {
        log.info("로그인:{}", userVo);
        return memberService.memberLogin(userVo, response);
    }

    @GetMapping("/idCheck")
    public ResponseEntity<Message> idCheck(@RequestParam String userId) {
        return memberService.memberIdCheck(userId);
    }

    @GetMapping("/emailCheck")
    public ResponseEntity<Message> emailCheck(@RequestParam String userEmail) {
        return memberService.memberEmailCheck(userEmail);
    }

    @GetMapping("/getMemberInfo")
    public UserVo getMemberInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return memberService.getMemberInfo(token);
    }

    @PatchMapping("/updateMemberInfo")
    public void updateMemberInfo(@RequestBody UserVo userVo) {
        log.info("수정 회원정보:{}", userVo);

        memberService.updateMemberInfo(userVo);
    }

    @PatchMapping("/updateMemberDelYn")
    public void updateMemberDelYn(@RequestBody UserVo userVo) {
        log.info("탈퇴할 회원정보:{}", userVo);
        memberService.updateMemberDelYn(userVo);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody UserRequestDto.Reissue refreshTokenRequest, @CookieValue("refreshToken")String refreshToken, HttpServletResponse response) {
        log.info("재발급할 토큰정보:{}", refreshTokenRequest);
        return memberService.reissue(refreshTokenRequest, refreshToken, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody UserRequestDto.Logout logout, HttpServletResponse response) {
        return memberService.logout(logout, response);
    }
}

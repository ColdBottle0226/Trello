package pjt.trello.member.service;

import org.springframework.http.ResponseEntity;
import pjt.trello.common.Message;
import pjt.trello.jwt.request.UserRequestDto;
import pjt.trello.member.model.UserVo;

import javax.servlet.http.HttpServletResponse;

public interface MemberService {
    public void memberJoin(UserVo userVo);
    public ResponseEntity<Message> memberIdCheck(String userId);
    public ResponseEntity<Message>  memberEmailCheck(String userEmail);
    public ResponseEntity<Message> memberLogin(UserVo userVo, HttpServletResponse response);
    public UserVo getMemberInfo(String secretKey);
    public void updateSecretKey(UserVo userVo);
    public void updateMemberInfo(UserVo userVo);
    public void updateMemberDelYn(UserVo userVo);
    public ResponseEntity<?> reissue(UserRequestDto.Reissue reissue,String refreshToken,HttpServletResponse response);
    public ResponseEntity<?> logout(UserRequestDto.Logout logout, HttpServletResponse response);
}

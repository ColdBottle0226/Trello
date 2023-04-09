package pjt.trello.member.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * tb_user 테이블 모델 클래스
 */
@Data
@NoArgsConstructor
public class UserVo {
    private long userSeq;
    /** 유저 id */
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String userId;
    /** 유저명 */
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String userName;
    /** 유저 이메일 */
    @Email
    private String userEmail;
    /** 비밀번호 */
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 16자의 비밀번호여야 합니다.")
    private String password;
    /** 유저 상태 */
    private String userStatus;
    /** 삭제 여부 */
    private String delYn;
    /** 로그인 실패 횟수 */
    private int loginFailCnt;
    /** 마지막 로그인 일시 */
    private String loginDt;
    /** 생성일 */
    private String creDt;
    /** 업데이트일 */
    private String udtDt;
    /** 회원의 secretKey(세션처리) */
    private String secretKey;
    // 추가 멤버변수
    /** 회원의 새로운 secretKey */
    private String newSecretKey;

    public UserVo(String userId) {
        this.userId = userId;
    }
}

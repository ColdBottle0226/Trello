package pjt.trello.member.model;

import lombok.Data;

/**
 * tb_user_noti 테이블 모델 클래스
 */
@Data
public class UserNotiVo {
    private int notiSeq;
    /** 유저 id */
    private String userId;
    /** 카드 코드 */
    private String cardCode;
}

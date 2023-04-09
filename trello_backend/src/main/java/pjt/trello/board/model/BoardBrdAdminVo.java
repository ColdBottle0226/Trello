package pjt.trello.board.model;

import lombok.Data;

/**
 * tb_brd_admin 테이블 모델 클래스
 */
@Data
public class BoardBrdAdminVo {
    private int adminSeq;
    /** 보드코드 */
    private String brdCode;
    /** 회원 id */
    private String userId;
    /** 회원 역할 */
    private String userRole;
}

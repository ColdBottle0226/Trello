package pjt.trello.member.model;

import lombok.Data;

/**
 * tb_grp 테이블 모델 클래스
 */
@Data
public class GrpVo {
    private int grpSeq;
    /** 그룹 코드 */
    private String grpCode;
    /** 그룹명 */
    private String grpName;
    /** 보드 코드 */
    private String brdCode;
    /** 유저 id */
    private String userId;
}

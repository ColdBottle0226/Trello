package pjt.trello.card.model;

import lombok.Data;

/**
 * tb_activity 테이블 모델 클래스
 */
@Data
public class ActivityVo {
    private int acSeq;
    /** 활동 코드 */
    private String acCode;
    /** 내용 */
    private String content;
    /** 유저 id 멘션하기 (@유저 id) */
    private String cmntId;
    /** 파일 멘션 */
    private String cmntAtt;
    /** 좋아요 개수 */
    private int likeCnt;
    /** 업데이트 여부 (Y/N) */
    private String udtYn;
    /** 삭제 여부 */
    private String delYn;
    /** 생성일 */
    private String creDt;
    /** 생성자 */
    private String creId;
    /** 카드 코드 */
    private String cardCode;

}

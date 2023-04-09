package pjt.trello.board.model;

import lombok.Data;

/**
 * tb_brd 테이블 모델 클래스
 */
@Data
public class BoardBrdVo {
    /** 보드 시퀀스 */
    private int brdSeq;
    /** 보드 코드 */
    private String brdCode;
    /** 보드명 */
    private String brdName;
    /** 공개범위 */
    private String visibility;
    /** 북마크 여부 (Y/N) */
    private String bookmarkYn;
    /** 삭제여부 (Y/N) */
    private String delYn;

    // 추가 멤버변수
    /** 리스트 코드 */
    private String listCode;
    /** 리스트 명 */
    private String listName;
    /** 리스트 순서 */
    private String listOrd;
    /** 카드 코드 */
    private String cardCode;
    /** 카드 순서 */
    private String cardOrd;
    /** 카드 제목 */
    private String title;
    /** 설명 */
    private String content;
    /** 이미지 커버 */
    private String imgCover;
}

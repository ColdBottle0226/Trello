package pjt.trello.card.model;

import lombok.Data;

/**
 * tb_card 테이블 모델 클래스
 */
@Data
public class CardVo {
    private int cardSeq;
    /** 카드 코드 */
    private String cardCode;
    /** 카드 제목 */
    private String title;
    /** 설명 */
    private String content;
    /** 이미지 커버 */
    private String imgCover;
    /** 리스트 코드 */
    private String listCode;
    /** 카드 순서 */
    private int cardOrd;

    // 추가 멤버변수
    /** 리스트 이름 */
    private String listName;

}

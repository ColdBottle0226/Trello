package pjt.trello.board.model;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * tb_list 테이블 모델 클래스
 */
@Data
public class ListVo {
    private int listSeq;
    /** 리스트 코드 */
    @Size(min = 6, max = 6)
    private String listCode;
    /** 리스트 명 */
    private String listName;
    /** 보드 코드 */
    private String brdCode;
    /** 리스트 순서 */
    private int listOrd;
}

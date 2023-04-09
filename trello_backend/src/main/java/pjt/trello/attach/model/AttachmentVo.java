package pjt.trello.attach.model;

import lombok.Data;

/**
 * tb_attachment 테이블 모델 클래스
 */
@Data
public class AttachmentVo {
    private int attachSeq;
    private String attachCode;
    /** 업로드 파일 저장경로 */
    private String fileLoc;
    /** 생성일 */
    private String creDt;
    /** 카드 코드 */
    private String cardCode;
}

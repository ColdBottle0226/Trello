package pjt.trello.board.common;

import lombok.Data;

/**
 * 이미지 업로드 결과 DTO
 */
@Data
public class UploadDto {
    private String filePath;
    private String fileType;
}

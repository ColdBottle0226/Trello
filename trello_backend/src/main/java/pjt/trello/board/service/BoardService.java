package pjt.trello.board.service;

import org.springframework.http.ResponseEntity;
import pjt.trello.board.model.BoardBrdAdminVo;
import pjt.trello.board.model.BoardBrdVo;
import pjt.trello.board.model.BoardDto;
import pjt.trello.common.Message;

import java.util.List;

public interface BoardService {
    public List<BoardBrdVo> selectBoardList();
    public BoardBrdVo selectBoardInfo(BoardBrdVo boardBrdVo);
    public List<BoardBrdVo> selectBrdByPk(BoardDto boardDto);
    public List<BoardBrdAdminVo> selectBrdAdmin(BoardBrdAdminVo boardBrdAdminVo);
    public void updateAdmin(BoardBrdAdminVo boardBrdAdminVo);
    public void updateVisibility(BoardBrdVo boardBrdVo);
    public void updateBrdInfo(BoardBrdVo boardBrdVo);
    public ResponseEntity<Message> insertBoardMember(BoardBrdAdminVo boardBrdAdminVo);
}

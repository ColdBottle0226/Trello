package pjt.trello.board.repository;

import org.apache.ibatis.annotations.Mapper;
import pjt.trello.board.model.BoardBrdAdminVo;
import pjt.trello.board.model.BoardBrdVo;
import pjt.trello.board.model.BoardDto;

import java.util.List;

@Mapper
public interface BoardDao {
    public List<BoardBrdVo> selectBoardList();
    public BoardBrdVo selectBoardInfo(BoardBrdVo boardBrdVo);
    public List<BoardBrdVo> selectBrdByPk(BoardDto boardDto);
    public List<BoardBrdAdminVo> selectBrdAdmin(BoardBrdAdminVo boardBrdAdminVo);
    public void updateAdmin(BoardBrdAdminVo boardBrdAdminVo);
    public void updateVisibility(BoardBrdVo boardBrdVo);
    public void updateBrdInfo(BoardBrdVo boardBrdVo);
    public void insertBoardMember(BoardBrdAdminVo boardBrdAdminVo);
}
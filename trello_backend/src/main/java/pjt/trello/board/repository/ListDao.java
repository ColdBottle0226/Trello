package pjt.trello.board.repository;

import org.apache.ibatis.annotations.Mapper;
import pjt.trello.board.model.BoardBrdVo;
import pjt.trello.board.model.BoardDto;
import pjt.trello.board.model.ListVo;

import java.util.List;

@Mapper
public interface ListDao {
    public void insertList(ListVo listVo);
    public String getMaxListCode();
    public ListVo getInfoListByPk(ListVo listVo);
    public void updateListOrd(ListVo listVo);
    public void updateListInfo(ListVo listVo);
    public void deleteListByPk(String listCode);
}
package pjt.trello.board.service;

import pjt.trello.board.model.ListVo;

import java.util.List;

public interface ListService {
    public void insertList(ListVo listVo);
    public void updateListInfo(ListVo listVo);
    public void updateListOrdBulk(List<ListVo> list);
    public void deleteListByPk(String listCode);
}

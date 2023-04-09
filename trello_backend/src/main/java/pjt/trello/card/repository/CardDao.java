package pjt.trello.card.repository;

import org.apache.ibatis.annotations.Mapper;
import pjt.trello.board.model.BoardBrdVo;
import pjt.trello.card.model.CardVo;

import java.util.List;

@Mapper
public interface CardDao {
    public void insertCard(CardVo cardVo);
    public String getMaxCardCode();
    public CardVo getInfoCardByPk(CardVo cardVo);
    public List<CardVo> getResultBySearch(String keyword);
    public void updateCardInfo(CardVo cardVo);
    public void updateCardOrd(List<CardVo> list);
    public void deleteCardByPk(CardVo cardVo);
    public void updateCardPosition(CardVo cardVo);

}

package pjt.trello.card.service;

import pjt.trello.card.model.CardVo;

import java.util.List;

public interface CardService {
    public void insertCard(CardVo cardVo);
    public CardVo getInfoCardByPk(CardVo cardVo);
    public List<CardVo> getResultBySearch(String keyword);
    public void updateCardInfo(CardVo cardVo);
    public void updateCardOrd(List<CardVo> list);
    public void deleteCardByPk(CardVo cardVo);
    void updateCardPosition(List<CardVo> fromUpdatedOrders, List<CardVo> toUpdatedOrders);

}

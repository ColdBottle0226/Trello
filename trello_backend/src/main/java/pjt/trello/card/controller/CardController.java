package pjt.trello.card.controller;

import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pjt.trello.card.model.CardDto;
import pjt.trello.card.model.CardVo;
import pjt.trello.card.service.CardService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/card")
@Slf4j
public class CardController {
    @Autowired
    CardService cardService;
    @PostMapping("/getCardInfo")
    public CardVo getCardInfo(@RequestBody CardVo cardVo){
        return cardService.getInfoCardByPk(cardVo);
    }
    @GetMapping("/getResultBySearch")
    public List<CardVo> getResultBySearch(@RequestParam String keyword){
        return cardService.getResultBySearch(keyword);
    }
    @PostMapping("/insertCard")
    public void insertCard(@RequestBody CardVo cardVo) {
        cardService.insertCard(cardVo);
    }
    @PostMapping("/deleteCard")
    public void deleteCard(@RequestBody CardVo cardVo){
        log.info("삭제할 cardCode:{}", cardVo);
        cardService.deleteCardByPk(cardVo);
    }
    @PostMapping("/updateCardOrd")
    public void updateCardOrd(@RequestBody List<CardVo> list) {
        log.info("수정 cardOrd", list);
        cardService.updateCardOrd(list);
    }
    @PatchMapping("/updateCard")
    public void updateCard(@RequestBody CardVo cardVo) {
        log.info("수정할 cardOrd", cardVo);
        cardService.updateCardInfo(cardVo);
    }
    @PostMapping("/updateCardPosition")
    public void updateCardPosition(@RequestBody Map<String, List<CardVo>> cardsMap) {
        cardService.updateCardPosition(cardsMap.get("fromUpdatedOrders"), cardsMap.get("toUpdatedOrders"));
    }
}

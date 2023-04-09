package pjt.trello.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pjt.trello.board.model.ListVo;
import pjt.trello.board.service.ListService;
import pjt.trello.card.model.CardVo;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/list")
@Slf4j
public class ListController {
    @Autowired
    ListService listService;

    @PostMapping("/insertList")
    public void insertList(@Valid @RequestBody ListVo listVo) {
        listService.insertList(listVo);
    }
    @PatchMapping("/updateListInfo")
    public void updateListInfo(@Valid @RequestBody ListVo listVo) {
        log.info("수정 listInfo", listVo);
        listService.updateListInfo(listVo);
    }
    @PostMapping("/updateListOrdBulk")
    public void updateListOrdBulk(@RequestBody List<ListVo> list) {
        log.info("Updating list order");
        listService.updateListOrdBulk(list);
    }
    @DeleteMapping("/deleteList")
    public void deleteList(@RequestParam String listCode) {
        listService.deleteListByPk(listCode);
    }
}

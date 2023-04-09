package pjt.trello.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pjt.trello.board.model.BoardBrdAdminVo;
import pjt.trello.board.model.BoardBrdVo;
import pjt.trello.board.model.BoardDto;
import pjt.trello.board.service.BoardService;
import pjt.trello.common.Message;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/board")
@Slf4j
public class BoardController {
    @Autowired
    BoardService boardService;

    @GetMapping("/getBoardList")
    public List<BoardBrdVo> getBoardList(){
        return boardService.selectBoardList();
    }
    @PostMapping("/getBoardInfo")
    public List<BoardBrdVo> getBoardInfo(@RequestBody BoardDto boardDto){
        log.info("dto:{}", boardDto);
        return boardService.selectBrdByPk(boardDto);
    }
    @PostMapping("/getInfoBoard")
    public BoardBrdVo getInfoBoard(@RequestBody BoardBrdVo boardBrdVo){
        log.info("BoardVo:{}", boardBrdVo);
        return boardService.selectBoardInfo(boardBrdVo);
    }
    @PostMapping("/getBoardAdmin")
    public List<BoardBrdAdminVo> getBoardAdmin(@RequestBody BoardBrdAdminVo boardBrdAdminVo){
        log.info("보드 정보:{}", boardBrdAdminVo);
        return boardService.selectBrdAdmin(boardBrdAdminVo);
    }
    @PatchMapping("/updateAdmin")
    public void updateAdmin(@RequestBody BoardBrdAdminVo boardBrdAdminVo) {
        boardService.updateAdmin(boardBrdAdminVo);
    }
    @PatchMapping("/updateVisibility")
    public void updateVisibility(@RequestBody BoardBrdVo boardBrdVo) {
        boardService.updateVisibility(boardBrdVo);
    }
    @PatchMapping("/updateBrdInfo")
    public void updateBrdInfo(@RequestBody BoardBrdVo boardBrdVo) {
        boardService.updateBrdInfo(boardBrdVo);
    }
    @PostMapping("/insertBoardMember")
    public ResponseEntity<Message> insertBoardMember(@RequestBody BoardBrdAdminVo boardBrdAdminVo) {
        return boardService.insertBoardMember(boardBrdAdminVo);
    }
}

package pjt.trello.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pjt.trello.board.model.BoardBrdAdminVo;
import pjt.trello.board.model.BoardBrdVo;
import pjt.trello.board.model.BoardDto;
import pjt.trello.board.repository.BoardDao;
import pjt.trello.common.Message;
import pjt.trello.common.StatusEnum;
import pjt.trello.member.model.UserVo;
import pjt.trello.member.repository.MemberDao;

import java.util.*;

/**
 * 보드 서비스
 */
@Service
@Slf4j
public class BoardServiceImpl implements BoardService{
    @Autowired
    private BoardDao boardDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    Message message = new Message();
    HttpHeaders headers= new HttpHeaders();
    @Override
    public List<BoardBrdVo> selectBoardList() {
        return boardDao.selectBoardList();
    }

    @Override
    public BoardBrdVo selectBoardInfo(BoardBrdVo boardBrdVo) {
        return boardDao.selectBoardInfo(boardBrdVo);
    }

    @Override
    public List<BoardBrdVo> selectBrdByPk(BoardDto boardDto) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        /** Redis에서 보드코드에 해당하는 리스트 가져오기 */
        String brdCode = boardDto.getBrdCode();
        // tb_list 로 시작하는 모든 hash key ex) tb_list:LI0001, tb_list:LI0002
        Set<String> keys = hashOperations.getOperations().keys("tb_list:*");
        List<Map<String, String>> listData = new ArrayList<>();

        if (keys != null) {
            for (String key : keys) {
                // HGETALL ex) {listCode=LI0001,listName=list1, brdCode=brd1}
                Map<String, String> record = hashOperations.entries(key);
                if (brdCode.equals(record.get("brdCode"))) {
                    // 보드코드와 일치하면 list에 추가
                    listData.add(record);
                }
            }
        }
        log.info("리스트 데이터:{}", listData);

        /** Redis에서 리스트 코드에 해당하는 카드 가져오기 */
        List<Map<String, String>> cardData = new ArrayList<>();
        // tb_card 로 시작하는 모든 hash key ex) tb_card:CD0001, tb_card:CD0002
        Set<String> keys2 = hashOperations.getOperations().keys("tb_card:*");

        if (keys2 != null) {
            // 찾은 리스트만큼 반복
            for (Map<String, String> listRecord : listData) {
                // 각 리스트의 리스트 코드만 얻기
                String listCode = listRecord.get("listCode");
                for (String key2 : keys2) {
                    Map<String, String> record2 = hashOperations.entries(key2);
                    if (listCode.equals(record2.get("listCode"))) {
                        cardData.add(record2);
                    }
                }
            }
        }
        log.info("카드 데이터:{}", cardData);

        /**
         * 각각 불러온 listData와 cardData BoardBrdVo 형태로 결합 (JOIN 데이터 형태처럼 만드는 과정)
         * */
        List<BoardBrdVo> combinedData = new ArrayList<>();
        for (Map<String, String> listRecord : listData) {
            String listCode = listRecord.get("listCode");
            boolean isCardYn = false;
            // 리스트에 카드가 존재한 경우
            for (Map<String, String> cardRecord : cardData) {
                if (listCode.equals(cardRecord.get("listCode"))) {
                    isCardYn = true;
                    BoardBrdVo boardBrdVo = new BoardBrdVo();
                    boardBrdVo.setListCode(listRecord.get("listCode"));
                    boardBrdVo.setListName(listRecord.get("listName"));
                    boardBrdVo.setListOrd(listRecord.get("listOrd"));
                    boardBrdVo.setCardCode(cardRecord.get("cardCode"));
                    boardBrdVo.setCardOrd(cardRecord.get("cardOrd"));
                    boardBrdVo.setTitle(cardRecord.get("title"));
                    boardBrdVo.setContent(cardRecord.get("content"));
                    combinedData.add(boardBrdVo);
                }
            }
            // 카드는 없고 리스트만 존재한 경우
            if (!isCardYn) {
                BoardBrdVo boardBrdVo = new BoardBrdVo();
                boardBrdVo.setListCode(listRecord.get("listCode"));
                boardBrdVo.setListName(listRecord.get("listName"));
                boardBrdVo.setListOrd(listRecord.get("listOrd"));
                combinedData.add(boardBrdVo);
            }
        }
        log.info("결합 데이터:{}", combinedData);
//        return boardDao.selectBrdByPk(boardDto);
        return combinedData;
    }

    @Override
    public List<BoardBrdAdminVo> selectBrdAdmin(BoardBrdAdminVo boardBrdAdminVo) {
        return boardDao.selectBrdAdmin(boardBrdAdminVo);
    }

    @Override
    public void updateAdmin(BoardBrdAdminVo boardBrdAdminVo) {
        boardDao.updateAdmin(boardBrdAdminVo);
    }

    @Override
    public void updateVisibility(BoardBrdVo boardBrdVo) {
        boardDao.updateVisibility(boardBrdVo);
    }

    @Override
    public void updateBrdInfo(BoardBrdVo boardBrdVo) {
        boardDao.updateBrdInfo(boardBrdVo);
    }

    @Override
    public ResponseEntity<Message> insertBoardMember(BoardBrdAdminVo boardBrdAdminVo) {
        // 아이디 존재 여부
        int idchk = memberDao.memberIdCheck(boardBrdAdminVo.getUserId());
        // 삭제 여부
        String delYn = memberDao.memberCheck(boardBrdAdminVo.getUserId());
        log.info("delYn:{}", delYn);
        // 보드 멤버 리스트
        List<BoardBrdAdminVo> adminList = boardDao.selectBrdAdmin(boardBrdAdminVo);

        // 아이디가 존재할 경우
        if(idchk>0 && delYn.equals("N")){
            // 이미 멤버일경우
            for (BoardBrdAdminVo admin : adminList) {
                if (admin.getUserId().equals(boardBrdAdminVo.getUserId())) {
                    Message message = new Message(StatusEnum.BAD_REQUEST, "이미 보드 멤버인 회원입니다.");
                    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
                }
            }
            boardDao.insertBoardMember(boardBrdAdminVo);
            Message message = new Message(StatusEnum.OK, "정상적으로 공유되었습니다.");
            return new ResponseEntity<>(message, HttpStatus.OK);
        }else if(idchk>0 && delYn.equals("Y")){
            Message message = new Message(StatusEnum.BAD_REQUEST, "탈퇴한 회원입니다.");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }else{
            Message message = new Message(StatusEnum.BAD_REQUEST, "존재하지 않는 회원입니다.");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }
}

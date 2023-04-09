package pjt.trello.card.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pjt.trello.card.model.CardVo;
import pjt.trello.card.repository.CardDao;

import javax.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class CardServiceImpl implements CardService{
    @Autowired
    CardDao cardDao;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Override
    public void insertCard(CardVo cardVo) {
        // cardCode에 해당하는 카드 불러오기
//        CardVo getCardInfo = cardDao.getInfoCardByPk(cardVo);
//        // 객체 빈값 여부 확인 (카드가 존재하지 않으면)
//        if(getCardInfo == null){
//            String lastCd = cardDao.getMaxCardCode();
//            // 마지막 cardCode 값에서 번호만 받기
//            int lastNum = Integer.parseInt(lastCd.substring(2));
//            // 새로운 cardCode
//            String newCd = "CD" + String.format("%04d", lastNum + 1);
//            // cardCode set
//            cardVo.setCardCode(newCd);
//            // insert
//            cardDao.insertCard(cardVo);
//        }else{
//           // update
//           cardDao.updateCardInfo(cardVo);
//        }
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        CardVo getCardInfo = null;
        if(cardVo.getCardCode()!=null){
            getCardInfo = getInfoCardByPk(cardVo);
        }

        // 객체 빈값 여부 확인 (카드가 존재하지 않으면)
        if(getCardInfo == null){
            // tb_card로 시작하는 모든 key 받기
            Set<String> listKeys = hashOperations.getOperations().keys("tb_card:*");
            int maxListCodeNumber = 0;

            // max 번호 얻기
            if (listKeys != null) {
                for (String key : listKeys) {
                    Map<String, String> record = hashOperations.entries(key);
                    String listCode = record.get("cardCode");
                    if (listCode != null) {
                        // CD 제거
                        int listCodeNumber = Integer.parseInt(listCode.substring(2));
                        if (listCodeNumber > maxListCodeNumber) {
                            maxListCodeNumber = listCodeNumber;
                        }
                    }
                }
            }else{
                maxListCodeNumber = 0;
            }
            int newCodeNumber = maxListCodeNumber + 1;
            String newCardCode = "CD" + String.format("%04d", newCodeNumber);
            /** Redis 사용 */
            // JSON 형태 만들기 위해 ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> listData = new LinkedHashMap<>();
            listData.put("cardCode", newCardCode);
            listData.put("title", cardVo.getTitle());
            listData.put("content", cardVo.getContent());
            listData.put("listCode", cardVo.getListCode());
            listData.put("cardOrd", Integer.toString(cardVo.getCardOrd()));

            try {
                String jsonString = objectMapper.writeValueAsString(listData);
                // Redis 저장
                String key = "tb_card:" + newCardCode;
                hashOperations.putAll(key, listData);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }else{
            updateCardInfo(cardVo);
            log.info("업데이트");
        }
    }

    @Override
    public CardVo getInfoCardByPk(CardVo cardVo) {
        CardVo cardVoData = new CardVo();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        // tb_card 로 시작하는 키
        Set<String> listKeys = hashOperations.getOperations().keys("tb_card:*");
        if (listKeys != null) {
            for (String key : listKeys) {
                Map<String, String> record = hashOperations.entries(key);
                if (cardVo.getCardCode().equals(record.get("cardCode"))) {
                    cardVoData.setCardCode(record.get("cardCode"));
                    cardVoData.setTitle(record.get("title"));
                    cardVoData.setContent(record.get("content"));
                    cardVoData.setListCode(record.get("listCode"));
                    cardVoData.setListName(record.get("listCode"));
                }
            }
        }
        return cardVoData;
//        return cardDao.getInfoCardByPk(cardVo);
    }

    @Override
    public List<CardVo> getResultBySearch(String keyword) {
        return cardDao.getResultBySearch(keyword);
    }

    @Override
    public void updateCardInfo(CardVo cardVo) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        if (cardVo.getContent() != null) {
            hashOperations.put("tb_card:"+ cardVo.getCardCode(), "content", cardVo.getContent());
        }
//        cardDao.updateCardInfo(cardVo);
    }

    @Override
    public void updateCardOrd(List<CardVo> list) {
        cardDao.updateCardOrd(list);
    }
    @Override
    public void deleteCardByPk(CardVo cardVo) {
        String cardCode = cardVo.getCardCode();
        redisTemplate.delete("tb_card:"+ cardCode);

        /** 해당 카드의 activity 정보도 삭제 */
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        Set<String> acKeys = hashOperations.getOperations().keys("tb_activity:*");
        if (acKeys != null) {
            for (String key : acKeys) {
                Map<String, String> record = hashOperations.entries(key);
                if (cardCode.equals(record.get("cardCode"))) {
                    redisTemplate.delete(key);
                }
            }
        }
        /** 해당 카드의 attachment 정보도 삭제 */
        Set<String> acKeys2 = hashOperations.getOperations().keys("tb_attachment:*");
        if (acKeys2 != null) {
            for (String key : acKeys2) {
                Map<String, String> record = hashOperations.entries(key);
                if (cardCode.equals(record.get("cardCode"))) {
                    redisTemplate.delete(key);
                }
            }
        }
//        cardDao.deleteCardByPk(cardVo);
    }
    @Override
    public void updateCardPosition(List<CardVo> fromUpdatedOrders, List<CardVo> toUpdatedOrders) {
        // 드래그 시작한 리스트의 카드 상태 업데이트        
//        for (CardVo cardVo : fromUpdatedOrders) {
//            cardDao.updateCardPosition(cardVo);
//        }
//        // 드래그 종료한 리스트의 카드 상태 업데이트
//        for (CardVo cardVo : toUpdatedOrders) {
//            cardDao.updateCardPosition(cardVo);
//        }
        
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        for (CardVo cardVo : fromUpdatedOrders) {
            log.info("시작 카드들:{}", cardVo);
            hashOperations.put("tb_card:"+ cardVo.getCardCode(), "listCode", cardVo.getListCode());
            hashOperations.put("tb_card:"+ cardVo.getCardCode(), "cardOrd", Integer.toString(cardVo.getCardOrd()));
        }
        // 드래그 종료한 리스트의 카드 상태 업데이트
        for (CardVo cardVo : toUpdatedOrders) {
            log.info("종료 카드들:{}", cardVo);
            hashOperations.put("tb_card:"+ cardVo.getCardCode(), "listCode", cardVo.getListCode());
            hashOperations.put("tb_card:"+ cardVo.getCardCode(), "cardOrd", Integer.toString(cardVo.getCardOrd()));
        }
    }
}

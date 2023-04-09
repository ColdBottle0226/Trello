package pjt.trello.board.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pjt.trello.board.model.ListVo;
import pjt.trello.board.repository.ListDao;

import java.util.*;

@Service
@Slf4j
public class ListServiceImpl implements ListService {
    @Autowired
    ListDao listDao;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Override
    public void insertList(ListVo listVo) {
//        // listCode에 해당하는 리스트 불러오기
//        ListVo getListInfo = listDao.getInfoListByPk(listVo);
//        // 마지막 listCode 번호만
//        if (getListInfo == null) {
//            String lastCd = listDao.getMaxListCode();
//            // 마지막 listCode 값에서 번호만 받기
//            int lastNum = Integer.parseInt(lastCd.substring(2));
//            // 새로운 listCode
//            String newCd = "LI" + String.format("%04d", lastNum + 1);
//            listVo.setListCode(newCd);
//            // insert
//            listDao.insertList(listVo);
//        } else {
//            // update
//        }
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        // tb_list로 시작하는 모든 key 받기
        Set<String> listKeys = hashOperations.getOperations().keys("tb_list:*");
        int maxListCodeNumber = 0;

        // max 번호 얻기
        if (listKeys != null) {
            for (String key : listKeys) {
                Map<String, String> record = hashOperations.entries(key);
                String listCode = record.get("listCode");
                if (listCode != null) {
                    int listCodeNumber = Integer.parseInt(listCode.substring(2));
                    if (listCodeNumber > maxListCodeNumber) {
                        maxListCodeNumber = listCodeNumber;
                    }
                }
            }
        }else{
            maxListCodeNumber = 0;
        }
        log.info("최대키:{}",maxListCodeNumber);
        // max 번호 +1
        int newCodeNumber = maxListCodeNumber + 1;
        String newListCode = "LI" + String.format("%04d", newCodeNumber);
        
        
        /** Redis 사용 */
        // JSON 형태 만들기 위해 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, String> listData = new LinkedHashMap<>();
        listData.put("listCode", newListCode);
        listData.put("listName", listVo.getListName());
        listData.put("brdCode", listVo.getBrdCode());
        listData.put("listOrd", Integer.toString(listVo.getListOrd()));

        try {
            String jsonString = objectMapper.writeValueAsString(listData);
            // Redis 저장
            String key = "tb_list:" + newListCode;
            hashOperations.putAll(key, listData);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void updateListOrdBulk(List<ListVo> list) {

        for (int i = 0; i < list.size(); i++) {
            ListVo listVo = list.get(i);
            listVo.setListOrd(i + 1);
            // ex) tb_list:LI0001
            final String key = "tb_list:" + listVo.getListCode();
            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            hashOperations.put(key, "listOrd", Integer.toString(listVo.getListOrd()));
//            listDao.updateListOrd(listVo);
        }
    }
    @Override
    public void updateListInfo(ListVo listVo) {
        log.info("수정할 리스트 정보:{}", listVo);
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put("tb_list:"+listVo.getListCode(), "listName", listVo.getListName());
//        listDao.updateListInfo(listVo);
    }
    @Override
    public void deleteListByPk(String listCode) {
        // 리스트 삭제
        redisTemplate.delete("tb_list:"+listCode);

        // 리스트에 해당하는 카드들도 전부 삭제
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        Set<String> keys = hashOperations.getOperations().keys("tb_card:*");

        if (keys != null) {
            for (String key : keys) {
                Map<String, String> record = hashOperations.entries(key);
                // 카드의 listCode가 같으면
                if (listCode.equals(record.get("listCode"))) {
                    redisTemplate.delete(key);
                }
            }
        }
        //        listDao.deleteListByPk(listCode);
    }
}
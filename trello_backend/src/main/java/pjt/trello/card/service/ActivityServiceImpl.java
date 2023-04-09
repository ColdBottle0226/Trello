package pjt.trello.card.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pjt.trello.board.model.BoardBrdVo;
import pjt.trello.card.model.ActivityVo;
import pjt.trello.card.repository.ActivityDao;
import pjt.trello.member.model.UserVo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class ActivityServiceImpl implements ActivityService{
    @Autowired
    ActivityDao activityDao;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Override
    public void insertActivity(ActivityVo activityVo) {
        // 마지막 activity code
//        String lastCd = activityDao.getMaxActivityCode();
//        // 마지막 code에서 번호만 받기
//        int lastNum = Integer.parseInt(lastCd.substring((2)));
//        // 새로운 activity code
//        String newCd = "AC" + String.format("%04d", lastNum + 1);
//        activityVo.setAcCode(newCd);
//        activityDao.insertActivity(activityVo);

        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        // tb_list로 시작하는 모든 key 받기
        Set<String> acKeys = hashOperations.getOperations().keys("tb_activity:*");
        int maxListCodeNumber = 0;
        // max 번호 얻기
        if (acKeys != null) {
            for (String key : acKeys) {
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
        int newCodeNumber = maxListCodeNumber + 1;
        String newACCode = "AC" + String.format("%04d", newCodeNumber);

        /** Redis 사용 */
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formatDateTime = now.format(formatter);

        // JSON 형태 만들기 위해 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, String> acData = new LinkedHashMap<>();
        acData.put("acCode", newACCode);
        acData.put("content", activityVo.getContent());
        acData.put("cmntId", activityVo.getCmntId());
        acData.put("creId", activityVo.getCreId());
        acData.put("creDt", formatDateTime);
        acData.put("cardCode", activityVo.getCardCode());

        try {
            String jsonString = objectMapper.writeValueAsString(acData);
            // Redis 저장
            String key = "tb_activity:" + newACCode;
            hashOperations.putAll(key, acData);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<ActivityVo> getInfoActivityByPk(ActivityVo activityVo) {
//        return activityDao.getInfoActivityByPk(activityVo);
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        List<ActivityVo> dataList = new ArrayList<>();

        // card에 맞는 activity 가져오기
        Set<String> acKeys = hashOperations.getOperations().keys("tb_activity:*");
        if (acKeys != null) {
            for (String key : acKeys) {
                // HGETALL
                Map<String, String> record = hashOperations.entries(key);
                // 해당 cardCode와 일치하면
                if (activityVo.getCardCode().equals(record.get("cardCode"))) {
                    ActivityVo getActivityVo = new ActivityVo();
                    getActivityVo.setAcCode(record.get("acCode"));
                    getActivityVo.setContent(record.get("content"));
                    getActivityVo.setCmntId(record.get("cmntId"));
                    getActivityVo.setCreDt(record.get("creDt"));
                    getActivityVo.setCreId(record.get("creId"));
                    getActivityVo.setCardCode(record.get("cardCode"));
                    dataList.add(getActivityVo);
                }
            }
        }
        return dataList;
    }
    @Override
    public void updateActivityDelYn(ActivityVo activityVo) {
        log.info("삭제할 정보:{}", activityVo);
        redisTemplate.delete("tb_activity:"+ activityVo.getAcCode());
        //        activityDao.updateActivityDelYn(activityVo);
    }
    @Override
    public void updateActivityInfo(ActivityVo activityVo) {
        log.info("수정할 정보:{}", activityVo);
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put("tb_activity:"+activityVo.getAcCode(), "content", activityVo.getContent());
        if (activityVo.getCmntId() != null) {
            hashOperations.put("tb_activity:"+activityVo.getAcCode(), "cmntId", activityVo.getCmntId());
        }
        //        activityDao.updateActivityInfo(activityVo);
    }
}

package pjt.trello.attach.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pjt.trello.attach.model.AttachmentVo;
import pjt.trello.attach.repository.AttachDao;
import pjt.trello.board.model.BoardBrdVo;
import pjt.trello.card.model.CardVo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class AttachServiceImpl implements AttachService{
    @Autowired
    AttachDao attachDao;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Override
    public void insertAttach(AttachmentVo attachmentVo) {

//        String lastCd = attachDao.getMaxAttachCode();
//        // 마지막 attachCode 값에서 번호만 받기
//        int lastNum = Integer.parseInt(lastCd.substring(2));
//        // 새로운 cardCode
//        String newCd = "AT" + String.format("%04d", lastNum + 1);
//        // cardCode set
//        attachmentVo.setAttachCode(newCd);
//        // insert
//        attachDao.insertAttach(attachmentVo);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formatDateTime = now.format(formatter);

        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        // tb_attachment로 시작하는 모든 key 받기
        Set<String> listKeys = hashOperations.getOperations().keys("tb_attachment:*");
        int maxListCodeNumber = 0;

        // max 번호 얻기
        if (listKeys != null) {
            for (String key : listKeys) {
                Map<String, String> record = hashOperations.entries(key);
                String listCode = record.get("attachCode");
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
        String newListCode = "AT" + String.format("%04d", newCodeNumber);


        /** Redis 사용 */
        // JSON 형태 만들기 위해 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, String> listData = new LinkedHashMap<>();
        listData.put("attachCode", newListCode);
        listData.put("fileLoc", attachmentVo.getFileLoc());
        listData.put("creDt", formatDateTime);
        listData.put("cardCode", attachmentVo.getCardCode());

        try {
            String jsonString = objectMapper.writeValueAsString(listData);
            // Redis 저장
            String key = "tb_attachment:" + newListCode;
            hashOperations.putAll(key, listData);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
    @Override
    public List<AttachmentVo> getInfoAttachByPk(AttachmentVo attachmentVo) {
        AttachmentVo attachVoData = new AttachmentVo();
        List<AttachmentVo> attachData = new ArrayList<>();

        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        // tb_atttachment 로 시작하는 키
        Set<String> listKeys = hashOperations.getOperations().keys("tb_attachment:*");
        if (listKeys != null) {
            for (String key : listKeys) {
                Map<String, String> record = hashOperations.entries(key);
                if (attachmentVo.getCardCode().equals(record.get("cardCode"))) {
                    attachVoData.setAttachCode(record.get("attachCode"));
                    attachVoData.setFileLoc(record.get("fileLoc"));
                    attachVoData.setCreDt(record.get("creDt"));
                    attachVoData.setCardCode(record.get("cardCode"));
                    attachData.add(attachVoData);
                }
            }
        }
        return attachData;
//        return attachDao.getInfoAttachByPk(attachmentVo);
    }
    @Override
    public void deleteAttachByPk(String attchCode) {
        redisTemplate.delete("tb_attachment:"+ attchCode);

//        attachDao.deleteAttachByPk(attchCode);
    }
}

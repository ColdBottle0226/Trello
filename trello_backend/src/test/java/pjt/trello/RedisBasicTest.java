package pjt.trello;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class RedisBasicTest{
    @Autowired
    RedisTemplate<String,String> StringredisTemplate;  //redisTemplate autowired
    @Autowired
    RedisTemplate<String,Object> redisTemplate;  //redisTemplate autowired

    @Test
    @DisplayName("Redis 기본 key,value 테스트")
    void redisConnectionTest(){
        // key
        final String key = "a";
        // value
        final String data = "1";

        final ValueOperations<String,String> valueOperations = StringredisTemplate.opsForValue();
        valueOperations.set(key,data);

        // s는 redis에서 key에 해당하는 value 값
        final String s = valueOperations.get(key);
        // s와 data가 같은지 확인
        Assertions.assertThat(s).isEqualTo(data);
    }
    @Test
    @DisplayName("Redis 기본 object 테스트")
    void redisHashTest(){
        // key
        final String key = "tb_user_seq:테스트7777";
        Map<Object, Object> value = redisTemplate.opsForHash().entries(key);
        System.out.println("값:" + value);
    }

    @Test
    @DisplayName("HMSET insert 테스트")
    void insertDataUsingHMSET() {

        String key = "tb_user:테스트7777";
        Map<String, String> userData = new HashMap<>();
        userData.put("user_id", "테스트7777");
        userData.put("user_name", "김병찬");
        userData.put("user_email", "kbc3978@naver.com");
        userData.put("password", "Kbc@3324865");
        userData.put("user_status", "00");
        userData.put("del_yn", "N");
        userData.put("login_fail_cnt", "0");
        userData.put("login_dt", "20230330164814");
        userData.put("cre_dt", "20230323173242");
        userData.put("udt_dt", "");
        userData.put("secret_key", "b9b18a42-483c-4c6d-af7b-db29dc189ed8");

        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(key, userData);

        // key에 해당하는 hash데이터 불러오기
        Map<String, String> retrievedData = hashOperations.entries(key);
        Assertions.assertThat(retrievedData).isEqualTo(userData);
    }
}

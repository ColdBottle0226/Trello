package pjt.trello.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pjt.trello.member.model.UserVo;
import pjt.trello.member.repository.MemberDao;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** UserDetailsService 구현 클래스, Redis에서 사용자 정보 가지고 온다 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    MemberDao memberDao;
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserVo userVo = memberDao.memberLogin(new UserVo(userId));
        if (userVo == null) {
            throw new UsernameNotFoundException("해당하는 아이디를 찾을 수 없습니다.: " + userId);
        }
        return createUserDetails(userVo);
    }
    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(UserVo userVo) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 기본적인 ROLE_USER 권한 세팅
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(userVo.getUserId(), userVo.getPassword(), authorities);
    }
}



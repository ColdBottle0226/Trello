package pjt.trello.member.repository;

import org.apache.ibatis.annotations.Mapper;
import pjt.trello.member.model.UserVo;

@Mapper
public interface MemberDao {
    public void memberJoin(UserVo userVo);
    public int memberIdCheck(String userId);
    public int memberEmailCheck(String userEmail);
    public UserVo memberLogin(UserVo userVo);
    public String memberCheck(String userId);
    public UserVo getMemberInfo(String secretKey);
    public void updateSecretKey(UserVo userVo);
    public void updateLastLoginDt(UserVo userVo);
    public void updateMemberDelYn(UserVo userVo);
    public void updateMemberInfo(UserVo userVo);
}

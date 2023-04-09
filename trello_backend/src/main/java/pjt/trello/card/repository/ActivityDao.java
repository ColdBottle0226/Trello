package pjt.trello.card.repository;

import org.apache.ibatis.annotations.Mapper;
import pjt.trello.attach.model.AttachmentVo;
import pjt.trello.card.model.ActivityVo;

import java.util.List;

@Mapper
public interface ActivityDao {
    public void insertActivity(ActivityVo activityVo);
    public String getMaxActivityCode();
    public List<ActivityVo> getInfoActivityByPk(ActivityVo activityVo);
    public void updateActivityDelYn(ActivityVo activityVo);
    public void updateActivityInfo(ActivityVo activityVo);
}

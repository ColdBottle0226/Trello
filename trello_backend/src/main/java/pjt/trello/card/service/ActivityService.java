package pjt.trello.card.service;

import pjt.trello.card.model.ActivityVo;

import java.util.List;

public interface ActivityService {
    public void insertActivity(ActivityVo activityVo);
    public List<ActivityVo> getInfoActivityByPk(ActivityVo activityVo);
    public void updateActivityDelYn(ActivityVo activityVo);
    public void updateActivityInfo(ActivityVo activityVo);
}

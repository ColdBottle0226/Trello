package pjt.trello.card.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pjt.trello.attach.model.AttachmentVo;
import pjt.trello.card.model.ActivityVo;
import pjt.trello.card.service.ActivityService;

import java.util.List;

@RestController
@RequestMapping("/activity")
@Slf4j
public class ActivityController {
    @Autowired
    ActivityService activityService;

    @PostMapping("/insertActivity")
    public void insertActivity(@RequestBody ActivityVo activityVo) {
        log.info("activity:{}", activityVo);
        activityService.insertActivity(activityVo);
    }
    @PostMapping("/getActivityInfo")
    public List<ActivityVo> getActivityInfo(@RequestBody ActivityVo activityVo) {
        log.info("activityVo:{}", activityVo);
        return activityService.getInfoActivityByPk(activityVo);
    }
    @PatchMapping("/updateActivityDelYn")
    public void updateActivityDelYn(@RequestBody ActivityVo activityVo) {
        activityService.updateActivityDelYn(activityVo);
    }
    @PatchMapping("/updateActivityInfo")
    public void updateActivityInfo(@RequestBody ActivityVo activityVo) {
        activityService.updateActivityInfo(activityVo);
    }

}

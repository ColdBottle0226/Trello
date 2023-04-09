package pjt.trello.attach.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pjt.trello.attach.model.AttachmentVo;
import pjt.trello.attach.service.AttachService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/attach")
public class AttachmentController {
    @Autowired
    AttachService attachService;

    @PostMapping("/insertAttach")
    public void insertAttach(@RequestBody AttachmentVo attachmentVo) {
        log.info("업로드 파일:{}", attachmentVo);
        attachService.insertAttach(attachmentVo);
    }
    @PostMapping("/getAttachInfo")
    public List<AttachmentVo> getAttachInfo(@RequestBody AttachmentVo attachmentVo) {
        log.info("업로드 파일:{}", attachmentVo);
        return attachService.getInfoAttachByPk(attachmentVo);
    }
    @DeleteMapping("/deleteAttach")
    public void deleteAttach(@RequestParam String attachCode){
        attachService.deleteAttachByPk(attachCode);
    }

}

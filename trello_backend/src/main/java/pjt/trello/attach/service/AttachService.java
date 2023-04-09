package pjt.trello.attach.service;

import pjt.trello.attach.model.AttachmentVo;

import java.util.List;

public interface AttachService {
    public void insertAttach(AttachmentVo attachmentVo);
    public List<AttachmentVo> getInfoAttachByPk(AttachmentVo attachmentVo);
    public void deleteAttachByPk(String attachCode);
}

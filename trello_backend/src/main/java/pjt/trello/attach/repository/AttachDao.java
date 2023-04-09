package pjt.trello.attach.repository;

import org.apache.ibatis.annotations.Mapper;
import pjt.trello.attach.model.AttachmentVo;

import java.util.List;

@Mapper
public interface AttachDao {
    public void insertAttach(AttachmentVo attachmentVo);
    public String getMaxAttachCode();
    public List<AttachmentVo> getInfoAttachByPk(AttachmentVo attachmentVo);
    public void deleteAttachByPk(String attachCode);
}

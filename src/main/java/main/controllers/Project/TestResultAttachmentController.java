package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.model.db.dao.project.TestResultAttachmentDao;
import main.model.dto.project.TestResultAttachmentDto;
import main.model.dto.settings.UserDto;
import org.apache.commons.io.FilenameUtils;

import java.util.List;

public class TestResultAttachmentController extends BaseController<TestResultAttachmentDto> {
    private final TestResultAttachmentDao testResultAttachmentDao;

    public TestResultAttachmentController(UserDto user) {
        super(user);
        testResultAttachmentDao = new TestResultAttachmentDao();
    }

    @Override
    public List<TestResultAttachmentDto> get(TestResultAttachmentDto entity) throws AqualityException {
        return getResultAttachment(testResultAttachmentDao.searchAll(entity));
    }

    @Override
    public TestResultAttachmentDto create(TestResultAttachmentDto entity) throws AqualityException {
        return testResultAttachmentDao.create(entity);
    }

    @Override
    public boolean delete(TestResultAttachmentDto entity) throws AqualityException {
        return testResultAttachmentDao.delete(entity);
    }

    private List<TestResultAttachmentDto> getResultAttachment(List<TestResultAttachmentDto> testResultAttachmentDto) {
        testResultAttachmentDto.forEach(attachmentDto -> {
            attachmentDto.setName(FilenameUtils.getName(attachmentDto.getPath()));
        });
        return testResultAttachmentDto;
    }
}

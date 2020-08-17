package main.controllers;

import main.controllers.Administration.EmailSettingsController;
import main.controllers.Administration.StepTypeController;
import main.controllers.Administration.UserController;
import main.controllers.Project.*;
import main.model.dto.project.*;
import main.model.dto.settings.EmailSettingsDto;
import main.model.dto.settings.UserDto;

public class ControllerFactory {
    private UserDto user;

    public ControllerFactory(UserDto user){
        this.user = user;
    }

    public ProjectController getHandler(ProjectDto entity) {
        return new ProjectController(user);
    }
    public BodyPatternController getHandler(BodyPatternDto entity) {
        return new BodyPatternController(user);
    }
    public FinalResultController getHandler(FinalResultDto entity) {
        return new FinalResultController(user);
    }
    public ImportController getHandler(ImportDto entity) {
        return new ImportController(user);
    }
    public APITokenController getHandler(APITokenDto entity) {
        return new APITokenController(user);
    }
    public MilestoneController getHandler(MilestoneDto entity) {
        return new MilestoneController(user);
    }
    public ProjectUserController getHandler(ProjectUserDto entity) {
        return new ProjectUserController(user);
    }
    public ResultController getHandler(TestResultDto entity) {
        return new ResultController(user);
    }
    public ResultResolutionController getHandler(ResultResolutionDto entity) {
        return new ResultResolutionController(user);
    }
    public SuiteController getHandler(TestSuiteDto entity) {
        return new SuiteController(user);
    }
    public SuiteDashboardController getHandler(SuiteDashboardDto entity) {
        return new SuiteDashboardController(user);
    }
    public Test2SuiteController getHandler(Test2SuiteDto entity) {
        return new Test2SuiteController(user);
    }
    public TestController getHandler(TestDto entity) {
        return new TestController(user);
    }
    public TestRunController getHandler(TestRunDto entity) {
        return new TestRunController(user);
    }
    public UserController getHandler(UserDto entity) {
        return new UserController(user);
    }
    public EmailSettingsController getHandler(EmailSettingsDto entity) {
        return new EmailSettingsController(user);
    }
    public StepController getHandler(StepDto entity) {
        return new StepController(user);
    }
    public StepResultController getHandler(StepResultDto entity) {
        return new StepResultController(user);
    }
    public StepTypeController getHandler(StepTypeDto entity) {
        return new StepTypeController(user);
    }
    public PredefinedResolutionController getHandler(PredefinedResolutionDto entity) {
        return new PredefinedResolutionController(user);
    }
    public IssueController getHandler(IssueDto entity) {
        return new IssueController(user);
    }
    public TestResultAttachmentController getHandler(TestResultAttachmentDto entity) {
        return new TestResultAttachmentController(user);
    }
}

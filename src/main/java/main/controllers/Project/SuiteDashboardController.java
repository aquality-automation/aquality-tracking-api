package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.RPException;
import main.exceptions.RPPermissionsException;
import main.model.db.dao.project.Suite2DashboardDao;
import main.model.db.dao.project.SuiteDashboardDao;
import main.model.dto.Suite2DashboardDto;
import main.model.dto.SuiteDashboardDto;
import main.model.dto.TestSuiteDto;
import main.model.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SuiteDashboardController extends BaseController<SuiteDashboardDto> {
    private Suite2DashboardDao suite2DashboardDao;
    private SuiteDashboardDao suiteDashboardDao;
    private SuiteController suiteController;

    public SuiteDashboardController(UserDto user) {
        super(user);
        suite2DashboardDao = new Suite2DashboardDao();
        suiteDashboardDao = new SuiteDashboardDao();
        suiteController = new SuiteController(user);
    }

    @Override
    public SuiteDashboardDto create(SuiteDashboardDto template) throws RPException {
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            template.setId(suiteDashboardDao.create(template).getId());
            updateSuites2Dashboard(template);
            return template;
        }else{
            throw new RPPermissionsException("Account is not allowed to create Suite Dashboards", baseUser);
        }
    }

    @Override
    public List<SuiteDashboardDto> get(SuiteDashboardDto template) throws RPException{
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            return fillSuiteDashboards(suiteDashboardDao.searchAll(template));
        }else{
            throw new RPPermissionsException("Account is not allowed to view Suite Dashboards", baseUser);
        }
    }

    @Override
    public boolean delete(SuiteDashboardDto template) throws RPException{
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            return suiteDashboardDao.delete(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to delete Suite Dashboards", baseUser);
        }
    }

    private List<SuiteDashboardDto> fillSuiteDashboards(List<SuiteDashboardDto> dashboards) throws RPException {
        if(dashboards.size() < 1){
            return dashboards;
        }
        List<SuiteDashboardDto> filledSuiteDashboards = new ArrayList<>();
        TestSuiteDto testSuiteDto = new TestSuiteDto();
        testSuiteDto.setProject_id(dashboards.get(0).getProject_id());
        List<TestSuiteDto> projectSuites = suiteController.get(testSuiteDto, false);
        for (SuiteDashboardDto filledSuiteDashboard : dashboards) {
            Suite2DashboardDto suite2DashboardDto = new Suite2DashboardDto();
            suite2DashboardDto.setDashboard_id(filledSuiteDashboard.getId());
            List<Suite2DashboardDto> suite2Dashboards = suite2DashboardDao.searchAll(suite2DashboardDto);
            filledSuiteDashboard.setSuites(
                    projectSuites.stream().filter(x ->
                            suite2Dashboards.stream().filter(y -> y.getSuite_id().equals(x.getId())).findFirst().orElse(null)
                                    != null).collect(Collectors.toList()));

            filledSuiteDashboards.add(filledSuiteDashboard);
        }
        return filledSuiteDashboards;
    }

    private void updateSuites2Dashboard(SuiteDashboardDto template) throws RPException {
        if(template.getSuites() != null){
            for (TestSuiteDto suite: template.getSuites()) {
                Suite2DashboardDto suite2DashboardDto = new Suite2DashboardDto();
                suite2DashboardDto.setSuite_id(suite.getId());
                suite2DashboardDto.setDashboard_id(template.getId());
                suite2DashboardDao.create(suite2DashboardDto);
            }
        }
    }
}

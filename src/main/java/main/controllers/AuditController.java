package main.controllers;

import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.exceptions.AqualityUserException;
import main.model.db.dao.audit.*;
import main.model.db.dao.project.ProjectDao;
import main.model.db.dao.project.UserDao;
import main.model.dto.*;
import main.utils.ExcelUtils;
import main.utils.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AuditController extends BaseController<AuditDto> {
    private AuditDao auditDao;
    private AuditStatusDao auditStatusDao;
    private ServiceDao serviceDao;
    private ProjectDao projectDao;
    private AuditorsDao auditorsDao;
    private AuditCommentsDao auditCommentsDao;
    private AuditAttachmentsDao auditAttachmentsDao;
    private AuditStatisticDao auditStatisticDao;
    private UserDao userDao;
    private SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");

    public AuditController(UserDto user) {
        super(user);
        auditDao = new AuditDao();
        auditStatusDao = new AuditStatusDao();
        serviceDao = new ServiceDao();
        projectDao = new ProjectDao();
        auditorsDao = new AuditorsDao();
        auditCommentsDao = new AuditCommentsDao();
        auditStatisticDao = new AuditStatisticDao();
        auditAttachmentsDao = new AuditAttachmentsDao();
        userDao = new UserDao();
    }

    @Override
    public List<AuditDto> get(AuditDto searchTemplate) throws AqualityException {
        List<AuditDto> audits = auditDao.searchAll(searchTemplate);
        Integer projectId;
        try{
            projectId = searchTemplate.getId() == null
                ? searchTemplate.getProject_id()
                : audits.get(0).getProject_id();
        }catch (IndexOutOfBoundsException e){
            throw new AqualityException("The Audit you trying to access is not present!");
        }
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(projectId).isViewer()){
            return completeAudits(audits);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to view Audits.", baseUser);
        }
    }
    public List<ServiceDto> get(ServiceDto template) throws AqualityException {
        return serviceDao.searchAll(template);
    }
    public List<AuditCommentDto> get(AuditCommentDto template) throws AqualityException {
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()){
            return completeComments(auditCommentsDao.searchAll(template));
        }else{
            throw new AqualityPermissionsException("Account is not allowed to view Audit Comments.", baseUser);
        }
    }
    public List<AuditStatisticDto> get(AuditStatisticDto template) throws AqualityException {
        if(baseUser.isFromGlobalManagement()){
            return completeAuditStatistic(auditStatisticDao.searchAll(template));
        }else{
            throw new AqualityPermissionsException("Account is not allowed to view Audits.", baseUser);
        }
    }
    public List<AuditAttachmentDto> get(AuditAttachmentDto searchTemplate) throws AqualityException {
        AuditDto audit = new AuditDto();
        audit.setId(searchTemplate.getAudit_id());
        Integer projectId;
        try{
            projectId = auditDao.searchAll(audit).get(0).getProject_id();
        }catch (IndexOutOfBoundsException e){
            throw new AqualityException("The Audit you trying to access is not present!");
        }
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(projectId).isViewer()){
            return auditAttachmentsDao.searchAll(searchTemplate);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to view Audit Attachments.", baseUser);
        }
    }

    @Override
    public AuditDto create(AuditDto createTemplate) throws AqualityException {
        if(baseUser.isAuditAdmin() || isAssignedAuditor(createTemplate.getId())){
            AuditDto createdAudit = auditDao.create(createTemplate);
            if(createdAudit.getAuditors() != null){
                AuditorDto template = new AuditorDto();
                template.setAudit_id(createdAudit.getId());
                updateAuditors(createdAudit.getAuditors());
            }

            return createdAudit;
        }else{
            throw new AqualityPermissionsException("Account is not allowed to create Audits.", baseUser);
        }
    }
    public AuditCommentDto create(AuditCommentDto template) throws AqualityException {
        AuditDto audit = new AuditDto();
        audit.setId(template.getAudit_id());
        audit = get(audit).get(0);
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(audit.getProject_id()).isEditor()){
            template.setUser_id(baseUser.getId());
            return auditCommentsDao.create(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to create Audit Comments.", baseUser);
        }
    }
    public String create(boolean all, boolean xls) throws AqualityException {
        ExcelUtils excelUtils = new ExcelUtils();
        AuditDto auditDto = new AuditDto();
        AuditStatusDto auditStatusDto = new AuditStatusDto();
        auditStatusDto.setId(4);
        auditDto.setStatus(auditStatusDto);
        List<AuditDto> audits = get(auditDto);
        if(!all){
            audits = getLatestAudits(audits);
        }

        JSONArray resArray = new JSONArray();
        List<Pair<String, String>> fields = processExportArrayCreation(resArray, audits);
        try{
            if(xls){
                return excelUtils.writeXLSFile(resArray,fields, MessageFormat.format("Aquality_Tracking_{0}_Submitted_Audits_{1}", all ? "All" : "Last", formatter.format(new Date())), MessageFormat.format("{0} Submitted Audits", all ? "All" : "Last"));
            }else{
                return excelUtils.writeXLSXFile(resArray,fields, MessageFormat.format("Aquality_Tracking_{0}_Submitted_Audits_{1}", all ? "All" : "Last",formatter.format(new Date())), MessageFormat.format("{0} Submitted Audits", all ? "All" : "Last"));
            }
        } catch (Exception e){
            String full = "";
            for (StackTraceElement stack:                 e.getStackTrace()) {
                full = full.concat(stack.toString());
            }
            throw new AqualityException("Cannot create Export: " + e.getMessage() + ". Stack: " + full);
        }
    }

    public void createMultiply(List<AuditAttachmentDto> listOfAttachments) throws AqualityException {
        if((baseUser.isAuditAdmin() || isAssignedAuditor(listOfAttachments.get(0).getAudit_id())) && isAuditOpened(listOfAttachments.get(0).getAudit_id())){
            auditAttachmentsDao.createMultiply(listOfAttachments);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to create Audit Attachments.", baseUser);
        }
    }

    @Override
    public boolean delete(AuditDto template) throws AqualityException {
        if(baseUser.isAuditAdmin()){
            return auditDao.delete(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to delete Audits.", baseUser);
        }
    }
    public boolean delete(AuditAttachmentDto template) throws AqualityException {
        if(baseUser.isAuditor() ||baseUser.isAuditAdmin()){
            FileUtils fileUtils = new FileUtils();
            List<AuditAttachmentDto> attachments = get(template);
            List<String> pathes = new ArrayList<>();
            pathes.add(attachments.get(0).getPath());
            fileUtils.removeFiles(pathes);
            return auditAttachmentsDao.delete(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to delete Audit Attachments.", baseUser);
        }
    }

    public void updateAuditors(List<AuditorDto> newAuditors) throws AqualityException {
        if(baseUser.isAuditAdmin()){
            if(newAuditors.size() == 0){
                throw new AqualityException("no new auditors");
            }
            AuditorDto auditorDtoTemplate = new AuditorDto();
            auditorDtoTemplate.setAudit_id(newAuditors.get(0).getAudit_id());
            auditorDtoTemplate.setProject_id(newAuditors.get(0).getProject_id());

            List<AuditorDto> oldAuditors = auditorsDao.searchAll(auditorDtoTemplate);

            for (AuditorDto newAuditor : newAuditors) {
                AuditorDto alreadyExists = oldAuditors.stream().filter(x -> Objects.equals(x.getId(), newAuditor.getId())).findAny().orElse(null);
                if (alreadyExists != null) {
                    oldAuditors.removeIf(x -> Objects.equals(x.getId(), alreadyExists.getId()));
                } else {
                    auditorsDao.create(newAuditor);
                }
            }

            if(oldAuditors.size() > 0 ){
                for (AuditorDto oldAuditor : oldAuditors) {
                    auditorsDao.delete(oldAuditor);
                }
            }
        }else{
            throw new AqualityPermissionsException("Account is not allowed to update Audit Auditors.", baseUser);
        }
    }

    private List<AuditCommentDto> getAuditComments(AuditDto template) throws AqualityException {
        AuditCommentDto commentTemplate = new AuditCommentDto();
        commentTemplate.setAudit_id(template.getId());
        commentTemplate.setProject_id(template.getProject_id());
        return get(commentTemplate);
    }

    private List<AuditDto> completeAudits(List<AuditDto> audits) throws AqualityException {
        List<AuditDto> filledAudits = new ArrayList<>();
        List<AuditStatusDto> statuses = auditStatusDao.getAll();
        List<ServiceDto> services = serviceDao.getAll();

        for(AuditDto audit: audits){
            filledAudits.add(completeAuditDto(audit, statuses, services));
        }

        return filledAudits;
    }

    private AuditDto completeAuditDto(AuditDto audit, List<AuditStatusDto> statuses, List<ServiceDto> services) throws AqualityException {
        audit.setProject(searchForProject(audit));
        audit.setAuditors(searchForAuditors(audit));
        audit.setStatus(searchForStatus(audit,statuses));
        audit.setService(searchForService(audit,services));
        audit.setComments(getAuditComments(audit));

        return audit;
    }

    private AuditStatusDto searchForStatus(AuditDto audit, List<AuditStatusDto> statuses){
        return statuses.stream().filter(x -> x.getId().equals(audit.getStatus_id())).findAny().orElse(null);
    }

    private ServiceDto searchForService(AuditDto audit, List<ServiceDto> services){
        return services.stream().filter(x -> x.getId().equals(audit.getService_type_id())).findAny().orElse(null);
    }

    private ProjectDto searchForProject(AuditDto audit) throws AqualityException {
        ProjectDto projectTemplate = new ProjectDto();
        projectTemplate.setId(audit.getProject_id());

        return projectDao.searchAll(projectTemplate).get(0);
    }

    private List<AuditorDto> searchForAuditors(AuditDto audit) throws AqualityException {
        AuditorDto auditorTemplate = new AuditorDto();
        auditorTemplate.setProject_id(audit.getProject_id());
        auditorTemplate.setAudit_id(audit.getId());

        return auditorsDao.searchAll(auditorTemplate);
    }

    private boolean isAssignedAuditor(Integer auditId) throws AqualityException {
        if(auditId != null){
            AuditDto searchAudit = new AuditDto();
            searchAudit.setId(auditId);
            List<AuditorDto> auditors = searchForAuditors(searchAudit);
            return auditors.stream().anyMatch(x -> x.getId().equals(baseUser.getId()));
        }
        return false;
    }

    private boolean isAuditOpened(Integer auditId) throws AqualityException {
        if(auditId != null){
            AuditDto searchAudit = new AuditDto();
            searchAudit.setId(auditId);
            return get(searchAudit).get(0).getStatus_id() < 4;
        }
        return false;
    }

    private List<AuditCommentDto> completeComments(List<AuditCommentDto> comments) throws AqualityUserException {
        UserDto userTemplate = new UserDto();

        for (AuditCommentDto comment: comments){

            if(comment.getUser_id() != null){
                userTemplate.setId(comment.getUser_id());
            }else{
                userTemplate.setId(1);
            }
            comment.setAuthor(getUser(userTemplate));
        }
        return comments;
    }

    private UserDto getUser(UserDto template) throws AqualityUserException {
        try {
            return userDao.getEntityById(template);
        } catch (AqualityException e) {
            throw new AqualityUserException("Cannot get Author for the audit comment.");
        }
    }

    private List<AuditStatisticDto> completeAuditStatistic(List<AuditStatisticDto> auditStatistics) throws AqualityException {
        List<ServiceDto> services = serviceDao.getAll();
        List<ProjectDto> projects = projectDao.getAll();

        for (AuditStatisticDto auditStatistic: auditStatistics) {
            if(auditStatistic.getLast_submitted_id() != null){
                AuditorDto auditorDtoTemplate = new AuditorDto();
                auditorDtoTemplate.setAudit_id(auditStatistic.getLast_submitted_id());
                List<AuditorDto> auditors = auditorsDao.searchAll(auditorDtoTemplate);
                auditStatistic.setAuditors_last(auditors);
            }
            if(auditStatistic.getLast_created_id() != null && auditStatistic.getStatus_id() != 4){
                AuditorDto auditorDtoTemplate = new AuditorDto();
                auditorDtoTemplate.setAudit_id(auditStatistic.getLast_created_id());
                List<AuditorDto> auditors = auditorsDao.searchAll(auditorDtoTemplate);
                auditStatistic.setAuditors_next(auditors);
            }
            if(auditStatistic.getService_type_id() != null){
                Integer serviceTypeId = auditStatistic.getService_type_id();
                auditStatistic.setService(services.stream().filter(x -> Objects.equals(x.getId(), serviceTypeId)).findFirst().orElse(null));
            }
            Integer projectId = auditStatistic.getId();
            auditStatistic.setProject(projects.stream().filter(x -> Objects.equals(x.getId(), projectId)).findFirst().orElse(null));
        }
        return auditStatistics;
    }

    private List<AuditDto> getLatestAudits(List<AuditDto> audits) throws AqualityException {
        List<AuditDto> latest = new ArrayList<>();
        ProjectDao projectDao = new ProjectDao();
        List<ProjectDto> projects = projectDao.getAll();
        audits = audits.stream().filter(x-> x.getSubmitted() != null).collect(Collectors.toList());
        for (ProjectDto project : projects) {
            Integer id = project.getId();
            List<AuditDto> auditDtoList = audits.stream().filter(x -> Objects.equals(x.getProject().getId(), id)).collect(Collectors.toList());
            if (auditDtoList.size() > 0) {
                AuditDto collect = auditDtoList.stream()
                        .collect(Collectors.collectingAndThen(Collectors.reducing((AuditDto d1, AuditDto d2) -> d1.getSubmitted().getTime() > d2.getSubmitted().getTime() ? d1 : d2), Optional::get));
                latest.add(collect);
            }
        }
        return latest;
    }

    private List<Pair<String,String>> processExportArrayCreation(JSONArray resArray, List<AuditDto> audits) throws AqualityException {
        try{
            for (AuditDto auditDto : audits) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", auditDto.getId());
                jsonObject.put("project_name", auditDto.getProject().getName());
                if(auditDto.getCreated() != null) jsonObject.put("created", formatter.format(auditDto.getCreated()));
                if(auditDto.getStarted()!= null) jsonObject.put("started", formatter.format(auditDto.getStarted()));
                if(auditDto.getProgress_finished() != null) jsonObject.put("finished", formatter.format(auditDto.getProgress_finished()));
                if(auditDto.getSubmitted() != null) jsonObject.put("submitted", formatter.format(auditDto.getSubmitted()));
                jsonObject.put("result", auditDto.getResult());
                String auditorsString = "";
                List<AuditorDto> auditors = auditDto.getAuditors();
                for (int j = 0; j < auditors.size(); j++) {
                    if (j > 0) {
                        auditorsString += ", ";
                    }
                    try{
                        auditorsString += auditors.get(j).getFirst_name() + " " + auditors.get(j).getSecond_name();
                    }catch (Exception e){
                        //error on getting user info
                    }
                }
                jsonObject.put("auditors", auditorsString);
                resArray.put(jsonObject);
            }
            List<Pair<String,String>> fields = new ArrayList<>();
            fields.add(new Pair<>("Audit Id", "id"));
            fields.add(new Pair<>("Project", "project_name"));
            fields.add(new Pair<>("Date Created", "created"));
            fields.add(new Pair<>("Date Started", "started"));
            fields.add(new Pair<>("Date Finished", "finished"));
            fields.add(new Pair<>("Date Submitted", "submitted"));
            fields.add(new Pair<>("Result, %", "result"));
            fields.add(new Pair<>("Auditors", "auditors"));

            return fields;
        } catch (Exception e){
            throw new AqualityException("Cannot process create Export: " + e.getMessage() + "Stack: " + e.getStackTrace().toString());
        }
    }
}

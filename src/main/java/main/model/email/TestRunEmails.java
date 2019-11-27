package main.model.email;

import main.exceptions.AqualityException;
import main.model.db.dao.project.ProjectDao;
import main.model.db.dao.settings.EmailSettingsDao;
import main.model.dto.*;
import main.utils.AppProperties;
import main.utils.EmailUtil;
import org.json.JSONException;

import javax.mail.MessagingException;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class TestRunEmails extends Emails{
    public TestRunEmails() throws AqualityException {
        super();
    }

    private AppProperties appProperties = new AppProperties();
    public void sendTestRunResultsToTeam(TestRunDto testRun, List<UserDto> users) throws AqualityException, MessagingException, IOException, URISyntaxException {
        ProjectDto project = getProject(testRun.getProject_id());

        double failedCount = getFailedResultsCount(testRun.getTestResults());
        double passedCount = getPassedResultsCount(testRun.getTestResults());
        double totalCount = getTotalResultsCount(testRun.getTestResults());
        double notExecutedCount = getNotExecutedResultsCount(testRun.getTestResults());
        double successRate = getSuccessRate(passedCount, totalCount);

        List<TestResultDto> failed = getFailedResults(testRun.getTestResults());
        List<TestResultDto> appIssues = getAppIssues(failed);
        List<TestResultDto> otherIssues = getOtherIssues(failed);

        EmailDto email = new EmailDto();
        email.setSubject("[RP]:" + project.getName() + " - " + new DecimalFormat("##.##").format(successRate) + "% - "  + testRun.getBuild_name());
        email.setContent("<div style=\"\n" +
                "margin-left: 20px;\n" +
                "padding-left: 20px;\">\n" +
                "  <p style=\"font-family: Calibri, sans-serif;font-size: 40px;margin:0;color: #4c4c4c;\">Test Run Report</p>\n" +
                "  <div style=\"\n" +
                "                margin-top: 20px;\n" +
                "                height: 10px;\n" +
                "                text-transform: uppercase;\n" +
                "                font-family: Calibri, sans-serif;\n" +
                "                font-size: 15px;\n" +
                "                line-height: 10px;\n" +
                "                text-decoration: underline;\n" +
                "            \">\n" +
                "    <a style=\"\n" +
                "              color: #4c4c4c;\n" +
                "              font-weight: bold;\n" +
                "              font-family: Calibri, sans-serif;\n" +
                "              font-size: 15px;\n" +
                "              line-height: 10px;\" href=\"" + baseURL + "#/project/" + project.getId() + "/testrun/" + testRun.getId() + "\">View on " + appProperties.getName() + "</a>\n" +
                "  </div>\n" +
                "  <hr>\n" +
                "  <div style=\"margin: 0;font-family: Calibri, sans-serif;height:50px; padding-top:20px; color:" + getRateColor(successRate) + ";font-size: 30px;\">\n" +
                "    Success Rate: " + new DecimalFormat("##.##").format(successRate) + "%\n" +
                "  </div>\n" +
                "  <br/>\n" +
                "  <p style=\"font-family: Calibri, sans-serif;margin:0;color: #4c4c4c;font-weight: bold;\">Test Results:</p>\n" +
                "  <p style=\"margin: 0; color:#676767;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Passed | " + new DecimalFormat("##").format(passedCount) + " | " + new DecimalFormat("##.##").format(passedCount/totalCount*100) + "% </p>\n" +
                "  <p style=\"margin: 0;color:#e27070;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Failed | " + new DecimalFormat("##").format(failedCount) + " | " + new DecimalFormat("##.##").format(failedCount/totalCount*100) +"% </p>\n" +
                "  <p style=\"margin: 0;color:#676767;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Not Executed | " + new DecimalFormat("##").format(notExecutedCount) + " | " + new DecimalFormat("##.##").format(notExecutedCount/totalCount*100) +"% </p>\n" +
                "  <br/>\n" +
                "  <p style=\"font-family: Calibri, sans-serif;margin:0;color: #4c4c4c;font-weight: bold;\">Failed Test Resolutions:</p>\n" +
                "  <p style=\"margin: 0;color:#e27070;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Application Issues | " + appIssues.size() + " </p>\n" +
                "  <p style=\"margin: 0;color:#676767;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Test Issues | " + (int) failed.stream().filter(x -> x.getTest_resolution().getColor() == 2).count() + " </p>\n" +
                "  <p style=\"margin: 0;color:#676767;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Not Assigned | " + (int) failed.stream().filter(x -> x.getTest_resolution().getColor() == 3).count() + " </p>\n" +
                "  <p style=\"margin: 0;color:#676767;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Other Issues | " + otherIssues.size() + " </p>\n" +
                "  <br/>\n" +
                buildIssuesOverview(appIssues) +
                "  <br/>\n" +
                "  <hr>\n" +
                "  <br/>\n" +
                "  <p style=\"font-family: Calibri, sans-serif; font-size: 10pt; line-height: 2px;\">Best Regards,</p>\n" +
                "  <p style=\"font-family: Calibri, sans-serif; font-size: 10pt; line-height: 2px;\">" + appProperties.getName() + " Administration</p>\n" +
                "  <p>\n" +
                "    <img src=\"cid:logo\">\n" +
                "  </p>\n" +
                "  <p>\n" +
                "    <span style=\"font-family: Calibri; font-size: 7.5pt; color: #7f7f7f;\">This message is automatically generated by Notification Assistant for " + appProperties.getName() + ".\n" +
                "      <br/> If you think it was sent incorrectly, please contact your " + appProperties.getName() + " administrators.</span>\n" +
                "  </p>\n" +
                "</div>");
        email.setRecipients(getProjectMemberRecipients(users));
        if(!sendEmail(email)) {
            throw new AqualityException("Was not able to send Email!");
        }
    }

    private ProjectDto getProject(Integer id) throws AqualityException {
        ProjectDao projectDao = new ProjectDao();
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(id);
        return projectDao.getEntityById(projectDto);
    }

    private List<String> getProjectMemberRecipients(List<UserDto> users) {
        return users.stream().map(UserDto::getEmail).collect(Collectors.toList());
    }

    private double getSuccessRate(double passedCount, double totalCount) {
        return passedCount/totalCount*100;
    }

    private List<TestResultDto> getAppIssues(List<TestResultDto> testResults) {
        return  testResults.stream().filter(x -> x.getTest_resolution().getColor() == 1).collect(Collectors.toList());
    }

    private List<TestResultDto> getFailedResults(List<TestResultDto> testResults) {
        return testResults.stream().filter(x -> x.getFinal_result().getColor() != 5).collect(Collectors.toList());
    }

    private List<TestResultDto> getOtherIssues(List<TestResultDto> testResults) {
        return testResults.stream().filter(x -> x.getTest_resolution().getColor() != 1 && x.getTest_resolution().getColor() != 2 && x.getTest_resolution().getColor() != 3).collect(Collectors.toList());
    }

    private double getTotalResultsCount(List<TestResultDto> testResults) {
        return testResults.size();
    }

    private double getNotExecutedResultsCount(List<TestResultDto> testResults) {
        return (int) testResults.stream().filter(x -> x.getFinal_result().getColor() == 3 || x.getFinal_result().getColor() == 2 || x.getFinal_result().getColor() == 4).count();
    }

    private double getPassedResultsCount(List<TestResultDto> testResults) {
        return (int) testResults.stream().filter(x -> x.getFinal_result().getColor() == 5).count();
    }

    private double getFailedResultsCount(List<TestResultDto> testResults) {
        return (int) testResults.stream().filter(x -> x.getFinal_result().getColor() == 1).count();
    }

    private String getRateColor(double successRate) {
        return successRate > 85 ? "#009900" : successRate > 65 ? "#ff6601" : "#cc2100";
    }

    private StringBuilder buildIssuesOverview(List<TestResultDto> appIssues){
        StringBuilder overview = new StringBuilder(appIssues.size() > 0 ? "  <p style=\"font-family: Calibri, sans-serif;margin:0;color: #4c4c4c;font-weight: bold;\">Application Issues Overview:</p>\n" : "");

        for (TestResultDto testResult : appIssues) {
            overview
                    .append("  <p style=\"font-family: Calibri, sans-serif;margin:0;color: #4c4c4c;\">")
                    .append(testResult.getTest().getName())
                    .append(": ")
                    .append(
                            testResult.getComment() != null
                                    ? testResult.getComment()
                                    : "No comment for This Failure.")
                    .append("</p>\n");
        }

        return overview;
    }
}

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
    private AppProperties appProperties = new AppProperties();

    public void sendTestRunResultsToTeam(TestRunDto testRun, List<UserDto> users) throws IOException, JSONException, SQLException, NamingException, IllegalAccessException, InstantiationException, MessagingException, URISyntaxException, AqualityException {
        ProjectDao projectDao = new ProjectDao();
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(testRun.getProject_id());
        projectDto = projectDao.searchAll(projectDto).get(0);
        List<TestResultDto> testResults = testRun.getTestResults();
        double totalResultsNum = testResults.size();
        double passedResultsNum = (int) testResults.stream().filter(x -> x.getFinal_result().getColor() == 5).count();
        double failedResultsNum = (int) testResults.stream().filter(x -> x.getFinal_result().getColor() == 1).count();
        double notExecutedResultsNum = (int) testResults.stream().filter(x -> x.getFinal_result().getColor() == 3 || x.getFinal_result().getColor() == 2 || x.getFinal_result().getColor() == 4).count();
        List<TestResultDto> failedResults = testResults.stream().filter(x -> x.getFinal_result().getColor() != 5).collect(Collectors.toList());
        List<TestResultDto> appIssues = failedResults.stream().filter(x -> x.getTest_resolution().getColor() == 1).collect(Collectors.toList());
        List<TestResultDto> otherIssues = failedResults.stream().filter(x -> x.getTest_resolution().getColor() != 1 && x.getTest_resolution().getColor() != 2 && x.getTest_resolution().getColor() != 3).collect(Collectors.toList());
        double successRate = passedResultsNum/totalResultsNum*100;
        String rateColor = successRate > 85 ? "#009900" : successRate > 65 ? "#ff6601" : "#cc2100";
        StringBuilder overview = new StringBuilder(appIssues.size() > 0 ? "  <p style=\"font-family: Calibri, sans-serif;margin:0;color: #4c4c4c;font-weight: bold;\">Application Issues Overview:</p>\n" : "");
        for (TestResultDto testResult : appIssues) {
            overview.append("  <p style=\"font-family: Calibri, sans-serif;margin:0;color: #4c4c4c;\">").append(testResult.getTest().getName()).append(": ").append(testResult.getComment() != null ? testResult.getComment() : "No comment for This Failure.").append("</p>\n");
        }

        EmailDto email = new EmailDto();
        email.setSubject("[RP]:" + projectDto.getName() + " - " + new DecimalFormat("##.##").format(successRate) + "% - "  + testRun.getBuild_name());
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
                "              line-height: 10px;\" href=\"" + hostUri() + "#/project/" + projectDto.getId() + "/testrun/" + testRun.getId() + "\">View on " + appProperties.getName() + "</a>\n" +
                "  </div>\n" +
                "  <hr>\n" +
                "  <div style=\"margin: 0;font-family: Calibri, sans-serif;height:50px; padding-top:20px; color:" + rateColor + ";font-size: 30px;\">\n" +
                "    Success Rate: " + new DecimalFormat("##.##").format(successRate) + "%\n" +
                "  </div>\n" +
                "  <br/>\n" +
                "  <p style=\"font-family: Calibri, sans-serif;margin:0;color: #4c4c4c;font-weight: bold;\">Test Results:</p>\n" +
                "  <p style=\"margin: 0; color:#676767;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Passed | " + new DecimalFormat("##").format(passedResultsNum) + " | " + new DecimalFormat("##.##").format(passedResultsNum/totalResultsNum*100) + "% </p>\n" +
                "  <p style=\"margin: 0;color:#e27070;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Failed | " + new DecimalFormat("##").format(failedResultsNum) + " | " + new DecimalFormat("##.##").format(failedResultsNum/totalResultsNum*100) +"% </p>\n" +
                "  <p style=\"margin: 0;color:#676767;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Not Executed | " + new DecimalFormat("##").format(notExecutedResultsNum) + " | " + new DecimalFormat("##.##").format(notExecutedResultsNum/totalResultsNum*100) +"% </p>\n" +
                "  <br/>\n" +
                "  <p style=\"font-family: Calibri, sans-serif;margin:0;color: #4c4c4c;font-weight: bold;\">Failed Test Resolutions:</p>\n" +
                "  <p style=\"margin: 0;color:#e27070;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Application Issues | " + appIssues.size() + " </p>\n" +
                "  <p style=\"margin: 0;color:#676767;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Test Issues | " + (int) failedResults.stream().filter(x -> x.getTest_resolution().getColor() == 2).count() + " </p>\n" +
                "  <p style=\"margin: 0;color:#676767;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Not Assigned | " + (int) failedResults.stream().filter(x -> x.getTest_resolution().getColor() == 3).count() + " </p>\n" +
                "  <p style=\"margin: 0;color:#676767;text-transform: uppercase;font-family: Calibri, sans-serif;\">\n" +
                "    Other Issues | " + otherIssues.size() + " </p>\n" +
                "  <br/>\n" +
                overview +
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
        EmailSettingsDao emailSettingsDao = new EmailSettingsDao();
        EmailSettingsDto settings = emailSettingsDao.getAll().get(0);
        settings.setPassword(emailSettingsDao.getAdminSecret(settings.getPassword()));
        EmailUtil emailUtil = new EmailUtil(settings);
        emailUtil.SendHtmlEmail(email.getRecipients(), email.getSubject(), email.getContent());
    }

    private List<String> getProjectMemberRecipients(List<UserDto> users) {
        return users.stream().map(UserDto::getEmail).collect(Collectors.toList());
    }

}

package main.utils.integrations;

import java.io.IOException;
import java.util.List;

public interface ITestTrackingApi {

    int addTestExecTestResult(String testExecutionKey, String testKey, int statusId) throws IOException;

    void addDefectToTestResult(String issueKey, int testResultId) throws IOException;

    void addTestToTestExecution(String testExecutionKey, List<String> testKeys) throws IOException;
}

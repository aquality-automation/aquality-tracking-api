package main.model.db.imports.ImportHandlers;

import main.exceptions.AqualityException;
import main.utils.DateUtils;
import main.utils.FileUtils;
import main.utils.TestNGCucumberJsonParser.*;
import main.model.db.imports.Handler;
import main.model.dto.*;
import main.utils.TestNGCucumberJsonParser.StepDto;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

import static main.model.db.imports.ResultStatus.*;

public class Cucumber extends Handler{
        private List<FeatureDto> features;
        private List<TestDto> tests = new ArrayList<>();
        private List<TestResultDto> results = new ArrayList<>();
        private DateUtils dateUtils = new DateUtils();
        private FeatureDto currentFeature;
        private TestResultDto currentResult;
        private Integer testTime = 0;
        private Date dateCounter;
        private boolean previousWasBackground = false;
        private TestDto currentTest;

        public Cucumber(@NotNull File file, Date finishTime) throws AqualityException {
            super();
            FileUtils fileUtils = new FileUtils();
            String json = fileUtils.readFile(file.getPath());
            CucumberUtils cucumberUtils = new CucumberUtils(json);
            this.features = cucumberUtils.getFeatures();
            dateCounter = finishTime;
            parse();
        }

        private void parse() {
            handleTestRun();
        }

        private void handleTestRun() {
            testRun = new TestRunDto();
            testRun.setFinish_time(dateCounter);
            features.forEach(this::handleFeature);
            testRun.setStart_time(dateCounter);
        }

        private void handleFeature(@NotNull FeatureDto feature) {
            currentFeature = feature;
            if (feature.getElements() != null) {
                feature.getElements().forEach(this::handleScenario);
            }
        }

        private void handleScenario(@NotNull ScenarioDto scenario) {
            String type = scenario.getType()==null ? scenario.getKeyword().toLowerCase() :scenario.getType();
            if(Objects.equals(type, "background")){
                previousWasBackground = true;
                currentTest = new TestDto();
                currentResult = new TestResultDto();
                if(scenario.getSteps() != null){
                    scenario.getSteps().forEach(this::handleStep);
                }
            } else if (Objects.equals(type, "scenario")) {
                if(!previousWasBackground) {
                    currentTest = new TestDto();
                    currentResult = new TestResultDto();
                }
                previousWasBackground = false;
                String[] strings = scenario.getId().split(";;");
                currentTest.setName(String.format("%s: %s%s", currentFeature.getName(), scenario.getName(), strings.length > 1 ? String.format(": %s", strings[1]) : ""));

                currentResult.setFinish_date(dateCounter);
                if(currentResult.getFail_reason() == null) currentResult.setFail_reason("");
                if(scenario.getBefore() != null) scenario.getBefore().forEach(this::handleStep);
                scenario.getSteps().forEach(this::handleStep);
                if(scenario.getAfter() != null) scenario.getAfter().forEach(this::handleStep);
                dateCounter = dateUtils.removeMS(dateCounter, testTime);
                currentResult.setStart_date(dateCounter);
                testTime = 0;
                tests.add(currentTest);
                results.add(currentResult);
            } else {
                List<StepDto> steps = scenario.getSteps();
                for (StepDto step : steps) {
                    int dur = getStepDuration(step);
                    if (dur == 0) System.out.println("[WARNING] scenario duration is 0 for => " + scenario.getName());
                    testTime += dur;
                }
            }
        }

        private Integer getStepDuration(@NotNull StepDto step) {
            if (step.getResult() != null && step.getResult().getDuration() != null) {
                return Integer.parseInt(String.valueOf(step.getResult().getDuration() / 1000000));
            }
            return (0);
        }

        private void handleStep(@NotNull StepDto step) {
            if(step.getName() != null){
                currentTest.setBody((currentTest.getBody() == null ? "" : currentTest.getBody() + "\r\n") + step.getKeyword() + " " + step.getName());
                if(step.getRows() != null){
                    for (RowDto row: step.getRows()) {
                        currentTest.setBody((currentTest.getBody() + "\r\n  |"));
                        for (String cell : row.getCells()) {
                            currentTest.setBody((currentTest.getBody() + " " + cell + " |"));
                        }
                    }
                }
            }
            if (currentResult.getFinal_result_id() == null) {
                currentResult.setFinal_result_id(NOT_EXECUTED.getValue());
            }

            if (currentResult.getFinal_result_id() != FAILED.getValue() && currentResult.getFinal_result_id() != PENDING.getValue()) {
                currentResult.setFinal_result_id(getStatus(step.getResult().getStatus()));
            }

            if (getStatus(step.getResult().getStatus()) == FAILED.getValue() || getStatus(step.getResult().getStatus()) == PENDING.getValue()) {
                currentResult.setFail_reason(currentResult.getFail_reason() + step.getResult().getError_message());
            }

            if (getStatus(step.getResult().getStatus()) == PENDING.getValue()) {
                currentResult.setFail_reason(currentResult.getFail_reason() + step.getResult().getError_message());
            }

            if (step.getResult().getDuration() != null) {
                testTime += getStepDuration(step);
            }
        }

        private int getStatus(@NotNull String status) {
            switch (status) {
                case "passed":
                    return PASSED.getValue();
                case "failed":
                    return FAILED.getValue();
                case "skipped":
                    return PENDING.getValue();
                default:
                    return NOT_EXECUTED.getValue();
            }
        }

        public TestRunDto getTestRun() {
            return testRun;
        }

        public TestSuiteDto getTestSuite() {
            return new TestSuiteDto();
        }

        public List<TestDto> getTests() {
            return tests;
        }

        public List<TestResultDto> getTestResults() {
            return results;
        }
    }


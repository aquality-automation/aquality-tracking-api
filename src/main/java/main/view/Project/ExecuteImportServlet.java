package main.view.Project;


import main.Session;
import main.controllers.Project.SuiteController;
import main.controllers.Project.TestRunController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityParametersException;
import main.exceptions.AqualityQueryParameterException;
import main.model.db.imports.Importer;
import main.model.db.imports.TestNameNodeType;
import main.model.dto.ErrorDto;
import main.model.dto.project.ImportDto;
import main.model.dto.project.TestRunDto;
import main.model.dto.project.TestSuiteDto;
import main.utils.FileUtils;
import main.utils.PathUtils;
import main.view.BaseServlet;
import main.view.IPost;
import main.model.db.imports.ImportTypes;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@WebServlet("/import")
@MultipartConfig
public class ExecuteImportServlet extends BaseServlet implements IPost {

    private static final int IMPORT_THREAD_POOL_SIZE = 10;
    private static final int IMPORT_QUEUE_CAPACITY = 100;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 60;

    private final ThreadPoolExecutor importExecutor = createImportExecutor();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        String uploadDir = null;
        Importer importer = null;
        boolean handedOver = false;
        String notStartedReason = "Import was not started";
        try {
            ImportRequestParams params = readParameters(req);
            Session session = createSession(req);

            SuiteController suiteController = session.controllerFactory.getHandler(new TestSuiteDto());
            TestRunController testRunController = session.controllerFactory.getHandler(new TestRunDto());

            uploadDir = PathUtils.createPathToBin("temp", params.projectId.toString(), UUID.randomUUID().toString());
            List<String> filePaths = doUpload(req, resp, uploadDir);

            importer = session.getImporter(
                    filePaths,
                    prepareTestRun(params, suiteController, testRunController),
                    getStringQueryParameter(req, ImportParams.pattern.name()),
                    params.format,
                    getTestNameNodeType(req, params.format),
                    params.singleTestRun
            );

            // validates settings and creates "in progress" records; nothing is created if validation fails
            List<ImportDto> acceptedImports = importer.startImport();

            // serialize before the background import starts to modify the same DTOs
            String responseBody = mapper.serialize(acceptedImports);
            importExecutor.execute(new ImportTask(importer, uploadDir));
            handedOver = true;

            setEncoding(resp);
            setJSONContentType(resp);
            resp.getWriter().write(responseBody);
        } catch (RejectedExecutionException e) {
            notStartedReason = "Import was not started: queue is full or server is shutting down";
            log.warning(notStartedReason);
            respondServiceUnavailable(resp);
        } catch (Exception e) {
            notStartedReason = "Import was not started: " + describe(e);
            handleException(resp, e);
        } finally {
            if (!handedOver) {
                // startImport() can fail in the middle and leave already created records "in progress"
                if (importer != null) {
                    importer.failUnfinishedImports(notStartedReason);
                }
                cleanup(uploadDir);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        importExecutor.shutdown();
        try {
            if (!importExecutor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.warning("Import tasks were not finished in " + SHUTDOWN_TIMEOUT_SECONDS + " seconds. Cancelling them.");
                abortQueuedTasks(importExecutor.shutdownNow());
            }
        } catch (InterruptedException e) {
            abortQueuedTasks(importExecutor.shutdownNow());
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }

    private static ThreadPoolExecutor createImportExecutor() {
        ClassLoader appClassLoader = ExecuteImportServlet.class.getClassLoader();
        AtomicInteger threadCounter = new AtomicInteger();
        // Workers use JNDI (DataSource lookup), so they must run with the web application class loader.
        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable, "import-worker-" + threadCounter.incrementAndGet());
            thread.setDaemon(true);
            thread.setContextClassLoader(appClassLoader);
            return thread;
        };

        return new ThreadPoolExecutor(
                IMPORT_THREAD_POOL_SIZE,
                IMPORT_THREAD_POOL_SIZE,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(IMPORT_QUEUE_CAPACITY),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }

    private void abortQueuedTasks(List<Runnable> queuedTasks) {
        for (Runnable task : queuedTasks) {
            if (task instanceof ImportTask) {
                ((ImportTask) task).abort("Import was cancelled because the server is shutting down.");
            }
        }
    }

    private void respondServiceUnavailable(HttpServletResponse resp) {
        try {
            resp.setStatus(503);
            setResponseBody(resp, new ErrorDto("Import queue is full or server is shutting down. Try again later."));
        } catch (AqualityException e) {
            handleException(resp, e);
        }
    }

    private ImportRequestParams readParameters(HttpServletRequest req) throws AqualityException {
        ImportRequestParams params = new ImportRequestParams();
        params.singleTestRun = getBooleanQueryParameter(req, ImportParams.singleTestRun.name());
        params.format = getStringQueryParameter(req, ImportParams.format.name());
        params.importToken = getStringQueryParameter(req, ImportParams.importToken.name());
        params.projectId = getIntegerQueryParameter(req, ImportParams.projectId.name());
        params.buildName = getStringQueryParameter(req, ImportParams.buildName.name());
        params.author = getStringQueryParameter(req, ImportParams.author.name());
        params.suiteName = getStringQueryParameter(req, ImportParams.suite.name());
        params.testRunId = getIntegerQueryParameter(req, ImportParams.testRunId.name());
        params.addToLastTestRun = getBooleanQueryParameter(req, ImportParams.addToLastTestRun.name());
        params.environment = getStringQueryParameter(req, ImportParams.environment.name());
        params.cilink = getStringQueryParameter(req, ImportParams.cilink.name());
        params.debug = getBooleanQueryParameter(req, ImportParams.debug.name());
        validateRequest(params);
        return params;
    }

    private TestRunDto prepareTestRun(ImportRequestParams params, SuiteController suiteController, TestRunController testRunController) throws AqualityException {
        TestSuiteDto testSuiteTemplate = new TestSuiteDto();
        testSuiteTemplate.setName(params.suiteName);
        testSuiteTemplate.setProject_id(params.projectId);

        TestRunDto testRunTemplate = new TestRunDto();
        testRunTemplate.setProject_id(params.projectId);
        testRunTemplate.setBuild_name(params.buildName);
        testRunTemplate.setCi_build(params.cilink);
        testRunTemplate.setAuthor(params.author);
        testRunTemplate.setExecution_environment(params.environment);
        testRunTemplate.setTest_suite(testSuiteTemplate);
        testRunTemplate.setId(getTestRunId(params, suiteController, testRunController));
        testRunTemplate.setDebug(params.debug ? 1 : 0);

        return testRunTemplate;
    }

    private Integer getTestRunId(ImportRequestParams params, SuiteController suiteController, TestRunController testRunController) throws AqualityException {
        if (params.testRunId != null) {
            return params.testRunId;
        }

        if (params.addToLastTestRun) {
            return testRunController.getLastSuiteTestRun(suiteController.get(params.suiteName, params.projectId).getId(), params.projectId).getId();
        }

        return null;
    }

    private void validateRequest(ImportRequestParams params) throws AqualityException {
        if (params.importToken != null) {
            throw new AqualityQueryParameterException("Import Token is deprecated. Follow instructions on the API Token page.");
        }

        if (params.singleTestRun) {
            if (params.projectId == null || params.format == null || params.buildName == null || params.author == null) {
                throw new AqualityQueryParameterException("ProjectId or/and Format or/and Author or/and BuildName parameters are missed.");
            }
        } else {
            if (params.projectId == null || params.format == null) {
                throw new AqualityQueryParameterException("ProjectId or/and Format parameters are missed.");
            }
        }

        validateFormat(params.format);

        if (params.format.equals(ImportTypes.MSTest.name()) || params.format.equals(ImportTypes.Cucumber.name())) {
            if (params.suiteName == null) {
                throw new AqualityQueryParameterException("Suite parameter is missed.");
            }
        }
    }

    private void validateFormat(String format) throws AqualityParametersException {
        try {
            ImportTypes.valueOf(format);
        } catch (IllegalArgumentException e) {
            throw new AqualityParametersException("Import type '%s' is not supported. Allowed values: %s",
                    format, Arrays.toString(ImportTypes.values()));
        }
    }

    private List<String> doUpload(HttpServletRequest req, HttpServletResponse resp, String uploadDir) throws ServletException, IOException {
        FileUtils fileUtils = new FileUtils();
        return fileUtils.doUpload(req, resp, uploadDir);
    }

    private void cleanup(String uploadDir) {
        new FileUtils().removeDirectory(uploadDir);
    }

    private TestNameNodeType getTestNameNodeType(HttpServletRequest req, String format) throws AqualityParametersException {
        String testNameKey = getStringQueryParameter(req, ImportParams.testNameKey.name());

        if (testNameKey == null) {
            if (format.equals(ImportTypes.NUnit_v3.name())) {
                return TestNameNodeType.featureNameTestName;
            }
            return null;
        }

        try {
            return TestNameNodeType.valueOf(testNameKey);
        } catch (IllegalArgumentException e) {
            throw new AqualityParametersException("TestNameKey parameter you provide is not correct. The correct values are:'testName', 'className', 'descriptionNode', 'featureNameTestName'.");
        }
    }

    private static String describe(Throwable throwable) {
        return throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getSimpleName();
    }

    /**
     * Background import. Always leaves the import records in a final state and removes uploaded files.
     */
    private final class ImportTask implements Runnable {
        private final Importer importer;
        private final String uploadDir;

        private ImportTask(Importer importer, String uploadDir) {
            this.importer = importer;
            this.uploadDir = uploadDir;
        }

        @Override
        public void run() {
            try {
                importer.executeImport();
            } catch (Throwable throwable) {
                log.log(Level.SEVERE, "Error during background import execution", throwable);
                // regular failures are already stored by Importer, this covers everything else (including Errors)
                importer.failUnfinishedImports(describe(throwable));
                if (throwable instanceof Error) {
                    throw (Error) throwable;
                }
            } finally {
                cleanup(uploadDir);
            }
        }

        private void abort(String reason) {
            importer.failUnfinishedImports(reason);
            cleanup(uploadDir);
        }
    }

    private static class ImportRequestParams {
        Integer projectId;
        String buildName;
        String author;
        String suiteName;
        Integer testRunId;
        Boolean addToLastTestRun;
        Boolean debug;
        String environment;
        String cilink;
        Boolean singleTestRun;
        String format;
        String importToken;
    }
}

-- Uncomment line below to run in MySQL Workbench
-- DELIMITER ;;

SET CHARACTER SET 'utf8';;

CREATE PROCEDURE `INSERT_MILESTONE`(
    IN milestone_id varchar(10),
    IN milestone_name VARCHAR(500),
    IN milestone_project_id VARCHAR(500)
)
BEGIN
	INSERT INTO union_reporting.milestones (id, name, project_id)
		VALUES (
        IF(milestone_id='',null,milestone_id),
		milestone_name,
		milestone_project_id)
        ON DUPLICATE KEY UPDATE
        name = milestone_name,
        project_id = milestone_project_id
        ;
END ;;


CREATE PROCEDURE `INSERT_PROJECT`(
    IN project_id varchar(10),
    IN projectName VARCHAR(100),
    IN user_id VARCHAR(11)
)
BEGIN
	DECLARE EXIT HANDLER FOR SQLSTATE '42000'
		SELECT 'No Permissions';
	IF (1!=(SELECT admin From union_reporting.users WHERE id = user_id) AND 1!=(SELECT manager From union_reporting.users WHERE id = user_id)) THEN
		CALL raise_error;
	END IF;

    IF NOT EXISTS (SELECT * From union_reporting.projects WHERE name = projectName)
    THEN INSERT INTO union_reporting.projects (id, name) VALUES (IF(project_id='',null,project_id),projectName)
		ON DUPLICATE KEY UPDATE
		name = projectName;
	ELSE
		signal sqlstate '23515';
    END IF;
END ;;


CREATE PROCEDURE `INSERT_PROJECT_USER`(
    IN ur_user_id varchar(10),
    IN ur_project_id VARCHAR(10),
    IN ur_viewer VARCHAR(1),
    IN ur_admin VARCHAR(1),
    IN ur_manager VARCHAR(1),
    IN ur_engineer VARCHAR(1)
)
BEGIN
INSERT INTO `union_reporting`.`user_roles` (`user_id`, `project_id`, `admin`, `manager`, `engineer`, `viewer`) VALUES (
        ur_user_id,
        ur_project_id,
        IF(ur_admin='',0,ur_admin),
        IF(ur_manager='',0,ur_manager),
        IF(ur_engineer='',0,ur_engineer),
        IF(ur_viewer='',0,ur_viewer)
        )
        ON DUPLICATE KEY UPDATE
        admin = IF(ur_admin = '', admin, ur_admin),
        manager = IF(ur_manager = '', manager, ur_manager),
        engineer = IF(ur_engineer = '', engineer, ur_engineer),
        viewer = IF(ur_viewer = '', viewer, ur_viewer)
        ;
END ;;


CREATE PROCEDURE `INSERT_RESULT_RESOLUTION`(
    IN request_user_id VARCHAR(11),
    IN resolutionName VARCHAR(500),
    IN resolutionId VARCHAR(11),
    IN resolutionProjectId VARCHAR(11),
    IN resolutionColorId VARCHAR(1)
)
BEGIN

	IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1 OR users.admin = 1
        OR (union_reporting.user_roles.project_id = resolutionProjectId
			AND (user_roles.admin = 1 OR user_roles.manager = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	INSERT INTO `union_reporting`.`result_resolution` (`id`, `name`, `color`, `project_id`) VALUES (
        If(resolutionId='',null,resolutionId),
        resolutionName,
        resolutionColorId,
        IF(resolutionProjectId='',null,resolutionProjectId)
        )
        ON DUPLICATE KEY UPDATE
        name = IF(resolutionName='',name,resolutionName),
        project_id = IF(resolutionProjectId='',project_id,resolutionProjectId),
        color = IF(resolutionColorId='',color,resolutionColorId)
        ;
END ;;


CREATE PROCEDURE `INSERT_SESSION`(
    IN session_user_id VARCHAR(100),
    IN session_session_code VARCHAR(100)
)
BEGIN
	INSERT INTO union_reporting.user_sessions (user_id, session_code)
    VALUES (
    session_user_id,
    session_session_code)
    ;

    UPDATE union_reporting.users
    SET last_session_id = (select max(us.id) from union_reporting.user_sessions as us where us.user_id=session_user_id)
    WHERE id = session_user_id;

END ;;


CREATE PROCEDURE `INSERT_SUITE`(
	IN request_user_id VARCHAR(10),
	IN suite_id VARCHAR(10),
	IN suiteName VARCHAR(500),
	IN projectId VARCHAR(10))
BEGIN
	IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = projectId
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

    IF EXISTS (SELECT * FROM union_reporting.test_suites WHERE project_id=projectId AND name = suiteName)
    THEN
		signal sqlstate '23505';
	END IF;

	INSERT INTO union_reporting.test_suites (id, name, project_id) VALUES (
    IF(suite_id='',null,suite_id),
    suiteName,
    projectId
    )
    ON DUPLICATE KEY UPDATE
    name = suiteName,
    project_id = projectId
    ;
END ;;


CREATE PROCEDURE `INSERT_TEST`(
    IN request_user_id VARCHAR(10),
    IN test_id VARCHAR(10),
    IN testName VARCHAR(500),
    IN testBody VARCHAR(5000),
    IN test_suite_id VARCHAR(10),
    IN test_project_id VARCHAR(10),
    IN test_manual_duration varchar(5),
    IN test_developer VARCHAR(10)
)
BEGIN
	IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = test_project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	INSERT INTO union_reporting.tests (id, name, body, test_suite_id, project_id, manual_duration, developer)
    VALUES (
    IF(test_id ='',null,test_id),
    testName,
    IF(testBody = '', null, testBody),
    IF(test_suite_id = '', null, test_suite_id),
    test_project_id,
    IF(test_manual_duration = '' OR null, null, test_manual_duration),
    IF(test_developer='',null,test_developer)
    )
    ON DUPLICATE KEY UPDATE
    name=IF(testName = '' OR null, name, testName),
    body = IF(testBody = '', null, testBody),
    test_suite_id = IF(test_suite_id = '', null, test_suite_id),
    project_id = test_project_id,
    manual_duration = IF(test_manual_duration = '' OR null, IF(manual_duration = null OR '', null, manual_duration), test_manual_duration),
    developer = IF(test_developer='',developer,test_developer)
    ;
END ;;


CREATE PROCEDURE `INSERT_TESTRESULT`(
    IN request_user_id VARCHAR(10),
    IN testresult_project_id VARCHAR(10),
    IN result_id VARCHAR(10),
    IN result_test_id VARCHAR(500),
    IN result_final_result_id VARCHAR(500),
    IN result_comment text,
    IN result_test_run_id VARCHAR(500),
    IN result_test_resolution_id VARCHAR(10),
    IN result_test_log longtext,
    IN result_test_debug VARCHAR(1),
    IN result_finishtime VARCHAR(500),
    IN result_starttime VARCHAR(500),
    IN result_finalresult_updated VARCHAR(500),
    IN result_finalresult_fail_reason mediumtext,
    IN result_test_assignee VARCHAR(10)
)
BEGIN
	IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = testresult_project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

INSERT INTO union_reporting.test_results (project_id, id, test_id, final_result_id, comment, test_run_id, test_resolution_id, log, debug, start_date, finish_date, final_result_updated, fail_reason, assignee)
    VALUES (
    testresult_project_id,
    IF(result_id='', null, result_id),
    result_test_id,
    IF(result_final_result_id='',1,result_final_result_id),
    IF(result_comment = '', null, result_comment),
    IF(result_test_run_id = '', null, result_test_run_id),
    IF(result_test_resolution_id = '', 1, result_test_resolution_id),
    IF(result_test_log = '', null, result_test_log),
    IF(result_test_debug = '', 0, result_test_debug),
    If(result_starttime = '', null, FROM_UNIXTIME(result_starttime)),
    If(result_finishtime = '', null, FROM_UNIXTIME(result_finishtime)),
    NOW(),
    IF(result_finalresult_fail_reason = '', null, result_finalresult_fail_reason),
    IF(result_test_assignee = '', null, result_test_assignee))
    ON DUPLICATE KEY UPDATE
    test_id=IF(result_test_id = '',test_id,result_test_id),
    final_result_id = IF(result_final_result_id = '', final_result_id, result_final_result_id),
    comment = IF(result_comment = '', IF(comment = null OR '', null, comment), result_comment),
    test_resolution_id = IF(final_result_id = 2, 1, IF(result_test_resolution_id='', test_resolution_id, result_test_resolution_id)),
    log = IF(result_test_log = '', log, result_test_log),
    debug = IF(result_test_debug = '', debug, result_test_debug),
    start_date = If(result_starttime = '', start_date, FROM_UNIXTIME(result_starttime)),
    finish_date = If(result_finishtime = '', finish_date, FROM_UNIXTIME(result_finishtime)),
    final_result_updated = IF(result_final_result_id = final_result_id AND result_finalresult_updated = '', NOW(), IF(result_finalresult_updated = '',final_result_updated, FROM_UNIXTIME(result_finalresult_updated))),
    fail_reason = IF(result_finalresult_fail_reason = '', fail_reason, IF(result_finalresult_fail_reason = '$blank', '', result_finalresult_fail_reason)),
    assignee = IF(result_test_assignee = '', assignee, result_test_assignee)
    ;
END ;;


CREATE PROCEDURE `INSERT_TESTRUN`(
    IN request_user_id VARCHAR(10),
    IN testrun_id VARCHAR(10),
    IN testrun_build_name VARCHAR(500),
    IN testrun_starttime VARCHAR(500),
    IN testrun_execution_environment VARCHAR(500),
    IN testrun_milestone_id VARCHAR(10),
    IN testrun_test_suite_id VARCHAR(10),
    IN testrun_project_id VARCHAR(10),
    IN testrun_finishtime VARCHAR(500),
    IN testrun_author VARCHAR(500),
    IN testrun_debug VARCHAR(1)
)
BEGIN
	IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = testrun_project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	INSERT INTO union_reporting.test_runs (
    id,
    build_name,
    start_time,
    milestone_id,
    test_suite_id,
    project_id,
    execution_environment,
    finish_time,
    author,
    debug)
    VALUES (
    IF(testrun_id = '', null,testrun_id),
    testrun_build_name,
    if(testrun_starttime = '', FROM_UNIXTIME('0'), FROM_UNIXTIME(testrun_starttime)),
    IF(testrun_milestone_id = '', null, testrun_milestone_id),
    IF(testrun_test_suite_id = '', null, testrun_test_suite_id),
    IF(testrun_project_id = '', 0, testrun_project_id),
    IF(testrun_execution_environment = '', null, testrun_execution_environment),
    If(testrun_finishtime = '', null, FROM_UNIXTIME(testrun_finishtime)),
    IF(testrun_author = '', null, testrun_author),
    IF(testrun_debug = '', '0', testrun_debug)
    )
    ON DUPLICATE KEY UPDATE
    build_name = IF(testrun_build_name = '', build_name, testrun_build_name),
    start_time = IF(testrun_starttime = '', start_time, FROM_UNIXTIME(testrun_starttime)),
    milestone_id = IF(testrun_milestone_id = '', milestone_id, testrun_milestone_id),
    test_suite_id = IF(testrun_test_suite_id = '', test_suite_id, testrun_test_suite_id),
    project_id = IF(testrun_project_id = '',project_id, testrun_project_id ),
    execution_environment = IF(testrun_execution_environment = '', execution_environment, testrun_execution_environment),
    finish_time = If(testrun_finishtime = '', finish_time, FROM_UNIXTIME(testrun_finishtime)),
    author = IF(testrun_author = '', author, testrun_author),
    debug = IF(testrun_debug = '', debug, testrun_debug);

    SET @result_id = IF(testrun_id = '', LAST_INSERT_ID(), testrun_id);

    SELECT * from union_reporting.test_runs where id = @result_id;
END ;;


CREATE PROCEDURE `INSERT_USER`(
    IN user_id varchar(10),
    IN user_user_name VARCHAR(100),
    IN user_first_name VARCHAR(100),
    IN user_second_name VARCHAR(100),
    IN user_password VARCHAR(100),
    IN user_admin VARCHAR(1),
    IN user_manager VARCHAR(1),
    IN user_email VARCHAR(150),
    OUT id INT(10)
)
BEGIN
	INSERT INTO union_reporting.users (id, first_name, second_name, user_name, pass, admin, manager, email)
		VALUES (
        IF(user_id='',null,user_id),
        user_first_name,
        user_second_name,
		user_user_name,
        user_password,
        IF(user_admin='',0,user_admin),
        IF(user_manager='',0,user_manager),
        user_email
        )
        ON DUPLICATE KEY UPDATE
        user_name = IF(user_user_name = '', user_name, user_user_name),
        first_name = IF(user_first_name = '', first_name, user_first_name),
        second_name = IF(user_second_name = '', second_name, user_second_name),
        pass = IF(user_password = '', pass, user_password),
        admin = IF(user_admin = '', admin, user_admin),
        manager = IF(user_manager = '', manager, user_manager),
        email = IF(user_email='', email, user_email)
        ;

        SET id = IF(user_id = '', LAST_INSERT_ID(), user_id);
END ;;


CREATE PROCEDURE `REMOVE_MILESTONE`(
    IN request_user_id VARCHAR(10),
    IN milestone_id varchar(10),
    IN project_id VARCHAR(10)
)
BEGIN
IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	DELETE FROM union_reporting.milestones
	WHERE id=milestone_id;
END ;;


CREATE PROCEDURE `REMOVE_PROJECT`(
    IN request_user_id VARCHAR(10),
    IN project_id VARCHAR(10)
)
BEGIN
IF NOT EXISTS (SELECT * FROM union_reporting.users
        where id = request_user_id AND admin = 1)
	THEN
		signal sqlstate '23515';
	END IF;

	DELETE FROM union_reporting.projects
	WHERE id=project_id;
END ;;


CREATE PROCEDURE `REMOVE_PROJECT_USER`(
    IN ur_user_id varchar(10),
    IN ur_project_id varchar(10)
)
BEGIN
	DELETE FROM union_reporting.user_roles
	WHERE user_id=ur_user_id AND project_id=ur_project_id;
END ;;


CREATE PROCEDURE `REMOVE_RESULT_RESOLUTION`(
    IN request_user_id VARCHAR(11),
    IN resolutionProjectId  VARCHAR(11),
    IN resolutionId VARCHAR(11)
)
BEGIN
	IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1 OR users.admin = 1
        OR (union_reporting.user_roles.project_id = resolutionProjectId
			AND (user_roles.admin = 1 OR user_roles.manager = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	DELETE FROM union_reporting.result_resolution
	WHERE id=resolutionId;
END ;;


CREATE PROCEDURE `REMOVE_TEST`(
    IN request_user_id VARCHAR(10),
    IN test_id varchar(10),
    IN project_id VARCHAR(10)
)
BEGIN
	IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	DELETE FROM union_reporting.tests
	WHERE id=test_id;
END ;;


CREATE PROCEDURE `REMOVE_TESTSUITE`(
    IN request_user_id VARCHAR(10),
    IN testsuite_id varchar(10),
    IN project_id VARCHAR(10)
)
BEGIN
	IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	DELETE FROM union_reporting.test_suites
	WHERE id=testsuite_id;
END ;;


CREATE PROCEDURE `REMOVE_TEST_RESULT`(
    IN request_user_id VARCHAR(10),
    IN testresult_id varchar(10),
    IN project_id VARCHAR(10)
)
BEGIN
IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	DELETE FROM union_reporting.test_results
	WHERE id=testresult_id;
END ;;


CREATE PROCEDURE `REMOVE_TEST_RUN`(
    IN request_user_id VARCHAR(10),
    IN testrun_id varchar(10),
    IN project_id VARCHAR(10)
)
BEGIN
	IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	DELETE FROM union_reporting.test_runs
	WHERE id=testrun_id;
END ;;


CREATE PROCEDURE `REMOVE_USER`(
    IN user_id varchar(10)
)
BEGIN
	DELETE FROM union_reporting.users
	WHERE id=user_id;
END ;;


CREATE PROCEDURE `SELECT_FINAL_RESULT`(
    IN finalResultName VARCHAR(500),
    IN finalResultId VARCHAR(11)
)
BEGIN
	SELECT *
    FROM union_reporting.final_results
    WHERE (finalResultName = '' OR name = finalResultName)
    AND (finalResultId = '' OR id = finalResultId);
END ;;


CREATE PROCEDURE `SELECT_MILESTONE`(
    IN milestone_id VARCHAR(500),
    IN milestone_name VARCHAR(500),
    IN milestone_project_id VARCHAR(5000)
)
BEGIN
	SELECT *
	FROM union_reporting.milestones
	WHERE (milestone_id= '' OR id=milestone_id)
	AND (milestone_name = '' OR name=milestone_name)
    AND (milestone_project_id = '' OR project_id=milestone_project_id)
    ;
END ;;


CREATE PROCEDURE `SELECT_PERMISSIONS`(
    IN userId VARCHAR(11),
    IN projectId VARCHAR(11)
)
BEGIN
	SELECT admin, manager, engineer, viewer FROM union_reporting.user_roles
    WHERE (userId ='' OR userId=user_id)
    AND (projectId ='' OR projectId=project_id);
END ;;


CREATE PROCEDURE `SELECT_PROJECT`(
    IN projectName VARCHAR(500),
    IN projectId VARCHAR(10),
    IN userId VARCHAR(11)
)
BEGIN
	SELECT DISTINCT proj.*
    FROM union_reporting.projects as proj
    LEFT JOIN union_reporting.user_roles as us_r ON proj.id = us_r.project_id
    WHERE (projectId = '' OR id=projectId)
    AND (projectName = '' OR name=projectName)
    AND (userId = '' OR user_id=userId OR 1=(SELECT admin From union_reporting.users WHERE id = userId) OR 1=(SELECT manager From union_reporting.users WHERE id = userId));
END ;;


CREATE PROCEDURE `SELECT_PROJECT_USERS`(
    IN projectId VARCHAR(11),
    IN userId VARCHAR(11)
)
BEGIN
	SELECT *
	FROM union_reporting.user_roles
	WHERE (projectId= '' OR project_id=projectId) AND (userId='' OR user_id = userId)
    ;
END ;;


CREATE PROCEDURE `SELECT_RESULTS_BY_FAIL_REASON_CONTAINS`(
    IN request_user_id VARCHAR(10),
    IN testresult_project_id VARCHAR(10),
    IN resultFailReason VARCHAR(500)
)
BEGIN
	IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = testresult_project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1 OR user_roles.viewer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	SELECT
    res.id,
    res.test_id,
    res.final_result_id,
    res.comment,
    res.test_run_id,
    fin.name as result_name,
    res.test_resolution_id,
    resol.name as resolution_name,
    tests.name as test_name,
    debug,
    log,
    res.updated,
    start_date,
    finish_date,
    final_result_updated,
    fail_reason,
    first_name,
    second_name,
    assignee

	FROM union_reporting.test_results as res
    LEFT JOIN union_reporting.final_results as fin ON res.final_result_id = fin.id
    LEFT JOIN union_reporting.result_resolution as resol ON res.test_resolution_id = resol.id
    LEFT JOIN union_reporting.tests as tests ON res.test_id = tests.id
    LEFT JOIN union_reporting.users as users ON res.assignee = users.id

    WHERE REPLACE(REPLACE(fail_reason, '\r', ' '), '\n', ' ') LIKE CONCAT('%',REPLACE(REPLACE(resultFailReason, '\r', ' '), '\n', ' '),'%');
END ;;


CREATE PROCEDURE `SELECT_RESULT_RESOLUTION`(
    IN resolution_projectId VARCHAR(11),
    IN resolution_id VARCHAR(11),
    IN resolution_name VARCHAR(100),
    IN resolution_color VARCHAR(11)
)
BEGIN
	SELECT *
    FROM union_reporting.result_resolution
    WHERE ((resolution_ProjectId = '' OR project_id = resolution_ProjectId) OR project_id is null)
    AND (resolution_id = '' OR id = resolution_id)
    AND (resolution_name = '' OR name = resolution_name)
    AND (resolution_color = '' OR color = resolution_color);
END ;;


CREATE PROCEDURE `SELECT_SESSION`(
    IN session_user_id VARCHAR(100),
    IN session_id VARCHAR(100)
)
BEGIN

	SELECT *
    FROM union_reporting.user_sessions
    WHERE (session_user_id= '' OR user_id=session_user_id)
	AND (session_id= '' OR session_code=session_id);
END ;;


CREATE PROCEDURE `SELECT_SUITE`(
    IN request_user_id VARCHAR(10),
    IN suiteName VARCHAR(500),
    IN suiteId VARCHAR(10),
    IN projectId VARCHAR(10)
)
BEGIN
    IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = projectId
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1 OR user_roles.viewer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	SELECT *
    FROM union_reporting.test_suites
    WHERE (suiteName = '' OR name = suiteName)
	AND (suiteId = '' OR id = suiteId)
    AND project_id = projectId;
END ;;


CREATE PROCEDURE `SELECT_TEST`(
    IN request_user_id VARCHAR(10),
    IN testId VARCHAR(10),
    IN testName VARCHAR(500),
    IN testBody VARCHAR(5000),
    IN test_test_suite_id VARCHAR(10),
    IN test_project_id VARCHAR(10),
    IN test_developer VARCHAR(10)
)
BEGIN
	IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = test_project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1 OR user_roles.viewer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	SELECT *
	FROM union_reporting.tests
	WHERE (testId = '' OR id=testId)
	AND (testName = '' OR name=testName)
    AND (testBody = '' OR body=testBody)
    AND (test_test_suite_id = '' OR test_suite_id=test_test_suite_id)
    AND (test_project_id = '' OR project_id=test_project_id)
    AND (test_developer = '' OR developer=test_developer)
    ;
END ;;


CREATE PROCEDURE `SELECT_TESTRESULT`(
    IN request_user_id VARCHAR(10),
    IN testresult_project_id VARCHAR(10),
    IN testresult_Id VARCHAR(10),
    IN testresult_test_id VARCHAR(10),
    IN testresult_final_result_id VARCHAR(10),
    IN testresult_comment VARCHAR(5000),
    IN testresult_test_run_id VARCHAR(10),
    IN testresult_resolution_id VARCHAR(10),
    IN testresult_debug VARCHAR(1),
    IN resultFailReason VARCHAR(500)
)
BEGIN
    IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = testresult_project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1 OR user_roles.viewer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	SELECT * FROM union_reporting.test_results as res
	WHERE (testresult_Id = '' OR id=testresult_Id)
	AND (testresult_test_id = '' OR test_id=testresult_test_id)
    AND (testresult_final_result_id = '' OR final_result_id=testresult_final_result_id)
    AND (testresult_comment = '' OR comment=testresult_comment)
    AND (testresult_test_run_id = '' OR test_run_id=testresult_test_run_id)
    AND (testresult_resolution_id = '' OR test_resolution_id=testresult_resolution_id)
    AND (testresult_debug = '' OR debug=testresult_debug)
    AND (resultFailReason = '' OR REPLACE(REPLACE(fail_reason, '\r', ' '), '\n', ' ') LIKE CONCAT('%',REPLACE(REPLACE(resultFailReason, '\r', ' '), '\n', ' '),'%'))
    ORDER BY start_date DESC
    ;
END ;;


CREATE PROCEDURE `SELECT_TESTRUN`(
    IN request_user_id VARCHAR(10),
    IN testrun_Id VARCHAR(10),
    IN testrun_build_name VARCHAR(500),
    IN testrun_milestone_id VARCHAR(10),
    IN testrun_test_suite_id VARCHAR(10),
    IN testrun_project_id VARCHAR(10),
    IN testrun_execution_environment VARCHAR(500),
    IN testrun_debug VARCHAR(1)
)
BEGIN
    IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = testrun_project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1 OR user_roles.viewer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

	SELECT
    runs.id,
    build_name,
    start_time,
    milestone_id,
    mil.name as milestone_name,
    test_suite_id,
    suite.name as testsuite_name,
    runs.project_id,
    execution_environment,
    finish_time,
    updated,
    author,
    debug

	FROM union_reporting.test_runs as runs
    LEFT JOIN union_reporting.milestones as mil ON runs.milestone_id = mil.id
    LEFT JOIN union_reporting.test_suites as suite ON runs.test_suite_id = suite.id

	WHERE (testrun_Id= '' OR runs.id=testrun_Id)
	AND (testrun_build_name = '' OR build_name=testrun_build_name)
    AND (testrun_milestone_id = '' OR milestone_id=testrun_milestone_id OR IF(testrun_milestone_id=0, milestone_id IS NULL, testrun_milestone_id = ''))
    AND (testrun_test_suite_id = '' OR test_suite_id=testrun_test_suite_id)
    AND (testrun_project_id = '' OR runs.project_id=testrun_project_id)
    AND (testrun_execution_environment = '' OR execution_environment=testrun_execution_environment)
    AND (testrun_debug = '' OR runs.debug=testrun_debug)
    ORDER BY finish_time DESC
    ;
END ;;


CREATE PROCEDURE `SELECT_TESTRUN_STATS`(
    IN request_user_id VARCHAR(10),
    IN testrun_Id VARCHAR(10),
    IN testrun_build_name VARCHAR(500),
    IN testrun_milestone_id VARCHAR(10),
    IN testrun_test_suite_id VARCHAR(10),
    IN testrun_project_id VARCHAR(10),
    IN testrun_execution_environment VARCHAR(500),
    IN testrun_debug VARCHAR(1)
)
BEGIN
IF NOT EXISTS (SELECT * FROM union_reporting.user_roles
		right join union_reporting.users on union_reporting.user_roles.user_id = id
        where id = request_user_id
        AND (users.manager = 1
        OR (union_reporting.user_roles.project_id = testrun_project_id
			AND (user_roles.admin = 1 OR user_roles.manager = 1 OR user_roles.engineer = 1 OR user_roles.viewer = 1))))
	THEN
		signal sqlstate '23515';
	END IF;

    SELECT
    trn.id,
    trn.start_time,
    trn.finish_time,
    sum(frs.color = 1) as failed,
    sum(frs.color = 5) as passed,
    sum(frs.color = 3) as not_executed,
    sum(frs.color = 2) as in_progress,
    sum(frs.color = 4) as pending,
    sum(frs.color != 0) as total,
    sum(rr.color = 1) as app_issue,
    sum(rr.color = 2) as warning,
    sum(rr.color = 3 AND frs.color = 1) as not_assigned,
    sum(rr.color = 4 OR rr.color = 5) as other


    from union_reporting.test_runs as trn
    right join union_reporting.test_results as trs on trn.id=test_run_id
    left join union_reporting.final_results as frs on trs.final_result_id = frs.id
    left join union_reporting.result_resolution as rr on trs.test_resolution_id = rr.id

    WHERE (testrun_Id= '' OR trn.id=testrun_Id)
        AND (testrun_build_name = '' OR build_name=testrun_build_name)
        AND (testrun_milestone_id = '' OR milestone_id=testrun_milestone_id)
        AND (testrun_test_suite_id = '' OR test_suite_id=testrun_test_suite_id)
        AND (testrun_project_id = '' OR trn.project_id=testrun_project_id)
        AND (testrun_execution_environment = '' OR execution_environment=testrun_execution_environment)
        AND (testrun_debug = '' OR trn.debug=testrun_debug)

    GROUP BY trn.id
    ORDER BY finish_time DESC;

END ;;


CREATE PROCEDURE `SELECT_USERS`(
    IN userId VARCHAR(10),
    IN userFirstName VARCHAR(100),
    IN userLastName VARCHAR(100),
    IN userName VARCHAR(100)
)
BEGIN
	SELECT
		users.id as id,
		first_name,
		second_name,
        user_name,
        updated,
        pass,
        session_code,
        sessions.created as session_created,
        admin,
        manager,
        email
	FROM union_reporting.users as users
    LEFT JOIN union_reporting.user_sessions as sessions
    ON users.last_session_id = sessions.id
	WHERE (userId= '' OR users.id=userId)
	AND (userFirstName= '' OR first_name=userFirstName)
    AND (userLastName = '' OR second_name=userLastName)
    AND (userName = '' OR user_name=userName)
    ;
END ;;

-- DELIMITER ;

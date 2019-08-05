-- Insert test data for 2 demo projects.
-- Running this file is not obligatory.

SET CHARACTER SET 'utf8';

USE union_reporting;


-- Insert Demo Project 1 data
INSERT INTO union_reporting.projects (id, name)
VALUES (1, 'Demo Project 1');

INSERT INTO union_reporting.user_roles (user_id, project_id, admin, manager, engineer, viewer)
VALUES (1, 1, 1, 1, 1, 1);

INSERT INTO union_reporting.milestones (id, name, project_id)
VALUES (1, 'Demo Milestone', 1);

INSERT INTO union_reporting.test_suites (id, name, project_id)
VALUES (1, 'All tests', 1);

INSERT INTO union_reporting.test_runs (id, build_name, start_time, milestone_id, test_suite_id, project_id, execution_environment, finish_time, author, debug)
VALUES (1, 'Demo build', '2017-12-01 00:00:01', 1, 1, 1, 'Staging', '2017-12-01 01:00:01', 'Peter', 0);

INSERT INTO union_reporting.tests (id, name, body, test_suite_id, project_id, manual_duration, developer)
VALUES (1, 'Demo test 1', NULL, 1, 1, NULL, NULL),
       (2, 'Demo test 2', NULL, 1, 1, NULL, NULL);

INSERT INTO union_reporting.test_results (id, test_id, final_result_id, comment, test_run_id, test_resolution_id, updated, debug, log, start_date, finish_date, final_result_updated, fail_reason, assignee, project_id)
VALUES (1, 1, 1, 'Test result comment', 1, 1, NULL, 0, 'Test log...', '2017-12-01 00:00:01', '2017-12-01 00:01:01', NULL, 1, 1, 1),
       (2, 2, 2, 'Test result comment', 1, 2, NULL, 0, 'Test log...', '2017-12-01 00:01:01', '2017-12-01 00:02:01', NULL, 2, 1, 1);

-- Insert Demo Project 2 data
INSERT INTO union_reporting.projects (id, name)
VALUES (2, 'Demo Project 2');

INSERT INTO union_reporting.user_roles (user_id, project_id, admin, manager, engineer, viewer)
VALUES (1, 2, 1, 1, 1, 1);

INSERT INTO union_reporting.milestones (id, name, project_id)
VALUES (2, 'Demo Milestone', 2);

INSERT INTO union_reporting.test_suites (id, name, project_id)
VALUES (2, 'All tests', 2);

INSERT INTO union_reporting.test_runs (id, build_name, start_time, milestone_id, test_suite_id, project_id, execution_environment, finish_time, author, debug)
VALUES (2, 'Demo build', '2017-12-01 00:00:01', 2, 2, 2, 'Staging', '2017-12-01 01:00:01', 'Peter', 0);

INSERT INTO union_reporting.tests (id, name, body, test_suite_id, project_id, manual_duration, developer)
VALUES (3, 'Demo test 1', NULL, 2, 2, NULL, NULL),
       (4, 'Demo test 2', NULL, 2, 2, NULL, NULL);

INSERT INTO union_reporting.test_results (id, test_id, final_result_id, comment, test_run_id, test_resolution_id, updated, debug, log, start_date, finish_date, final_result_updated, fail_reason, assignee, project_id)
VALUES (3, 3, 1, 'Test result comment', 2, 1, NULL, 0, 'Test log...', '2017-12-01 00:02:01', '2017-12-01 00:03:01', NULL, 1, 1, 2),
       (4, 4, 2, 'Test result comment', 2, 2, NULL, 0, 'Test log...', '2017-12-01 00:03:01', '2017-12-01 00:04:01', NULL, 2, 1, 2);

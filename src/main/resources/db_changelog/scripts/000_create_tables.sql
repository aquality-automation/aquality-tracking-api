SET CHARACTER SET 'utf8';

-- Create tables
CREATE TABLE `final_results` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `color` int(1) DEFAULT '3',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
);


CREATE TABLE `result_resolution` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `color` int(1) NOT NULL DEFAULT '0',
  `project_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `project_name_un` (`name`,`project_id`)
);


CREATE TABLE `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
);


CREATE TABLE `projects` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `project_name_u` (`name`)
);


CREATE TABLE `milestones` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `project_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `project_idx` (`project_id`),
  CONSTRAINT `milestone_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);


CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(150) NOT NULL,
  `second_name` varchar(150) NOT NULL,
  `user_name` varchar(150) NOT NULL,
  `email` varchar(150) NOT NULL,
  `updated` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `pass` varchar(150) NOT NULL,
  `last_session_id` int(11) DEFAULT NULL,
  `admin` int(1) NOT NULL DEFAULT '0',
  `manager` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name_UNIQUE` (`user_name`)
);


CREATE TABLE `user_roles` (
  `user_id` int(11) NOT NULL,
  `project_id` int(11) NOT NULL,
  `admin` int(1) NOT NULL DEFAULT '0',
  `manager` int(1) NOT NULL DEFAULT '0',
  `engineer` int(1) NOT NULL DEFAULT '0',
  `viewer` int(1) NOT NULL DEFAULT '0',
  UNIQUE KEY `uq_by` (`user_id`,`project_id`),
  KEY `user_roles_user_idx` (`user_id`),
  KEY `user_roles_role_idx` (`project_id`),
  CONSTRAINT `user_roles_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_roles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE `user_sessions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `session_code` varchar(100) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`user_id`)
);


CREATE TABLE `test_suites` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) NOT NULL,
  `project_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `id_idx` (`project_id`),
  CONSTRAINT `test_suite_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);


CREATE TABLE `test_runs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `build_name` varchar(100) NOT NULL,
  `start_time` datetime NOT NULL,
  `milestone_id` int(11) DEFAULT NULL,
  `test_suite_id` int(11) DEFAULT NULL,
  `project_id` int(11) NOT NULL,
  `execution_environment` varchar(100) DEFAULT NULL,
  `finish_time` datetime DEFAULT NULL,
  `updated` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `author` varchar(100) DEFAULT NULL,
  `debug` int(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `milestone_id_idx` (`milestone_id`),
  KEY `test_suite_id_idx` (`test_suite_id`),
  KEY `test_run_project_idx` (`project_id`),
  CONSTRAINT `test_run_milestone` FOREIGN KEY (`milestone_id`) REFERENCES `milestones` (`id`) ON DELETE SET NULL ON UPDATE NO ACTION,
  CONSTRAINT `test_run_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `test_run_test_suite` FOREIGN KEY (`test_suite_id`) REFERENCES `test_suites` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);


CREATE TABLE `tests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) NOT NULL,
  `body` longtext,
  `test_suite_id` int(11) DEFAULT NULL,
  `project_id` int(11) NOT NULL,
  `manual_duration` varchar(5) DEFAULT NULL,
  `developer` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  CONSTRAINT `test_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
);


CREATE TABLE `test_results` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `test_id` int(11) NOT NULL,
  `final_result_id` int(11) NOT NULL,
  `comment` mediumtext,
  `test_run_id` int(11) DEFAULT NULL,
  `test_resolution_id` int(11) DEFAULT '1',
  `updated` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `debug` int(3) NOT NULL DEFAULT '0',
  `log` longtext,
  `start_date` datetime DEFAULT NULL,
  `finish_date` datetime DEFAULT NULL,
  `final_result_updated` datetime DEFAULT NULL,
  `fail_reason` mediumtext,
  `assignee` int(11) DEFAULT NULL,
  `project_id` int(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `final_result_id_idx` (`final_result_id`),
  KEY `test_results_test_idx` (`test_id`),
  KEY `test_results_test_run_idx` (`test_run_id`),
  KEY `test_result_assignee_idx` (`assignee`),
  KEY `test_results_project_idx` (`project_id`),
  CONSTRAINT `test_result_assignee` FOREIGN KEY (`assignee`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `test_results_final_result` FOREIGN KEY (`final_result_id`) REFERENCES `final_results` (`id`),
  CONSTRAINT `test_results_test` FOREIGN KEY (`test_id`) REFERENCES `tests` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `test_results_test_run` FOREIGN KEY (`test_run_id`) REFERENCES `test_runs` (`id`) ON DELETE SET NULL ON UPDATE NO ACTION
);

-- Insert common data
INSERT INTO union_reporting.roles (id, name)
VALUES (1, 'Admin'),
       (2, 'Manager'),
       (3, 'Viewer');

INSERT INTO union_reporting.final_results (id, name, color)
VALUES (1, 'Failed', 1),
       (2, 'Passed', 5),
       (3, 'Not Executed', 3),
       (4, 'In Progress', 2),
       (5, 'Pending', 4);

INSERT INTO union_reporting.result_resolution (id, name, color, project_id)
VALUES (1, 'Not Assigned', 3, NULL),
       (2, 'Application Issue', 1, NULL),
       (3, 'Environment Issue', 2, NULL),
       (4, 'Test Issue', 2, NULL),
       (5, 'Test Design Issue', 2, NULL);

INSERT INTO union_reporting.users (id, first_name, second_name, user_name, email, updated, pass, last_session_id, admin, manager)
VALUES (1, 'peter', 'parker', 'p.parker', 'p.parker@test.com', NULL, 'e10adc3949ba59abbe56e057f20f883e', NULL, 1, 1);

# CHANGELOG

## 1.5.4 (2025-02-20)
Features:
  - Added support for NUnit v3 results xml date "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX" format

## 1.5.3 (2024-08-11)
Bugfixes:
  - Improved performance of internal api endpoints: /testresult, /test

## 1.5.2 (2024-04-12)
Bugfixes:
  - Fixed 500 (Entity is locked) issue
  - Fixed SQL connections leak
  - Improved /public/test/create-or-update, /public/test/result/start, /public/test/result/finish performance
  - Fixed INSERT_TEST_RESULT final_result_id comparison

## 1.5.1 (2023-11-15)
Bugfixes:
  - Fixed an issue Invalid authorisation returns 500 status code

## 1.5.0 (2023-07-28)
  - Added `/issues/assign` endpoint, which allows to check previously created Test Runs and Test Results and assign Issues to them if there's a RegEx match

## 1.4.1 (2023-06-27)
Bugfixes:
  - Fixed an issue with the 'SELECT_TEST_STATS' procedure referencing nonexisting column

## 1.3.0 (2021-02-26)
  - Added feature that allows to link aquality entities (tests, issues and test runs) with 3rd party systems like Jira, Xray, TestRail and etc.
 In this version only Jira and Xray support has been added.
  - Improved and generified work with controllers

## 1.1.1 (2020-12-15)
Bugfixes:
  - Added handling of non utf-8 body in the request

## 1.1.0 (2020-12-02)
Features:
  - Added option to enable/disable 'STARTTLS' in the email settings
  - Refactored email utilities class

## 1.0.4 (2020-11-02)

Bugfixes:
  - Changed testRun start and finish time calculation for Cucumber version 4.3.0+ -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/125)

## 1.0.3 (2020-08-27)

Features: 
  - API: fixes in test result attachments -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/102)

## 1.0.2 (2020-08-18)

Changes: 
  - Added new import type - Maven Surefire, it will be used for JUnit/TestNG Java projects

## 1.0.1

Bugfixes:
  - API: Incorrect Error status code processing JUnit5 -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/112)
  - API: Incorrect Skipped status code processing JUnit5 -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/111)

## 1.0.0

Features:
  - Migrate to Angular 9 -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/101)
  - Add Execution environment column to Test History table -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/107)

Bugfixes:
  - [API] /api/public/test/create-or-update does not update list of suites -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/105)
  - External issue link is incorrect on Issues table -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/108)
  - Import is blocked when invalid regular expression was saved for issue -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/103)

## 0.3.10 (2020-05-17)

Features:
  - Update Tests to use Admin API to create preconditions -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/100)

Bugfixes:
  - Test run View Page performance is bad -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/99)
  - Change Title on swagger page -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/98)
  - Swagger: Authorization is impossible due to localhost Servers selection -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/85)

## 0.3.9 (2020-04-21)

Features:
  - Added unit tests for import handlers -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/81)
  - Added unit tests for checking DAO's -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/80)
  - Migrate results info to issues -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/82)
  - Migrate predefined Resolutions to issues -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/83)
  - Issue -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/69)
  - Test Run: Auto Fill Results on issue creation -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/77)
  - Issues List Page -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/75)
  - add Failed test cases column(count) to Test Runs table -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/92)

## 0.3.8 (2020-03-11)

Features:
  - Update Swagger with Statistic endpoints -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/76)
  - Mark import as failed when import is failed -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/79)

Bugfixes:
  - Import was finished with Error! You are trying to edit entity which is locked -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/44)
  - Incorrect "Duration" count if import several *.json's -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/37)

## 0.3.7 (2020-03-02)

Features:
  - Improve Test Run and Test List page performance -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/71)
  - Exclude Debug results from last results column -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/70)
  - Milestone: Add selected Suites to milestone -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/63)
  - Milestone: Add Due Date to Milestone. -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/61)
  - Milestone: Add possibility to Close Milestone. -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/62)

## 0.3.6 (2020-02-24)

Features:
  - Test Run view: Add stability indicator -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/60)
  - Provide public API -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/56)

## 0.3.5 (2020-02-15)

Features:
  - Milestone View: Add functionality that displays the latest results of the tests from the test runs -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/11)
  - List of predefined Resolutions -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/26)
  - Import: Mark import as debug  -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/47)
  - Add JUnit 5 support -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/33)
  
Bugfixes:
  - Add ALLOW_UNQUOTED_CONTROL_CHARS for mapper -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/45)
  - Cannot remove milestone from TestRun -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/50)

## 0.3.4 (2019-12-10)

Features:
  - Remove Customers Feature -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/25)
  - Add possibility to sync testsuites according to Not Executed results -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/32)

Bugfixes:
  - It should not be possible to delete Customer that assigned to the projects -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/9)
  - It is not possible to import several test run results one by one -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/35)

## 0.3.3 (2019-11-16)

Features:
  - Rename Import Token to API Token -> [View Issue](https://github.com/aquality-automation/aquality-tracking-ui/issues/23)
  - Steps Feature -> [View Issue](https://github.com/aquality-automation/aquality-tracking-ui/issues/46)
  - Regex Search by Errors for Import -> [View Issue](https://github.com/aquality-automation/aquality-tracking/issues/17)

Bugfixes:
  - Local Manager can see Local Permissions page -> [View Issue](https://github.com/aquality-automation/aquality-tracking-ui/issues/22)

## 0.3.2 (2019-09-10)

Features:

  - Import: NUnit V3 add possibility to import with fullname test name  -> [View Issue](https://github.com/aquality-automation/aquality-tracking-ui/issues/27)

Bugfixes:

  - Import: Import to latest test Run not working when importing more than one file  -> [View Issue](https://github.com/aquality-automation/aquality-tracking-ui/issues/29)

## 0.3.1 (2019-08-30)

Features:

  - Add ability to set Default Email Pattern
  - Add regex search to Results Searcher

Bugfixes:

  - Fix Fail Reason Search

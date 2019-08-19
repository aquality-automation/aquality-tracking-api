# Aquality Tracking API

## Getting started

1. Install Tomcat 9+
1. Install MySQL 5.7
1. Set password `anyPassword` for MySQL user `root`
1. Run maven `package` phase to create war file and run database migration
1. Deploy created war file to local tomcat

### Package command template

```
package 
-DfrontURI={web site url} 
-Ddb.username={db username} 
-Ddb.password={db password} 
-Ddb.host={db host name or ip in docker} 
-Ddb.publicPort={db port on host} 
-Ddb.port={db port in docker}
-Ddb.publicHost={db host or ip}
```


## Database migration

DB migration is implemented using Liquibase tool. It is started automatically when you execute maven `package` goal.

It's also possible to run only db migration manually:

- For local DB:
    ```
    mvn resources:resources liquibase:update
    ```

- For remote DB (need to specify build profile and DB password):
    ```
    mvn resources:resources liquibase:update -P staging -Ddb.password=XXX
    ```

Latest migration can be rolled back this way (will work only if rollback logic was implemented for changeset):
```
mvn resources:resources liquibase:rollback -Dliquibase.rollbackCount=1
```

To mark all migrations as applied without actually executing them use command below (can be useful on prod or staging):
```
mvn resources:resources liquibase:changelogSync -P staging -Ddb.password=XXX
```

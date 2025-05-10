SCH-PINCÉR
===

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=kir-dev_sch-pincer&metric=alert_status)](https://sonarcloud.io/dashboard?id=kir-dev_sch-pincer)


## Setup dev env

- Open the project (for IDEA users: don't use import, use open)
- Create a database with this mysql query: `CREATE DATABASE schpincer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
- Create your own application-local.properties to set the properties or use `docker` flag and apply the environment variables from `application-docker.properties`
- You will need an authsch application that points to the `http://127.0.0.1:8080/loggedin` url. You can register one for yourself at https://auth.sch.bme.hu/ Don't forget to set the `authsch.client-identifier` (20 chars long) and `authsch.client-key` (80 chars long) properties.
- Add the following profiles to running configuration: `dev,test,local` (or `docker` is you use env variables)
- You can now start the app and open: http://127.0.0.1:8080 (when it's started)
- Enable `test` profile to insert demo data
- To test you can grant yourself admin privileges from the db (see below)

### A sample application-local.properties

Location: `src/main/resources/config/application-local.properties` (Git will automatically ignore it, so you can put your personal properties there)

```
# Don't forget to enable local profile!

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/schpincer?useSSL=false&useUnicode=yes&characterEncoding=utf8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
spring.datasource.username=username
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true # change to false if you want

authsch.client-identifier=20 chars length
authsch.client-key=80 chars length

# never / always / on-trace-param
#server.error.include-stacktrace=never

schpincer.external=./test/external/

spring.servlet.multipart.max-file-size=2000KB
spring.servlet.multipart.max-request-size=2000KB
```

### Grant admin privilege via SQL queries

- Open mysql console
- `use schpincer;`
- Make sure you've logged in at least once
- Use eg. this query to grant yourself sysadmin, or provide the `schincer.sysadmin=YOUR_AUTHSCH_UUID` to any of the properties files

```sql
`UPDATE `users` SET `sysadmin`=1 WHERE `email`='YOUR_EMAIL';`
```

- Relog (log out and in)
- You will now see the two administration buttons

## How to contribute

Coming soon.

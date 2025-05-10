# SCH-PINCÉR

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=kir-dev_sch-pincer&metric=alert_status)](https://sonarcloud.io/dashboard?id=kir-dev_sch-pincer)


## Running/debugging locally

- Open the repository in IntelliJ IDEA
- Create a database with this mysql query: `CREATE DATABASE schpincer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
- Go to https://auth.sch.bme.hu/console/create, set *Átirányítási cím* to
  `http://localhost:8080/login/oauth2/code/authsch`, then use the created credentials in the following config file:
- Create `src/main/resources/config/application-local.properties` using the below config and update it with your AuthSCH and MySQL credentials:

```properties
spring.security.oauth2.client.registration.authsch.client-id=AUTHSCHKLIENSAZONOSITO
spring.security.oauth2.client.registration.authsch.client-secret=AUTHSCHKLIENSKULCS

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/schpincer?useSSL=false&useUnicode=yes&characterEncoding=utf8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
spring.datasource.username=schpincer
spring.datasource.password=mypassword

# use "create" to reset the database when restarting the server  
spring.jpa.hibernate.ddl-auto=update

# *Enable debug output* in the IntelliJ *Run/Debug Configuration* under
# *Modify options* also prints SQL commands that were run.  
#spring.jpa.show-sql=true
```

- Ensure that the `local` profile is set in *Run/Debug Configurations*  / *SchpincerApp* / *Active profiles:*
- Press *Debug 'SchpincerApp'*. Open http://localhost:8080
- Enable the `test` profile to insert demo data into the database

### Grant admin privilege via SQL queries

- Open mysql console
- `use schpincer;`
- Make sure you've logged in at least once
- Use eg. this query to grant yourself sysadmin, or set `schincer.sysadmin=YOUR_AUTHSCH_UUID` in your `application-local.properties`

```sql
`UPDATE `users` SET `sysadmin`=1 WHERE `email`='YOUR_EMAIL';`  
```

- Relog (log out and in)
- You will now see the two administration buttons  
  
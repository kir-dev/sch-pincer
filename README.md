# SCH-PINCÉR

Food ordering website for the Schönherz Dormitory

## Running/debugging locally

- Open the repository in IntelliJ IDEA
- Create a database with this query: `CREATE DATABASE schpincer;`
- Go to https://auth.sch.bme.hu/console/create, set *Átirányítási cím* to
  `http://localhost:8080/login/oauth2/code/authsch`, then use the created credentials in the following config file:
- Create `src/main/resources/config/application-local.properties` using the below config and update it with your AuthSCH credentials:

```properties
spring.security.oauth2.client.registration.authsch.client-id=AUTHSCHKLIENSAZONOSITO
spring.security.oauth2.client.registration.authsch.client-secret=AUTHSCHKLIENSKULCS

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

- Open postgres console and select your database
- Make sure you've logged in at least once
- Use eg. this query to grant yourself sysadmin, or set `schincer.sysadmin=YOUR_AUTHSCH_UUID` in your `application-local.properties`

```sql
UPDATE `users` SET `sysadmin`=1 WHERE `email`='YOUR_EMAIL';
```

- Relog (log out and in)
- You will now see the two administration buttons  

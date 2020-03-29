SCH-PINCÉR
===


## Setup dev env

- Import as a maven project
- Create a database with this mysql query: `CREATE DATABASE webschop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
- Create your own application-local.properties to set the properties or use `docker` flag and apply the environment variables from `application-docker.properties`
- You will need an authsch application that points to the `http://127.0.0.1:8080/loggedin` url. You can register one for yourself at https://auth.sch.bme.hu/ Don't forget to set the `authsch.client-identifier` (20 chars long) and `authsch.client-key` (80 chars long) properties.
- Add the following profiles to running configuration: `db-mysql,demo,dev,local` (or `docker` is you use env variables)
- You can now start the app

## How to contribute

Coming soon.

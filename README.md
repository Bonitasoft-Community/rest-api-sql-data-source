REST API extension with SQL dataSource
======================================


API EXTENSION SETUP
--------------------

clone the project

```shell
git clone git@github.com:Bonitasoft-Community/rest-api-sql-data-source.git
cd rest-api-sql-data-source
```

adjust bonita.version properties in [pom.xml](pom.xml) if necessary. 
 
run maven build

```shell
mvn clean install
```


DATABASE SETUP
--------------

create a PostgreSQL database named "demo" owned by user "bonita" with password "bpm".

run script [sql/createExample.sql](sql/createExample.sql) to create table and populate sample data


DATASOURCE SETUP
----------------

create new dataSource setup with a standart tomcat H2 bundle

edit file conf/Catalina/localhost/bonita.xml and add new data source:

```xml
<Resource name="demoDS"
              auth="Container"
              type="javax.sql.DataSource"
              maxActive="17"
              minIdle="5"
              maxWait="10000"
              initialSize="3"
              validationQuery="SELECT 1"
              validationInterval="30000"
              removeAbandoned="true"
              logAbandoned="true"
              username="bonita"
              password="bpm"
              driverClassName="org.postgresql.Driver"
              url="jdbc:postgresql://localhost:5432/demo"/>
```

In folder lib/bonita add [PostgreSQL jdbc driver](https://jdbc.postgresql.org/download.html) jar

Restart tomcat

DEPLOY RESOURCES IN PORTAL
--------------------------

log in with an administrator profile in portal

1. add resource [target/rest-api-sql-datasource.zip](target/rest-api-sql-datasource.zip) 
2. add page [page/page-apiExtensionDatasourceViewer.zip](page/page-apiExtensionDatasourceViewer.zip)
3. import living application [livingApplication/Application_Data.xml](livingApplication/Application_Data.xml)

RUN IT
------

login as with a User profile in portal to have new permissions in user's session

navigate to:
http://localhost:8080/bonita/apps/sqlDemo/example/




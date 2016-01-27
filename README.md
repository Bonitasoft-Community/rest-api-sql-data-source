REST API extension with SQL dataSource
======================================

1 [Build instructions](#1-build-instructions)<br>
1.1 Setup your IDE<br>
1.2 Retrieving the project from GitHub<br>
1.3 Building the project<br>

2 [Installation instructions](#2-installation instructions)<br>
2.1 Setting up the database<br>
2.2 Setting up the datasource<br>
2.3 Deploying the resources in Bonita BPM Portal<br>
2.4 Running the sample application<br>


# 1 Build instructions
If you want to modify and build the project, you may follow these instructions.
Otherwise, you can work directly with the binaries available in the [releases](../../releases).


## 1.1 Setup your IDE
These instructions apply to a Eclipse Mars (4.5)

- Go in Help > Install New Software...
- Work with `org.codehaus.groovy.eclipse.site - http://dist.springsource.org/snapshot/GRECLIPSE/e4.5`
- Select following features to install:
	- Extra compiler 1.8 
	- Groovy eclipse
	- m2e configurator 
- Install and restart Eclipse
- Go to Help > Eclipse Marketplace...
- Search for Spock Plugin and install it


## 1.2 Retrieving the project from GitHub
Clone the project

```shell
git clone git@github.com:Bonitasoft-Community/rest-api-sql-data-source.git
cd rest-api-sql-data-source
```

## 1.3 Building the project
In case you don't have required dependencies in your local Maven repository, they are available in `/lib` folder.
To install them:
```shell
mvn install:install-file -Dfile=lib/console-server-7.0.1-classes.jar -DgroupId=org.bonitasoft.console -DartifactId=console-server -Dversion=7.0.1 -Dpackaging=jar -Dclassifier=classes
mvn install:install-file -Dfile=lib/console-common-7.0.1.jar -DgroupId=org.bonitasoft.console -DartifactId=console-common -Dversion=7.0.1 -Dpackaging=jar
```   

- Build the project with Maven:
```shell
mvn clean install
```
- Retrieve the generated REST API extension zip: `target/rest-api-sql-datasource.zip`


# 2 Installation instructions

## 2.1 Setting up the database

- Create a PostgreSQL database named `demo` owned by user `bonita` with password `bpm`.
- Run the [sql/createExample.sql](sql/createExample.sql) script to create the table and populate it with sample data.


## 2.2 Setting up the datasource

These instructions apply to a standart Tomcat H2 bundle.

- Edit the `conf/Catalina/localhost/bonita.xml` file and add new data source:

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

- Add the [PostgreSQL jdbc driver](https://jdbc.postgresql.org/download.html) jar in the `lib/bonita` folder
- Restart Tomcat


## 2.3 Deploying the resources in Bonita BPM Portal

- Log in with a user with Administrator profile in Bonita BPM Portal
- Import the REST API Extension (the one you built or retrieved from the [releases](../../releases))
- Import the sample viewer page [page/page-apiExtensionDatasourceViewer.zip](page/page-apiExtensionDatasourceViewer.zip)
- Import the living application [livingApplication/Application_Data.xml](livingApplication/Application_Data.xml)


## 2.4 Running the sample application

- Log in with a user with Administrator profile in Bonita BPM Portal
- Navigate to this URL (port number may vary):
[http://localhost:8080/bonita/apps/sqlDemo/example/](http://localhost:8080/bonita/apps/sqlDemo/example/)

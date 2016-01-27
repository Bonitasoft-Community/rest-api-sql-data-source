REST API extension with SQL dataSource
======================================

# Build instructions
If you want to modify and build the project, you may follow these instructions.
Otherwise, you can work directly with the binaries available in the [releases](../../releases).

## Setup your IDE
These instruction apply to a Eclipse Mars (4.5)

- Go in Help > Install New Software...
- Work with `org.codehaus.groovy.eclipse.site - http://dist.springsource.org/snapshot/GRECLIPSE/e4.5`
- Select following features to install:
	- Extra compiler 1.8 
	- Groovy eclipse
	- m2e configurator 
- Install and restart Eclipse
- Go to Help > Eclipse Marketplace...
- Search for Spock Plugin and install it

## Retrieving the project from GitHub
Clone the project

```shell
git clone git@github.com:Bonitasoft-Community/rest-api-sql-data-source.git
cd rest-api-sql-data-source
```

## Building the project
In case you don't have required dependencies in your local Maven repository, they are available in `/lib` folder.
To install them:

```shell
mvn install:install-file -Dfile=lib/console-server-7.0.1-classes.jar -DgroupId=org.bonitasoft.console -DartifactId=console-server -Dversion=7.0.1 -Dpackaging=jar -Dclassifier=classes
mvn install:install-file -Dfile=lib/console-common-7.0.1.jar -DgroupId=org.bonitasoft.console -DartifactId=console-common -Dversion=7.0.1 -Dpackaging=jar
```   
 
Build the project with Maven:

```shell
mvn clean install
```

# Installation instructions

## Setting up the database

1. Create a PostgreSQL database named `demo` owned by user `bonita` with password `bpm`.
2. Run the [sql/createExample.sql](sql/createExample.sql) script to create the table and populate it with sample data.

## Setting up the datasource

These instruction apply to a standart Tomcat H2 bundle.

1. Edit the `conf/Catalina/localhost/bonita.xml` file and add new data source:

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

2. In folder `lib/bonita` add the [PostgreSQL jdbc driver](https://jdbc.postgresql.org/download.html) jar
3. Restart Tomcat

## Deploy the resources in Bonita BPM Portal

1. Log in with a user with Administrator profile in Bonita BPM Portal
2. Add previously build REST API Extension located in `target/rest-api-sql-datasource.zip`
3. Add page [page/page-apiExtensionDatasourceViewer.zip](page/page-apiExtensionDatasourceViewer.zip)
4. Import living application [livingApplication/Application_Data.xml](livingApplication/Application_Data.xml)

## Running the sample application

1. Log in with a user with Administrator profile in Bonita BPM Portal
2. Navigate to this URL (port number may vary):
[http://localhost:8080/bonita/apps/sqlDemo/example/](http://localhost:8080/bonita/apps/sqlDemo/example/)



